package com.example.touristapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.touristapplication.databinding.FragmentDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;
    Boolean clicked = false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
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
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        String name = getActivity().getIntent().getStringExtra("name");
        //ArrayList<String> imageurl = getActivity().getIntent().getStringExtra("imageurl");

        String st_time = getActivity().getIntent().getStringExtra("st_time");
        String end_time = getActivity().getIntent().getStringExtra("end_time");
        String contact = getActivity().getIntent().getStringExtra("contact");
        ArrayList<SlideModel> imgList = new ArrayList<>();
        imgList = (ArrayList<SlideModel>) getActivity().getIntent().getSerializableExtra("imgTags");


        Log.d("xsdsd", "onCreateView: "+ Locale.getDefault().getDisplayLanguage());


if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("العربية")){
    translate();
    binding.username.setText(name);
    binding.userpostFromtime.setText(st_time);
    binding.userpostTotime.setText(end_time);
    binding.phoneDesc.setText(contact);


}else if (Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")){

    String desc = getActivity().getIntent().getStringExtra("desc");
    String placename = getActivity().getIntent().getStringExtra("place name");
    String st_day = getActivity().getIntent().getStringExtra("st_day");
    String end_day = getActivity().getIntent().getStringExtra("end_day");
     binding.userpostDesc.setText(desc);
    binding.userPostName.setText(placename);
    binding.userpostFromday.setText(st_day);
    binding.userpostToday.setText(end_day);
    binding.username.setText(name);
    binding.userpostFromtime.setText(st_time);
    binding.userpostTotime.setText(end_time);
    binding.phoneDesc.setText(contact);



}



        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+contact));
                startActivity(intent);
            }
        });


        ArrayList<SlideModel> transactionList = new ArrayList<SlideModel>();
        for (int i = 0; i < imgList.size(); i++) {
       transactionList.add(new SlideModel(String.valueOf(imgList.get(i)), ScaleTypes.CENTER_CROP));
        }

        Log.d("list", "details list: " + imgList);
        Log.d("list", "details list: " + transactionList);
        binding.userPostImgslider.setImageList(transactionList);



        return binding.getRoot();


    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void translate(){
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.ARABIC)
                        .build();
        final Translator englishGermanTranslator =
                Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        englishGermanTranslator.translate(getActivity().getIntent().getStringExtra("desc"))
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.userpostDesc.setText(s);

                    }
                });
        englishGermanTranslator.translate(getActivity().getIntent().getStringExtra("place name"))
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.userPostName.setText(s);


                    }
                });     englishGermanTranslator.translate(getActivity().getIntent().getStringExtra("st_day"))
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.userpostFromday.setText(s);

                    }
                });     englishGermanTranslator.translate(getActivity().getIntent().getStringExtra("end_day"))
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        binding.userpostToday.setText(s);

                    }
                });



        Translator translator = Translation.getClient(options);
        getLifecycle().addObserver(translator);

    }

}