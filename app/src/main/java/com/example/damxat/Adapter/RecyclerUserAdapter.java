package com.example.damxat.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.damxat.Model.User;
import com.example.damxat.R;
import com.example.damxat.Views.Fragments.XatFragment;

import java.util.ArrayList;


public class RecyclerUserAdapter extends RecyclerView.Adapter<RecyclerUserAdapter.ViewHolder> {
    private ArrayList<User> arrayList;
    private Context context;

    public RecyclerUserAdapter(ArrayList<User> arrN, Context c){
        this.arrayList = arrN;
        this.context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.recUsername.setText(arrayList.get(i).getUsername());

        if(arrayList.get(i).getStatus().equals("online")){
            holder.onlineOffline.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_online));
        }else{
            holder.onlineOffline.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_offline));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("user", arrayList.get(i).getId());
                        bundle.putString("type", "xatuser");
                        XatFragment detailFragment = new XatFragment();
                        detailFragment.setArguments(bundle);

                        AppCompatActivity app = (AppCompatActivity) view.getContext();
                        app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailFragment).commit();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView recUsername;
        ConstraintLayout itemUser;
        ImageView onlineOffline;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recUsername = itemView.findViewById(R.id.recUsername);
            itemUser = itemView.findViewById(R.id.itemUser);
            onlineOffline = itemView.findViewById(R.id.onlineOffline);
        }
    }
}

