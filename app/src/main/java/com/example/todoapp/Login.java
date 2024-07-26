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

public class Login extends AppCompatActivity {

    // Deklaration der UI-Elemente
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        // Überprüfen, ob der Benutzer bereits angemeldet ist
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // Wenn bereits angemeldet, weiter zur Hauptseite
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisierung von Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Zuweisen der UI-Elemente aus dem Layout
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        // Klick-Listener für den "Registrieren"-Text, um zur Registrierung zu wechseln
        textView.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        // Klicklistener für den Login-Button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true); // Ladesanzeige einblenden

                // Eingaben für E-Mail und Passwort holen
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Eingaben überprüfen
                if (TextUtils.isEmpty(email)){ //Wenn Email fehlt, Fehlermeldung anzeigen
                    showProgress(false); // Ladesanzeige ausblenden
                    Toast.makeText(Login.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){ //Wenn Passwort fehlt, Fehlermeldung anzeigen
                    showProgress(false); // Ladesanzeige ausblenden
                    Toast.makeText(Login.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase Authentication für den Login-Vorgang
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Bei erfolgreichem Login zur Hauptseite wechseln
                                    Toast.makeText(getApplicationContext(), R.string.login_successful, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Bei fehlgeschlagenem Login eine Fehlermeldung anzeigen
                                    Toast.makeText(Login.this, R.string.authentication_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // Methode zur Anzeige und Ausblendung der Ladesanzeige auf dem Button
    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE); // Ladesanzeige einblenden
            buttonLogin.setText(""); // Text des Registrierungsbuttons entfernen
        } else {
            progressBar.setVisibility(View.GONE); // Ladesanzeige ausblenden
            buttonLogin.setText(R.string.login); // Text des Registrierungsbuttons setzen
        }
    }
}
