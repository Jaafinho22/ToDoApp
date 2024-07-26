package com.example.todoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.todoapp.Adapter.ToDoAdapter;
import com.example.todoapp.Model.ToDoModel;
import com.example.todoapp.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    FirebaseAuth auth; //Firebase Authentifizierung
    FirebaseUser user; //Firebase Nutzer
    ImageButton settingsBtn; //Einstellungs-Button

    public ToDoAdapter tasksAdapter;

    // Liste der Aufgaben und Datenbankhandler
    private List<ToDoModel> taskList;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase-Authentifizierung initialisieren
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Einstellungen-Button initialisieren
        settingsBtn = findViewById(R.id.settings);

        // Überprüfen, ob der Nutzer eingeloggt ist
        if (user == null) {
            // Wenn nicht, zum Login weiterleiten
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        // Klick-Listener für den Einstellungen-Button
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Zur Einstellungen wechseln
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                finish();
            }
        });

        // Datenbankhandler initialisieren und Datenbank öffnen
        db = new DatabaseHandler(this);
        db.openDatabase();

        // Aufgabenliste initialisieren
        taskList = new ArrayList<>();

        // RecyclerView initialisieren und Layout-Manager setzen
        // RecyclerView, Adapter und Floating Action Button
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Adapter initialisieren und RecyclerView setzen
        tasksAdapter = new ToDoAdapter(db, this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        // ItemTouchHelper für Swipe-Operationen setzen
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        // Task hinzufügen Button initialisieren
        FloatingActionButton fab = findViewById(R.id.fab);

        // Aufgabenliste aus der Datenbank holen und umgekehrt sortieren
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        // Klick-Listener für den Task hinzufügen Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Neues Task-Dialogfragment anzeigen
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);

            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        // Aufgabenliste aus der Datenbank neu holen und umgekehrt sortieren
        taskList = db.getAllTasks();

        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList); // Aktualisiere den Adapter mit der neuen Liste
    }
}