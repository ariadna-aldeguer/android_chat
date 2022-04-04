package com.example.damxat.Views.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.damxat.Model.User;
import com.example.damxat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.registerButton);
        EditText registerUsername = findViewById(R.id.registerUsername);
        EditText registerEmail = findViewById(R.id.registerEmail);
        EditText registerPassword = findViewById(R.id.registerPassword);

        // S'obté la instancia del firebase.
        auth = FirebaseAuth.getInstance();

        // Al clickar sobre el butó Register.
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = registerUsername.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();

                //  Si l'usuari, el mail o la contrasenya no están en buit.
                if(username.isEmpty() || email.isEmpty() || password.isEmpty()){ // -> Mostra missatge d'alerta.
                    Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(username, email, password); // Va a la funció registerUser.
                }

            }
        });
    }

    // Crea un usuari a firebase amb l'email i la contrasenya.
    public void registerUser(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       // Si s'ha pogut crear correctament
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            // Agafa la referencia de l'usuari amb l'id de l'usuari al firebase.
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            // Crea un usuari amb l'id de l'usuari, el username amb l'esatus offiline.
                            User user = new User(userId, username, "offline");

                            // Fixa (Guarda) l'usuari al firebase.
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Si tot es correcte, crea un intent per entrar a la pantalla de login.
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }else{
                            // Si no es correcte la creació de l'usuari, mostra un missatge d'alerta.
                            Toast.makeText(RegisterActivity.this, "You can't register with this email", Toast.LENGTH_SHORT).show();
                        }
                   }
               });
    }
}