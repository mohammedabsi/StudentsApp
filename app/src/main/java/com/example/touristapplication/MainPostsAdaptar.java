package com.example.touristapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainPostsAdaptar extends RecyclerView.Adapter<MainPostsAdaptar.MainViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    private ArrayList<Place> mainList;
    private Context mContext;

    public MainPostsAdaptar(ArrayList<Place> mainList, Context mContext, RecyclerViewInterface recyclerViewInterface) {
        this.mainList = mainList;
        this.mContext = mContext;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);


        return new MainViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Place mainPlace = mainList.get(position);
        holder.place_name_mainiten.setText(mainPlace.getPlace_name());
        String ImageUrl = null;
        ImageUrl = mainPlace.getImageUrl();
        Picasso.get().load(ImageUrl).into(holder.single_img);

    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {
        ImageView single_img;
        TextView place_name_mainiten;

        public MainViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            single_img = itemView.findViewById(R.id.single_img);
            place_name_mainiten = itemView.findViewById(R.id.place_name_mainiten);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
