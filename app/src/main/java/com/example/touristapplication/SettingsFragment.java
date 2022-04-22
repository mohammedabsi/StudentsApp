package com.example.touristapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.touristapplication.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding ;
    private Dialog dialog;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentemail = user.getEmail();

    DocumentReference userprofilenode = FirebaseFirestore.getInstance().collection("User").document(currentemail);
    int NightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        binding = FragmentSettingsBinding.inflate(inflater , container, false );

        sharedPreferences = getActivity().getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(NightMode);


        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            binding.styleModeSwitch.setChecked(true);
        }else {
            binding.styleModeSwitch.setChecked(false);

        }

        binding.logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),AuthenticationAvtivity.class));
                Toast.makeText(getActivity(), "logout Success", Toast.LENGTH_SHORT).show();
                getActivity().finish();

            }
        });



        binding.styleModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){

                    if (b){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    }
                }else {
                    binding.styleModeSwitch.setChecked(false);
                    Toast.makeText(getActivity(), R.string.version_test, Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog = new Dialog(getActivity());
        UpdatePass();

        binding.updatePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        return binding.getRoot();


    }



    private void UpdatePass(){
        dialog.setContentView(R.layout.update_password_dialog);
        dialog.getWindow().setBackgroundDrawable(getActivity().getDrawable(R.drawable.white_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button updatePassBtn = dialog.findViewById(R.id.updatePassBtn);
        Button cancelUpdate = dialog.findViewById(R.id.cancelUpdate);
        EditText oldPass = dialog.findViewById(R.id.oldPass);
        EditText newPass = dialog.findViewById(R.id.newPass);

        updatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getEmail();
                String old_Pass = oldPass.getText().toString().trim();
                String new_Pass = newPass.getText().toString().trim();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, old_Pass);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(new_Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                userprofilenode.update("password",new_Pass);
                                                oldPass.getText().clear();
                                                newPass.getText().clear();
                                                Toast.makeText(getActivity(), "Password Changed ", Toast.LENGTH_SHORT).show();
                                                Log.d("pas", "Password updated");
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(getActivity(), "Error password not Changed", Toast.LENGTH_SHORT).show();
                                                Log.d("pas", "Error password not updated");
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Error auth failed", Toast.LENGTH_SHORT).show();
                                    Log.d("pas", "Error auth failed");
                                    dialog.dismiss();

                                }
                            }
                        });

            }
        });
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        NightMode = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = getActivity().getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putInt("NightModeInt", NightMode);
        editor.apply();
    }
}