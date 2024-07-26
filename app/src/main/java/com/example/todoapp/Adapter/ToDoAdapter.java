package com.example.todoapp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AddNewTask;
import com.example.todoapp.MainActivity;
import com.example.todoapp.Model.ToDoModel;
import com.example.todoapp.R;
import com.example.todoapp.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList; // Liste der ToDoModel-Objekte
    private final MainActivity activity; // Referenz auf die MainActivity
    private final DatabaseHandler db; // Referenz auf den DatabaseHandler

    // Konstruktor für den Adapter, der DatabaseHandler und MainActivity benötigt
    public ToDoAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    // Erstellt und gibt eine Instanz von ViewHolder zurück
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView); // Rückgabe eines neuen ViewHolders
    }

    // Bindet Daten an die Ansichtshalter im RecyclerView an
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position){
        db.openDatabase(); // Öffnen der Datenbankverbindung

        // Abrufen des aktuellen ToDoModel-Objekts aus der Liste
        final ToDoModel item = todoList.get(position);

        // Setzen des Texts und des Status der CheckBox
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));

        // Entferne den alten OnCheckedChangeListener, um falsche Aufrufe zu vermeiden
        holder.task.setOnCheckedChangeListener(null);

        // Setzen des OnCheckedChangeListener für die CheckBox
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Aktualisierung des Status in der Datenbank basierend auf Checked-Zustand
                if(isChecked){
                    db.updateStatus(item.getId(), 1); // Aktualisiere den Status in der Datenbank
                } else {
                    db.updateStatus(item.getId(), 0); // Aktualisiere den Status in der Datenbank

                }
                item.setStatus(isChecked ? 1 : 0);
            }
        });
    }

    // Gibt die Anzahl der Elemente im RecyclerView zurück
    @Override
    public int getItemCount(){
        return todoList.size();
    }

    // Konvertiert einen Integer-Wert in einen boolean-Wert
    private boolean toBoolean(int n){
        return n != 0;
    }

    // Setzt die Liste der ToDoModel-Objekte und aktualisiert den RecyclerView
    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList; // Setze die neue Liste von Aufgaben
        //notifyDataSetChanged(); // Benachrichtige den Adapter über die Datenänderung
    }



    public Context getContext() {
        return activity;
    }

    // Löscht ein Element aus der Liste und aktualisiert den RecyclerView
    public void deleteItem(int position){
        ToDoModel item = todoList.get(position); // Abrufen des zu löschenden ToDoModel-Objekts
        db.deleteTask(item.getId()); // Löschen der Aufgabe aus der Datenbank
        todoList.remove(position); // Entfernen der Aufgabe aus der Liste
        notifyItemRemoved(position); // Benachrichtigen, dass ein Element entfernt wurde
    }

    // Öffnet einen Dialog zur Bearbeitung einer Aufgabe
    public void editItem(int position){
        ToDoModel item = todoList.get(position); // Abrufen des zu bearbeitenden ToDoModel-Objekts
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId()); // Hinzufügen der Aufgaben-ID zum Bundle
        bundle.putString("task", item.getTask()); // Hinzufügen des Aufgabentexts zum Bundle

        // Erstellen einer Instanz von AddNewTask und Anzeigen des Dialogs
        AddNewTask task = new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        task.setCancelable(false);
    }

    public void addTask(ToDoModel task) {
        todoList.add(task);
        notifyItemInserted(0);
    }

    // Stellt die Ansichtshalter für jedes Element im RecyclerView dar
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task; // CheckBox für die Aufgabe

        // Konstruktor für ViewHolder, der die Ansichtsobjekte initialisiert
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox); // Initialisierung der CheckBox
        }
    }
}