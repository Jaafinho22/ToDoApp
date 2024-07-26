package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    // Deklaration der UI-Elemente und Firebase-Instanzen
    TextInputEditText editTextName, editTextEmail, editTextPassword; // Eingabefelder für Name, E-Mail und Passwort
    Button buttonReg; // Registrierungsbutton
    FirebaseAuth mAuth; // FirebaseAuth-Instanz zur Authentifizierung
    ProgressBar progressBar; // Fortschrittsanzeige
    TextView textView; // TextView zum Wechseln zur Login-Aktivität

    @Override
    public void onStart() {
        super.onStart();
        // Überprüfen, ob der Benutzer bereits angemeldet ist, und ggf. zur MainActivity (Hauptseite) weiterleiten
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Layout der Aktivität setzen

        // Firebase-Instanzen initialisieren
        mAuth = FirebaseAuth.getInstance();

        // UI-Elemente initialisieren
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        // Klick-Listener für den TextView setzen, um zum Login zu wechseln
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        // Klick-Listener für den Registrierungsbutton setzen
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true); // Ladesanzeige einblenden
                String name = String.valueOf(editTextName.getText()); // Namen aus dem Eingabefeld lesen
                String email = String.valueOf(editTextEmail.getText()); // E-Mail aus dem Eingabefeld lesen
                String password = String.valueOf(editTextPassword.getText()); // Passwort aus dem Eingabefeld lesen

                // Eingaben überprüfen
                if (TextUtils.isEmpty(name)) { //Wenn kein Name
                    Toast.makeText(Register.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    showProgress(false); // Ladesanzeige ausblenden
                    return;
                }

                if (TextUtils.isEmpty(email)) { //Wenn keine Email
                    Toast.makeText(Register.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    showProgress(false); // Ladesanzeige ausblenden
                    return;
                }

                if (TextUtils.isEmpty(password)) { //Wenn kein Passwort
                    Toast.makeText(Register.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    showProgress(false); // Ladesanzeige ausblenden
                    return;
                }

                // Benutzer mit E-Mail und Passwort erstellen
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                showProgress(false); // Ladesanzeige ausblenden
                                if (task.isSuccessful()) {
                                    // Benutzer erfolgreich erstellt
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        updateDisplayName(user, name); // Anzeigenamen aktualisieren
                                    }
                                    Toast.makeText(Register.this, R.string.account_created,
                                            Toast.LENGTH_SHORT).show();

                                    // Zum Login wechseln
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Authentifizierung fehlgeschlagen
                                    Toast.makeText(Register.this, R.string.authentication_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // Methode zur Aktualisierung des Anzeigenamens des Benutzers
    private void updateDisplayName(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, R.string.profile_updated, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // Methode zur Anzeige und Ausblendung der Ladesanzeige auf dem Button
    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE); // Ladesanzeige einblenden
            buttonReg.setText(""); // Text des Registrierungsbuttons entfernen
        } else {
            progressBar.setVisibility(View.GONE); // Ladesanzeige ausblenden
            buttonReg.setText(R.string.register); // Text des Registrierungsbuttons setzen
        }
    }
}
