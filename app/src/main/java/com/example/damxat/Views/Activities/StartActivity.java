package com.example.damxat.Views.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.damxat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        // Obté l'usuari amb la sessió actual
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){ // Si l'usuari esta loguejat, s'obre la pantalla de Main Activity
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startLogin = findViewById(R.id.startLogin);
        Button startRegister = findViewById(R.id.startRegister);

        // Al clickar sobre el butó Login ->
        startLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Es crea un intent per entrar a la pantalla del login
                Intent intentLogin = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        // Al clickar sobre el butó Register ->
        startRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Es crea un intent per entrar a la pantalla del register
                Intent intentRegister = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });
    }
}