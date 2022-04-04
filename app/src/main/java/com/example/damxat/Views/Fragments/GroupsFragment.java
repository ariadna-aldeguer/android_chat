package com.example.damxat.Views.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.example.damxat.Adapter.RecyclerGroupAdapter;
import com.example.damxat.Model.XatGroup;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {
    View groupView;
    RecyclerView recyclerGroups;

    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupView= inflater.inflate(R.layout.fragment_groups, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Groups");

        recyclerGroups = groupView.findViewById(R.id.recyclerGroups);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        refreshRecycler();

        FloatingActionButton btnAddGroup = groupView.findViewById(R.id.floatingActionButton);
        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                refreshRecycler();
            }
        });

        return groupView;
    }


    public void showDialog(){
        View alertCustomdialog = getLayoutInflater().inflate( R.layout.dialog_group, null);

        //initialize alert builder.
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(alertCustomdialog);

        final AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        Button btnSaveList = alertCustomdialog.findViewById(R.id.groupAdd);

        btnSaveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText groupName = alertCustomdialog.findViewById(R.id.groupName);
                String groupNameTxt = groupName.getText().toString();

                //Comentar
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                XatGroup group = new XatGroup(groupNameTxt);
                ref.child("Groups").child(groupNameTxt).setValue(group);

                dialog.dismiss();
            }
        });
    }

    public void refreshRecycler(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        ArrayList<XatGroup> arrayGroups = new ArrayList<>();

        //Comentar
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayGroups.clear();
                //Comentar
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    XatGroup group = snapshot.getValue(XatGroup.class);
                     arrayGroups.add(group);
                }

                RecyclerGroupAdapter adapter = new RecyclerGroupAdapter(arrayGroups, getContext());
                recyclerGroups.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}