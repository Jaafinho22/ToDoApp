// Deklaration des Pakets und der benötigten Importe
package com.example.todoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    // Deklaration der UI-Elemente und Firebase-Instanzen
    TextView userName; // TextView für den Benutzernamen
    TextView userDetails; // TextView für die Benutzer-Details
    FirebaseUser user; // FirebaseUser-Objekt zur Verwaltung des aktuellen Benutzers
    FirebaseAuth auth; // FirebaseAuth-Instanz zur Authentifizierung
    ImageButton backBtn; // ImageButton für die Zurück-Navigation

    @Override
    public void onStart() {
        super.onStart();
        // Überprüfen, ob der Benutzer angemeldet ist und die UI entsprechend aktualisieren
        user = auth.getCurrentUser(); // Aktuellen Benutzer abrufen

        if (user == null) {
            // Wenn der Benutzer nicht angemeldet ist, zum Login weiterleiten
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Benutzername und UID anzeigen
            userName.setText(user.getDisplayName()); // Benutzernamen setzen
            userDetails.setText(user.getUid()); // Benutzer-UID setzen
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // Layout der Aktivität setzen

        // FirebaseAuth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente finden
        backBtn = findViewById(R.id.backButton); // Zurück-Button
        userName = findViewById(R.id.profile_name); // TextView für den Benutzernamen
        userDetails = findViewById(R.id.profile_details); // TextView für die Benutzer-Details

        // Klick-Listener für die Logout-Option setzen
        RelativeLayout logoutOption = findViewById(R.id.logout_option); // RelativeLayout für Logout
        logoutOption.setOnClickListener(v -> showLogoutConfirmationDialog()); // Klick-Listener setzen

        // Klick-Listener für die Sprachoption setzen
        RelativeLayout languageOption = findViewById(R.id.language_option); // RelativeLayout für die Sprachoption
        languageOption.setOnClickListener(v -> showLanguageSelectionDialog()); // Klick-Listener setzen

        // Klick-Listener für die Zurück-Schaltfläche setzen
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); // Intent zur MainActivity (Hauptseite)
            startActivity(intent); // Starten der MainActivity (Hauptseite)
            finish(); // Aktuelle Aktivität beenden
        });

        // Klick-Listener für Dark Mode
        Switch darkModeSwitch = findViewById(R.id.dark_mode_switch); // Dark Mode Switch
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // Dark Mode aktivieren
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // Dark Mode deaktivieren
            }
        });

        // Klick-Listener für Profil bearbeiten
        RelativeLayout editProfileOption = findViewById(R.id.edit_profile); // RelativeLayout für Profil bearbeiten
        editProfileOption.setOnClickListener(v -> showEditProfileDialog()); // Klick-Listener setzen

        // Klick-Listener für Passwort ändern
        RelativeLayout changePasswordOption = findViewById(R.id.change_password); // RelativeLayout für Passwort ändern
        changePasswordOption.setOnClickListener(v -> showChangePasswordDialog()); // Klick-Listener setzen
    }

    // Dialog zur Sprachauswahl anzeigen
    private void showLanguageSelectionDialog() {
        final String[] languages = {"English", "Deutsch"}; // Sprachoptionen

        new AlertDialog.Builder(this)
                .setTitle(R.string.language_message) // Titel des Dialogs
                .setItems(languages, new DialogInterface.OnClickListener() { // Sprachauswahl-Dialog
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Sprache entsprechend der Auswahl setzen
                        String selectedLanguage = languages[which];
                        if (selectedLanguage.equals("Deutsch")) {
                            setLocale("de"); // Deutsch setzen
                        } else {
                            setLocale("en"); // Englisch setzen
                        }
                    }
                })
                .show(); // Dialog anzeigen
    }

    // Methode zur Änderung der Sprache
    private void setLocale(String lang) {
        Locale locale = new Locale(lang); // Neues Locale-Objekt mit der gewählten Sprache
        Locale.setDefault(locale); // Standard-Locale setzen
        Resources resources = getResources(); // Ressourcen abrufen
        Configuration config = new Configuration(resources.getConfiguration()); // Konfiguration abrufen
        config.setLocale(locale); // Sprache setzen
        resources.updateConfiguration(config, resources.getDisplayMetrics()); // Konfiguration aktualisieren

        // Aktivität neu starten, um die Änderungen anzuwenden
        Intent refresh = new Intent(this, Settings.class); // Intent zur aktuellen Aktivität
        startActivity(refresh); // Aktivität neu starten
        finish(); // Aktuelle Aktivität beenden
    }

    // Dialog zur Abmeldebestätigung anzeigen
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout) // Titel des Dialogs
                .setMessage(R.string.logout_message) // Nachricht im Dialog
                .setPositiveButton(R.string.logout, (dialog, which) -> {
                    // Abmelden durchführen
                    FirebaseAuth.getInstance().signOut(); // Benutzer abmelden
                    Intent intent = new Intent(getApplicationContext(), Login.class); // Intent zur Login-Aktivität
                    startActivity(intent); // Login-Aktivität starten
                    finish(); // Aktuelle Aktivität beenden
                })
                .setNegativeButton(R.string.cancel, null) // Negativer Button ohne Aktion
                .show(); // Dialog anzeigen
    }

    // Dialog zum Bearbeiten des Profilnames
    private void showEditProfileDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null); // Lässt das Layout dialog_edit_profile.xml im Popup anzeigen
        EditText editDisplayName = dialogView.findViewById(R.id.edit_display_name); // EditText für den neuen Anzeigennamen im Dialog finden

        // AlertDialog erstellen
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_profile) // Titel setzen
                .setView(dialogView) // Dialog-Layout setzen
                .setPositiveButton(R.string.save, null) // Save Button setzen (null, damit wir später den Klick-Listener setzen können)
                .setNegativeButton(R.string.cancel, null) // Cancel Button setzen
                .create();

        // Dialog anzeigen
        dialog.show();

        // Save Button standardmäßig deaktivieren
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // TextWatcher hinzufügen, um den Zustand des Save-Buttons basierend auf der Texteingabe zu steuern
        editDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Save-Button aktivieren, wenn der EditText nicht leer ist
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> { // Klick-Listener für den Save-Button setzen
            String newDisplayName = editDisplayName.getText().toString(); // Neuen Anzeigennamen aus dem EditText abrufen
            if (!newDisplayName.isEmpty()) {

                FirebaseUser user = auth.getCurrentUser(); // Aktuellen Benutzer abrufen
                if (user != null) {

                    user.updateProfile(new UserProfileChangeRequest.Builder() // Benutzerprofil mit dem neuen Anzeigennamen aktualisieren
                                    .setDisplayName(newDisplayName)
                                    .build())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Benutzername in der UI aktualisieren und Dialog schließen
                                    userName.setText(newDisplayName);
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Username updated", Toast.LENGTH_SHORT).show(); // Erfolgsmeldung anzeigen
                                } else {
                                    Toast.makeText(getApplicationContext(), "Update username failed", Toast.LENGTH_SHORT).show(); // Fehlermeldung anzeigen
                                }
                            });
                }
            }
        });
    }

    // Dialog zum Ändern des Passworts anzeigen
    private void showChangePasswordDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null); // Lässt das Layout dialog_change_password.xml im Popup anzeigen
        EditText editNewPassword = dialogView.findViewById(R.id.edit_new_password); // EditText für das neue Passwort im Dialog finden

        // AlertDialog erstellen
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.change_password) // Titel setzen
                .setView(dialogView) // Dialog-Layout setzen
                .setPositiveButton(R.string.save, null) // Save Button setzen (null, damit wir später den Klick-Listener setzen können)
                .setNegativeButton(R.string.cancel, null) // Cancel Button setzen
                .create();

        // Dialog anzeigen
        dialog.show();

        // Save Button standardmäßig deaktivieren
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // TextWatcher hinzufügen, um den Zustand des Save-Buttons basierend auf der Texteingabe zu steuern
        editNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Save-Button aktivieren, wenn der EditText nicht leer ist
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Klick-Listener für den Save-Button setzen
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newPassword = editNewPassword.getText().toString(); // Neues Passwort aus dem EditText abrufen
            if (!newPassword.isEmpty()) {

                FirebaseUser user = auth.getCurrentUser(); // Aktuellen Benutzer abrufen
                if (user != null) {

                    user.updatePassword(newPassword) // Passwort des Benutzers aktualisieren
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Dialog schließen und Erfolgsmeldung anzeigen
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Password updated", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(getApplicationContext(), "Update password failed", Toast.LENGTH_SHORT).show(); // Fehlermeldung anzeigen
                                }
                            });
                }
            }
        });
    }

}