package com.example.touristapplication;

import static java.util.Locale.getDefault;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.touristapplication.databinding.FragmentCreateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ArrayList<Uri> imagesUris;
    private static final int PICK_IMAGES_CODES = 0;
    private static final String TAG = "com.touristapplication";
    private int position = 0;
    private int upload_count = 0;

    String day = null;
    int hour, minute;
    FragmentCreateBinding binding;

    String x = null;
    ArrayList<String> tags = new ArrayList<>();


    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // String currentemail = user.getEmail();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateFragment newInstance(String param1, String param2) {
        CreateFragment fragment = new CreateFragment();
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

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        imagesUris = new ArrayList<>();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.weekdays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner 1
        binding.fromdayspinner.setAdapter(adapter);
        binding.fromdayspinner.setOnItemSelectedListener(this);
        //spinner 2
        binding.todayspinner.setAdapter(adapter);
        binding.todayspinner.setOnItemSelectedListener(this);
//upload button to firestore
        binding.createPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                UploadImageList();
            }
        });

        //time picker 1
        binding.startTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }


        });    //time picker 2
        binding.endTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker(view);
            }


        });
        //Image Switcher
        binding.imgUploadPost.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                binding.iconaddimg.setVisibility(View.INVISIBLE);
                ImageView imageView = new ImageView(getContext());


                return imageView;

            }
        });

        //Upload Image(s) Button
        binding.uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesUris.clear();
                pickImagesIntent();
            }
        });

        binding.nextarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < imagesUris.size() - 1) {
                    position++;
                    binding.imgUploadPost.setImageURI(imagesUris.get(position));

                } else {
                    Toast.makeText(getActivity(), "No More images....", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.prevarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    position--;
                    binding.imgUploadPost.setImageURI(imagesUris.get(position));

                } else {
                    Toast.makeText(getActivity(), "No previous images", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    private void pickImagesIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODES);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_CODES) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    //pick multiple images
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {

                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imagesUris.add(imageUri);// add to list
                    }
                    binding.imgUploadPost.setImageURI(imagesUris.get(0));
                    position = 0;


                } else {
                    //pick single images
                    Uri imageUri = data.getData();
                    imagesUris.add(imageUri);
                    binding.imgUploadPost.setImageURI(imagesUris.get(0));
                    position = 0;

                }


            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        day = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                switch (view.getId()) {
                    case R.id.start_timebtn:
                        if (hour >= 12) {

                            binding.startTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " pm");
                        } else {
                            binding.startTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " am");

                        }
                        break;
                    case R.id.end_timebtn:
                        if (hour >= 12) {

                            binding.endTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " pm");
                        } else {
                            binding.endTimebtn.setText(String.format(getDefault(), "%02d:%02d", hour, minute) + " am");

                        }
                }


            }
        };


        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), /*style,*/ onTimeSetListener, hour, minute, true);


        timePickerDialog.show();
    }

    public void UploadImageList() {
        binding.createPlaceProgress.setVisibility(View.VISIBLE);
        String place_name = binding.placeName.getText().toString().trim();
        String st_day = binding.fromdayspinner.getSelectedItem().toString();
        String end_day = binding.todayspinner.getSelectedItem().toString();
        String descName = binding.descName.getText().toString().trim();
        String contact = binding.contactName.getText().toString().trim();
        String fromtime = binding.startTimebtn.getText().toString().trim();
        String totime = binding.endTimebtn.getText().toString().trim();
        String id = String.valueOf(System.currentTimeMillis());


        StorageReference imagePostFolder = FirebaseStorage.getInstance().getReference().child("ImagePostsFolder");
        for (upload_count = 0; upload_count < imagesUris.size(); upload_count++) {
            Uri individualImage = imagesUris.get(upload_count);
            StorageReference ImageName = imagePostFolder.child("Images" + individualImage.getLastPathSegment());
            ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    x = uri.toString();
                    Log.d("url1", "" + x);

                    tags.add(x);
                    Log.d("list","onSuccess: "+tags);

                    binding.createPlaceProgress.setVisibility(View.INVISIBLE);
                    //
                    firestore.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            binding.createPlaceProgress.setVisibility(View.VISIBLE);

                            String cat = task.getResult().getString("category");


                            if (task.getResult().getString("status").equalsIgnoreCase("1")) {
                                Place place = new Place(id, task.getResult().getString("userName"), place_name, descName, contact, st_day, end_day, fromtime, totime, x, tags);
                                firestore.collection(cat).document(id).set(place).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "Upload new place success", Toast.LENGTH_SHORT).show();
                                        imagesUris.clear();
                                        binding.createPlaceProgress.setVisibility(View.INVISIBLE);



                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(),"Error :"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "You are not allowed to upload posts", Toast.LENGTH_SHORT).show();
                                binding.createPlaceProgress.setVisibility(View.INVISIBLE);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                            binding.createPlaceProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Upload data failed :" + e, Toast.LENGTH_SHORT).show();

                        }
                    });
                    //



                }


            });

            ImageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file created ");
                }
            });


        }


    }




    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

}