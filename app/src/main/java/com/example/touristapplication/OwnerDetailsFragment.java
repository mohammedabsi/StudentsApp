package com.example.touristapplication;

import static java.util.Locale.getDefault;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.touristapplication.databinding.FragmentOwnerDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.ObjIntConsumer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener  {

    FragmentOwnerDetailsBinding binding ;
    FirebaseFirestore firestore ;

    String day = null;
    int hour, minute;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OwnerDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerDetailsFragment newInstance(String param1, String param2) {
        OwnerDetailsFragment fragment = new OwnerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOwnerDetailsBinding.inflate(inflater , container , false);

        firestore = FirebaseFirestore.getInstance();

        String contact = getArguments().getString("contact");
        String placename = getArguments().getString("place name");
        String desc = getArguments().getString("desc");
        String postId = getArguments().getString("id");
        String cat = getArguments().getString("cat");


        binding.owndescName.setText(desc);
        binding.ownplaceName.setText(placename);
        binding.owncontactName.setText(contact);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner 1
        binding.ownfromdayspinner.setAdapter(adapter);
        binding.ownfromdayspinner.setOnItemSelectedListener(this);
        //spinner 2
        binding.owntodayspinner.setAdapter(adapter);
        binding.owntodayspinner.setOnItemSelectedListener(this);

        //time picker 1
        binding.ownstartTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker2(view);
            }


        });    //time picker 2
        binding.ownendTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker2(view);
            }


        });

        binding.owndeletePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle(R.string.delete_title);

                builder1.setMessage(R.string.delete_msg);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                firestore.collection(cat).document(postId).delete();
                                getParentFragmentManager().beginTransaction().replace(R.id.ownerframe_layout, new OwnerPostsFragment()).commit();

                            }
                        });

                builder1.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        binding.ownupdatePlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                updateData();
                Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_SHORT).show();

            }
        });


        return binding.getRoot();
    }

    private void updateData() {
        String id = getArguments().getString("id");
        String cat = getArguments().getString("cat");
        String st_day = binding.ownfromdayspinner.getSelectedItem().toString();
        String end_day = binding.owntodayspinner.getSelectedItem().toString();
        String fromtime = binding.ownstartTimebtn.getText().toString().trim();
        String totime = binding.ownendTimebtn.getText().toString().trim();

        firestore.collection(cat).document(id).update("place_name",binding.ownplaceName.getText().toString());
        firestore.collection(cat).document(id).update("descName",binding.owndescName.getText().toString());
        firestore.collection(cat).document(id).update("contact",binding.owncontactName.getText().toString());
        firestore.collection(cat).document(id).update("st_day",st_day);
        firestore.collection(cat).document(id).update("end_day",end_day);
        firestore.collection(cat).document(id).update("fromtime",fromtime);
        firestore.collection(cat).document(id).update("totime",totime);


    }

    public void popTimePicker2(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                switch (view.getId()) {
                    case R.id.ownstart_timebtn:
                        if (hour >= 12) {

                            binding.ownstartTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " pm");
                        } else {
                            binding.ownstartTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " am");

                        }
                        break;
                    case R.id.ownend_timebtn:
                        if (hour >= 12) {

                            binding.ownendTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " pm");
                        } else {
                            binding.ownendTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " am");

                        }
                }


            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), /*style,*/ onTimeSetListener2, hour, minute, true);


        timePickerDialog.show();
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        day = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}