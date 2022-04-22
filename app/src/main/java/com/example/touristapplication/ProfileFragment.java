package com.example.touristapplication;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.touristapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FragmentProfileBinding binding;
    String currentemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
    private StorageTask mUploadTask;

    private int click = 0;

    Map<String, Object> map = new HashMap<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        uploadProfileDetails();



        binding.startEditbtn.setOnClickListener(listener);
        binding.editImgTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        binding.cancelButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startEditbtn.setText(R.string.edit);
                binding.editImgTxt.setVisibility(View.GONE);
                binding.cancelButtonProfile.setVisibility(View.GONE);
                binding.usernameProfile.setEnabled(false);
                binding.usernameProfile.clearFocus();
                binding.phoneProfile.setEnabled(false);
                uploadProfileDetails();
            }
        });

        return binding.getRoot();
    }


    private void uploadProfileDetails() {

        firestore.collection("User").document(currentemail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                binding.usernameProfile.setText(task.getResult().getString("userName"));
                binding.typeProfile.setText(task.getResult().getString("accounttype"));
                binding.emailProfile.setText(task.getResult().getString("email"));
                binding.phoneProfile.setText(task.getResult().getString("phone"));
                if (task.getResult().getString("imageUrl")!= null){

                    Picasso.get().load(task.getResult().getString("imageUrl")).into(binding.profilePhoto);

                }else {
                    binding.profilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(binding.profilePhoto);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        binding.profileProgress.setVisibility(View.VISIBLE);

        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 500);
                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String ImageUrl = uri.toString();
                                    map.put("imageUrl", ImageUrl);
                                    System.out.println(ImageUrl);
                                    firestore.collection("User").document(currentemail).update(map);
                                    Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
                                    binding.profileProgress.setVisibility(View.GONE);


                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            binding.profileProgress.setVisibility(View.GONE);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
//            Toast.makeText(this, "No file selected For Image", Toast.LENGTH_SHORT).show();
        }
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (click % 2 == 0) {
                binding.startEditbtn.setText(R.string.Submit);
                binding.editImgTxt.setVisibility(View.VISIBLE);
                binding.cancelButtonProfile.setVisibility(View.VISIBLE);
                binding.usernameProfile.setEnabled(true);
                binding.usernameProfile.requestFocus();
                binding.phoneProfile.setEnabled(true);
            } else if (click % 2 !=0) {
                UpdateData();
                binding.startEditbtn.setText(R.string.edit);
                binding.editImgTxt.setVisibility(View.GONE);
                binding.cancelButtonProfile.setVisibility(View.GONE);
                binding.usernameProfile.setEnabled(false);
                binding.usernameProfile.clearFocus();
                binding.phoneProfile.setEnabled(false);
            }
            click++ ;
        }
    };


    public void UpdateData() {
        String username = binding.usernameProfile.getText().toString().trim();
        String phone = binding.phoneProfile.getText().toString().trim();



        HashMap hashMap = new HashMap();
        hashMap.put("userName", username);
        hashMap.put("phone", phone);

        uploadFile();

        firestore.collection("User").document(currentemail).update(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}