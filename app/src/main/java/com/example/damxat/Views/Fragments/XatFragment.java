package com.example.damxat.Views.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.damxat.Adapter.RecyclerXatAdapter;
import com.example.damxat.Model.User;
import com.example.damxat.Model.Xat;
import com.example.damxat.Model.XatGroup;
import com.example.damxat.Notification.ApiInterface;
import com.example.damxat.Notification.Constants;
import com.example.damxat.Notification.NotificationModel;
import com.example.damxat.Notification.PushNotification;
import com.example.damxat.Notification.ResponseModel;
import com.example.damxat.R;
import com.example.damxat.Views.Activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XatFragment extends Fragment {

    DatabaseReference ref;
    View view;
    FirebaseUser firebaseUser;
    String userid;
    Bundle bundle;
    Boolean isXatUser;
    ArrayList<Xat> arrayXats;
    ArrayList<String> arrayUsers;
    String userToken;
    XatGroup group;
    String groupName;
    ImageButton btnRecord;
    int RecordAudioRequestCode = 333;
    EditText txtMessage;
    final int RESULT_OK = -1;

    public XatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Demana els permissos per gravar audio.
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_xat, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Agafa la informació que s'ha guardat al bundle.
        bundle = getArguments();

        if(bundle.getString("type").equals("xatuser")){
            isXatUser = true;
            getUserXat();
        }else{
            isXatUser = false;
            groupName = bundle.getString("group");
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(groupName);
            readGroupMessages(groupName);
        }


        ImageButton btnMessage = view.findViewById(R.id.btnMessage);
        btnRecord = view.findViewById(R.id.btnRecord);
        txtMessage = view.findViewById(R.id.txtMessage);

        // Al clickar sobre el butó d'enviar missatge ->
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txtMessage.getText().toString();

                // Si el missatge no es buit, va a la funció d'enviar missatge.
                if(!msg.isEmpty()){
                    sendMessage(firebaseUser.getUid(), msg, isXatUser);
                }else{
                    Toast.makeText(getContext(), "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                txtMessage.setText("");
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setImageResource(R.drawable.ic_mic_on);

                // Es crea un intent que cridará la opció de gravar
                Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                // Li enviem que identifiqui l'idioma i un dialog
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, digues quelcom!");

                //Enviem la petició amb l'intent i un identificador
                startActivityForResult(speechRecognizerIntent, 333);
            }
        });

        return view;
    }

    // obté l'usuari actual
    public void getUserXat(){
        if(getArguments()!=null) {
            userid = bundle.getString("user");

            // Agafa la referencia de l'usuari a partir del id.
            ref = FirebaseDatabase.getInstance().getReference("Users").child(userid);

            // Si la informació canvia al firebase ->
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Crea un usuari a partir de la informació actual
                    User user = dataSnapshot.getValue(User.class);
                    //userToken = "fCfslGvqT6CYmG_ubPHGsC:APA91bGGfEVLdHa6i6c8hr-8K3ztS7DtWcSl45BvxhD-n_qvIThLK-5kE3V6lkr-_z9u2kch9CqJKNEKF-ShdydIeMTWVXnpMUanjmGSPvk42PQeLyczl-rXOTdosTy1LM5BhOBMwLlu";
                    userToken = user.getToken();
                    // Al Main activity, a l'action bar fixa el títol amb l'username de l'usuari.
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(user.getUsername());

                    // Llegeix els missatges dels usuaris.
                    readUserMessages();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void sendMessage(String sender, String message, boolean isXatUser){
        // Si l'usuari es el creador del xat, crea un chat amb el missatge
        if(isXatUser==true){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            String receiver = userid;
            Xat xat = new Xat(sender, receiver, message);
            sendNotification(message);
            ref.child("Xats").push().setValue(xat);
        }else{
            ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

            Xat xat = new Xat(sender, message);

            if(arrayXats==null) {
                arrayXats = new ArrayList<Xat>();
                arrayXats.add(xat);
            }else{
                arrayXats.add(xat);
            }

            if(group.getUsers()==null){
                arrayUsers = new ArrayList<String>();
                arrayUsers.add(firebaseUser.getUid());
            }else{
                if(!group.getUsers().contains(firebaseUser.getUid())){
                    arrayUsers.add(firebaseUser.getUid());
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("xats", arrayXats);
            hashMap.put("users", arrayUsers);
            ref.updateChildren(hashMap);
        }
    }

    public void sendNotification(String msg){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NotificationModel noti = new NotificationModel("Missatge", msg, "");
        PushNotification push = new PushNotification(userToken, noti);
        //PushNotification push = new PushNotification("fCfslGvqT6CYmG_ubPHGsC:APA91bGGfEVLdHa6i6c8hr-8K3ztS7DtWcSl45BvxhD-n_qvIThLK-5kE3V6lkr-_z9u2kch9CqJKNEKF-ShdydIeMTWVXnpMUanjmGSPvk42PQeLyczl-rXOTdosTy1LM5BhOBMwLlu", noti);

        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<ResponseModel> call = api.postNotification(push);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if(!response.isSuccessful()) {
                    Log.i("fail noti", String.valueOf(response.code()));
                }
                Log.i("success", String.valueOf(response.code()));
                Toast.makeText(getContext(), "Notificación enviada", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.i("fail noti", t.getMessage());
            }
        });
    }

    public void readUserMessages(){
        arrayXats = new ArrayList<>();

        //Comentar
        ref = FirebaseDatabase.getInstance().getReference("Xats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayXats.clear();

                //Comentar
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Xat xat = postSnapshot.getValue(Xat.class);
                    //Comentar
                    if(xat.getReceiver().equals(userid) && xat.getSender().equals(firebaseUser.getUid()) ||
                            xat.getReceiver().equals(firebaseUser.getUid()) && xat.getSender().equals(userid)){
                        arrayXats.add(xat);
                        Log.i("logTest",xat.getMessage());
                    }
                }

                //Comentar
                updateRecycler();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }

    public void readGroupMessages(String groupName){

        //Comentar
        ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(XatGroup.class);

                arrayXats = group.getXats();

                if(arrayXats!=null) {
                    updateRecycler();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("damxat", "Failed to read value.", error.toException());
            }
        });
    }

    public void updateRecycler(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerXat);
        RecyclerXatAdapter adapter = new RecyclerXatAdapter(arrayXats, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Al acabar de gravar, s'analitza l'intent

        if(requestCode == 333 && resultCode == RESULT_OK && data != null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtMessage.setText(result.get(0));

        }
        btnRecord.setImageResource(R.drawable.ic_mic_off);
    }
}