package com.example.touristapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.touristapplication.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    FragmentRegisterBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String category = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(adapter);
        binding.categorySpinner.setOnItemSelectedListener(this);
        binding.ownerChkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.ownerChkbox.isChecked()) {
                    binding.categorySpinner.setVisibility(View.VISIBLE);
                } else {
                    binding.categorySpinner.setVisibility(View.INVISIBLE);
                    category = null;

                }
            }
        });


        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
        return binding.getRoot();
    }

    private void SignUp() {

        String username = binding.regUsername.getText().toString();
        String email = binding.regEmail.getText().toString();
        String phone = binding.regPhone.getText().toString();
        String password = binding.regPassword.getText().toString();
        Boolean status = binding.ownerChkbox.isChecked();


        if (status) {
            binding.categorySpinner.setVisibility(View.VISIBLE);
        } else {
            binding.categorySpinner.setVisibility(View.INVISIBLE);
            category = null;

        }


        if (!username.isEmpty()) {
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!phone.isEmpty()) {
                    if (!password.isEmpty() && password.length() > 6) {
                        //todo: Register code
                        binding.regProgressBar.setVisibility(View.VISIBLE);
                        binding.bluralregisterlayout.setVisibility(View.VISIBLE);
                        binding.regUsername.setEnabled(false);
                        binding.regEmail.setEnabled(false);
                        binding.regPhone.setEnabled(false);
                        binding.regPassword.setEnabled(false);
                        binding.ownerChkbox.setEnabled(false);
                        binding.categorySpinner.setEnabled(false);
                        if (!status) {
                            mAuth
                                    .createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            User user = new User(username, email, phone, password, "user" , "0");
                                            firestore.collection("User")
                                                    .document(email)
                                                    .set(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            binding.regProgressBar.setVisibility(View.INVISIBLE);
                                                            binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getActivity(), "Register success :)", Toast.LENGTH_SHORT).show();
                                                            goMainActivity();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    binding.regProgressBar.setVisibility(View.INVISIBLE);
                                                    binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getActivity(), "adding this data to firestor failed!", Toast.LENGTH_SHORT).show();
                                                    Log.d("firestorefail", "onFailure: " + e);
                                                }
                                            });
                                            binding.regUsername.setEnabled(true);
                                            binding.regEmail.setEnabled(true);
                                            binding.regPhone.setEnabled(true);
                                            binding.regPassword.setEnabled(true);
                                            binding.ownerChkbox.setEnabled(true);
                                            binding.categorySpinner.setEnabled(true);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.regProgressBar.setVisibility(View.INVISIBLE);
                                    binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                    binding.regUsername.setEnabled(true);
                                    binding.regEmail.setEnabled(true);
                                    binding.regPhone.setEnabled(true);
                                    binding.regPassword.setEnabled(true);
                                    binding.ownerChkbox.setEnabled(true);
                                    binding.categorySpinner.setEnabled(true);
                                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                    Log.d("authfail", "onFailure: " + e);
                                }
                            });
                        } else if (status) {
                            if (!category.equals(null)) {
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        User user = new User(username, email, phone, password, "owner", category , "1");
                                        firestore.collection("User").document(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                binding.regProgressBar.setVisibility(View.INVISIBLE);
                                                binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getActivity(), "Register success :)", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getActivity(), OwnerMainActivity.class));
                                                getActivity().finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                binding.regProgressBar.setVisibility(View.INVISIBLE);
                                                binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                                Log.d("ownerauthfail", "onFailure: " + e);
                                            }
                                        });
                                        binding.regUsername.setEnabled(true);
                                        binding.regEmail.setEnabled(true);
                                        binding.regPhone.setEnabled(true);
                                        binding.regPassword.setEnabled(true);
                                        binding.ownerChkbox.setEnabled(true);
                                        binding.categorySpinner.setEnabled(true);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.regProgressBar.setVisibility(View.INVISIBLE);
                                        binding.bluralregisterlayout.setVisibility(View.INVISIBLE);
                                        binding.regUsername.setEnabled(true);
                                        binding.regEmail.setEnabled(true);
                                        binding.regPhone.setEnabled(true);
                                        binding.regPassword.setEnabled(true);
                                        binding.ownerChkbox.setEnabled(true);
                                        binding.categorySpinner.setEnabled(true);
                                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                        Log.d("authfail", "onFailure: " + e);
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Choose right category", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        binding.regPassword.setError("Type Valid password that contain more than 6 digits");
                        binding.regPassword.requestFocus();
                    }

                } else {
                    binding.regPhone.setError("add your phone number");
                    binding.regPhone.requestFocus();
                }

            } else {
                binding.regEmail.setError("Provide valid email please ...");
                binding.regEmail.requestFocus();

            }

        } else {
            binding.regUsername.setError("Add your name");
            binding.regUsername.requestFocus();
        }

    }

    private void goMainActivity() {
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getItemAtPosition(i).equals("Choose your category :")) {


        } else {
            category = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(view.getContext(), category, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}