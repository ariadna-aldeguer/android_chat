package com.example.damxat.Views.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damxat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Agafa l'instancia del firebase actual.
        auth = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.groupAdd);
        EditText loginEmail = findViewById(R.id.groupName);
        EditText loginPassword = findViewById(R.id.loginPassword);

        // Al clickar sobre el butó Login ->
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                // Comprova que l'email i la contrasenya no estiguin buides. Si ho están, mostra un missatge d'alerta.
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                }else{
                    // Intenta loguejarse al firebase amb l'email i la contrasenya.
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Si s'ha pogut comentar ->
                            if(task.isSuccessful()){
                                // Crea un intent per entrar a la pantalla Main,
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                // Cas contrari, mostra un error.
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}