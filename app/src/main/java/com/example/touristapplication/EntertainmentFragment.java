package com.example.touristapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.touristapplication.databinding.FragmentEntertainmentBinding;
import com.example.touristapplication.databinding.FragmentRestaurantsBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntertainmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntertainmentFragment extends Fragment implements RecyclerViewInterface {
    FragmentEntertainmentBinding binding;
    private MainPostsAdaptar mainPostsAdaptar;
    private ArrayList<Place> placesArrayList;
    private FirebaseFirestore mFirebaseFirestore;
    LinearLayoutManager layoutManager ;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntertainmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntertainmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EntertainmentFragment newInstance(String param1, String param2) {
        EntertainmentFragment fragment = new EntertainmentFragment();
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
        binding = FragmentEntertainmentBinding.inflate(inflater , container, false);
        mFirebaseFirestore = FirebaseFirestore.getInstance();


        initRecycler();
        RetrieveNewsData();
        binding.enttxt.addTextChangedListener(new TextWatcher() {
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
        layoutManager = new GridLayoutManager(getActivity(), 2);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        binding.entertainmentRecycler.setLayoutManager(layoutManager2);
        binding.entertainmentRecycler.setHasFixedSize(true);
        mainPostsAdaptar = new MainPostsAdaptar(placesArrayList, getActivity().getApplicationContext(), this);
        binding.entertainmentRecycler.setAdapter(mainPostsAdaptar);
    }

    public void RetrieveNewsData() {

        mFirebaseFirestore.collection("Entertainment").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                    Log.d("fireStore Error", error.getMessage().toString());

                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        placesArrayList.add(documentChange.getDocument().
                                toObject(Place.class));


                    }

                    mainPostsAdaptar.notifyDataSetChanged();


                }


            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MainActivity2.class);
        intent.putExtra("name",placesArrayList.get(position).getOwnerName());
        intent.putExtra("imageurl",placesArrayList.get(position).getImageUrl());
        intent.putExtra("place name",placesArrayList.get(position).getPlace_name());
        intent.putExtra("st_day",placesArrayList.get(position).getSt_day());
        intent.putExtra("end_day",placesArrayList.get(position).getEnd_day());
        intent.putExtra("st_time",placesArrayList.get(position).getFromtime());
        intent.putExtra("end_time",placesArrayList.get(position).getTotime());
        intent.putExtra("desc",placesArrayList.get(position).getDescName());
        intent.putExtra("contact",placesArrayList.get(position).getContact());
        ArrayList<String> x = (ArrayList<String>) placesArrayList.get(position).getImgTags();
        //   Log.d("list", "onItemClick: "+ x);

        intent.putExtra("imgTags", x);

        startActivity(intent);
    }
}