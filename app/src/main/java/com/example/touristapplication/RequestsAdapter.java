package com.example.touristapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsviewHolder> {

    Context context;
    ArrayList<User> userArrayList;
    List<String> keys;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    public RequestsAdapter(Context context, ArrayList<User> userArrayList, List<String> keys, FirebaseFirestore firestore) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.firestore = firestore;
    }

    @NonNull
    @Override
    public RequestsAdapter.RequestsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.single_request_item, parent, false);
        //notifyDataSetChanged();

        return new RequestsviewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.RequestsviewHolder holder, int position) {
        User user = userArrayList.get(position);

        holder.owner_req.setText(user.getUserName());
        holder.owner_email_req.setText(user.getEmail());

        Log.d("user", ""+user.getUserName());


        holder.accept_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.owner_req.getText() + "\n is accepted :)", Toast.LENGTH_SHORT).show();
                firestore.collection("User").document(user.getEmail()).update("status", "1");

            }
        });
        holder.reject_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), holder.owner_req.getText() + "\n is rejected :(", Toast.LENGTH_SHORT).show();
                firestore.collection("User").document(user.getEmail()).update("status", "0");

            }
        });


    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class RequestsviewHolder extends RecyclerView.ViewHolder {

        TextView owner_req, owner_email_req;
        ImageButton accept_req, reject_req;

        public RequestsviewHolder(@NonNull View itemView) {
            super(itemView);
            owner_req = itemView.findViewById(R.id.ownerName_request);
            owner_email_req = itemView.findViewById(R.id.owner_email_req);
            accept_req = itemView.findViewById(R.id.accept_req);
            reject_req = itemView.findViewById(R.id.reject_req);


        }
    }
}