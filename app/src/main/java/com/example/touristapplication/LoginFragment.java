package com.example.touristapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.touristapplication.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    FragmentLoginBinding binding;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        binding.forgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetPass = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link at your email ");
                passwordResetDialog.setView(resetPass);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract email reset link

                        String mail = resetPass.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Reset link sent To Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error ! "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                passwordResetDialog.create().show();
            }
        });

        return binding.getRoot();
    }

    private void Login() {
        String email = binding.loginEmail.getText().toString();
        String password = binding.loginPassword.getText().toString();

        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                binding.loginProgressBar.setVisibility(View.VISIBLE);
                binding.bluralloginlayout.setVisibility(View.VISIBLE);
                binding.loginEmail.setEnabled(false);
                binding.loginPassword.setEnabled(false);

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        binding.loginProgressBar.setVisibility(View.INVISIBLE);
                        binding.bluralloginlayout.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {

                            if (email.equalsIgnoreCase("admin@admin.com") && password.equalsIgnoreCase("admin123")) {
                                Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), AdminMainActivity.class));
                                getActivity().finish();
                            }

                            firestore.collection("User").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().exists()) {
                                        if (task.getResult().get("accounttype").toString().equalsIgnoreCase("owner")) {
                                            startActivity(new Intent(getActivity(), OwnerMainActivity.class));
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();

                                        } else if (task.getResult().get("accounttype").toString().equalsIgnoreCase("user")) {
                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Login success", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Login failed ...", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                    binding.loginEmail.setEnabled(true);
                                    binding.loginPassword.setEnabled(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.loginEmail.setEnabled(true);
                                    binding.loginPassword.setEnabled(true);
                                    Toast.makeText(getActivity(), "Login failed" + e, Toast.LENGTH_SHORT).show();
                                    Log.d("t", "onFailure: " + e);

                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("loginfail", "onFailure: " + e);
                        binding.loginProgressBar.setVisibility(View.INVISIBLE);
                        binding.bluralloginlayout.setVisibility(View.INVISIBLE);
                        binding.loginEmail.setEnabled(true);
                        binding.loginPassword.setEnabled(true);
                    }
                });
            } else {
                binding.loginPassword.setError("enter your password");
                binding.loginPassword.requestFocus();
            }
        } else {
            binding.loginEmail.setError("enter your email");
            binding.loginEmail.requestFocus();
        }
    }


}