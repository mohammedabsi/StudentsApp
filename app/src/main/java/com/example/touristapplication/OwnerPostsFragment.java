package com.example.touristapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.touristapplication.databinding.FragmentOwnerPostsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnerPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnerPostsFragment extends Fragment implements RecyclerViewInterface {

    FragmentOwnerPostsBinding binding ;

    private MainPostsAdaptar mainPostsAdaptar;
    private ArrayList<Place> placesArrayList;
    LinearLayoutManager layoutManager;

    private FirebaseFirestore mFirebaseFirestore;
    private Place imgPlace = new Place();
    ProgressDialog pd ;

    String current_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OwnerPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OwnerPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OwnerPostsFragment newInstance(String param1, String param2) {
        OwnerPostsFragment fragment = new OwnerPostsFragment();
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


         binding = FragmentOwnerPostsBinding.inflate(inflater , container,false);

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(getActivity());


        initRecycler();
        RetrieveNewsData();

         return binding.getRoot();
    }
    private void initRecycler() {
        placesArrayList = new ArrayList<Place>();
        layoutManager = new GridLayoutManager(getActivity(), 2);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.ownerpostsRecycler.setLayoutManager(layoutManager2);
        binding.ownerpostsRecycler.setHasFixedSize(true);
        mainPostsAdaptar = new MainPostsAdaptar(placesArrayList, getActivity().getApplicationContext() , this);
        binding.ownerpostsRecycler.setAdapter(mainPostsAdaptar);
    }

    public void RetrieveNewsData() {

        mFirebaseFirestore.collection("User").document(current_email).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            String cat = task.getResult().getString("category");

                            mFirebaseFirestore.collection(cat).whereEqualTo("post_emailId",current_email).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                    if (error != null) {
                                        Log.d("fireStore Error", error.getMessage().toString());
                                        return;
                                    }



                                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                                      //  Log.d("docOn", "onEvent: " + imgPlace.getId());

                                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                            placesArrayList.add(documentChange.getDocument().
                                                    toObject(Place.class));



                                        } else {
                                          //  Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                                        }


                                        mainPostsAdaptar.notifyDataSetChanged();


                                    }


                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public void onItemClick(Integer position) {



        Bundle bundle = new Bundle();





        bundle.putString("place name", placesArrayList.get(position).getPlace_name());
        bundle.putString("desc", placesArrayList.get(position).getDescName());
        bundle.putString("contact", placesArrayList.get(position).getContact());
        bundle.putString("id", placesArrayList.get(position).getId());

        mFirebaseFirestore.collection("User").document(current_email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if (task.isSuccessful()){
               bundle.putString("cat", task.getResult().getString("category"));

               OwnerDetailsFragment fragobj = new OwnerDetailsFragment();
               fragobj.setArguments(bundle);

               getParentFragmentManager().beginTransaction().replace(R.id.ownerframe_layout,
                       fragobj).addToBackStack(null).commit();

           }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }



}