package com.example.touristapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.touristapplication.databinding.FragmentRestaurantsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
 * Use the {@link RestaurantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantsFragment extends Fragment implements RecyclerViewInterface {
    FragmentRestaurantsBinding binding;
    private MainPostsAdaptar mainPostsAdaptar;
    private ArrayList<Place> placesArrayList;
    private ArrayList<SlideModel> ImagesPlaceArraylist;
    private FirebaseFirestore mFirebaseFirestore;
    private Place imgPlace = new Place();
    LinearLayoutManager layoutManager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestaurantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestaurantsFragment newInstance(String param1, String param2) {
        RestaurantsFragment fragment = new RestaurantsFragment();
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
        binding = FragmentRestaurantsBinding.inflate(inflater, container, false);
        mFirebaseFirestore = FirebaseFirestore.getInstance();


        initRecycler();
        RetrieveNewsData();

        binding.resttxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return binding.getRoot();

    }

    private void filter(String text) {
        ArrayList<Place> filteredList = new ArrayList<>();

        for (Place item : placesArrayList) {
            if (item.getPlace_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mainPostsAdaptar.filterList(filteredList);
    }

    private void initRecycler() {
        placesArrayList = new ArrayList<Place>();
        ImagesPlaceArraylist = new ArrayList<SlideModel>();
        layoutManager = new GridLayoutManager(getActivity(), 2);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.restaurantRecycler.setLayoutManager(layoutManager2);
        binding.restaurantRecycler.setHasFixedSize(true);
        mainPostsAdaptar = new MainPostsAdaptar(placesArrayList, getActivity().getApplicationContext(), this);
        binding.restaurantRecycler.setAdapter(mainPostsAdaptar);
    }

    public void RetrieveNewsData() {

        mFirebaseFirestore.collection("Restaurants").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.d("fireStore Error", error.getMessage().toString());
                    return;
                }



                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    Log.d("docOn", "onEvent: " + imgPlace.getId());

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        placesArrayList.add(documentChange.getDocument().
                                toObject(Place.class));


                        mFirebaseFirestore.collection("Restaurants").document(documentChange.getDocument().getId()).collection("PostUrlImages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        ImagesPlaceArraylist.add(new SlideModel(queryDocumentSnapshot.getString("Imglink"), ScaleTypes.FIT));
                                        Log.d("docout", "onEvent: " + queryDocumentSnapshot.get("Imglink"));
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Getting images to slide fail", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }


                    mainPostsAdaptar.notifyDataSetChanged();


                }


            }
        });


    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MainActivity2.class);
        intent.putExtra("name", placesArrayList.get(position).getOwnerName());
        intent.putExtra("place name", placesArrayList.get(position).getPlace_name());
        intent.putExtra("st_day", placesArrayList.get(position).getSt_day());
        intent.putExtra("end_day", placesArrayList.get(position).getEnd_day());
        intent.putExtra("st_time", placesArrayList.get(position).getFromtime());
        intent.putExtra("end_time", placesArrayList.get(position).getTotime());
        intent.putExtra("desc", placesArrayList.get(position).getDescName());
        intent.putExtra("contact", placesArrayList.get(position).getContact());




        ArrayList<String> x = (ArrayList<String>) placesArrayList.get(position).getImgTags();
        Log.d("list", "onItemClick: "+ x);

        intent.putExtra("imgTags", x);

        Bundle bundle = new Bundle();
        bundle.putSerializable("key", x);


        startActivity(intent);

    }
}