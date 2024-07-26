package com.example.todoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.todoapp.Adapter.ToDoAdapter;
import com.example.todoapp.Model.ToDoModel;
import com.example.todoapp.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;

    private DatabaseHandler db;

    // Statische Methode zur Erstellung einer neuen Instanz von AddNewTask
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Erstellen der Ansicht wenn man eine neue Task erstellt
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);

        // Einstellen der Soft-Input-Mode, um die Größe des Dialogs anzupassen
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    //Wenn die Ansicht erstellt wurde
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisierung der UI-Elemente aus der Layout-Datei
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskSaveButton = requireView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");

            // Setzen des Texts im Textfeld
            newTaskText.setText(task);
        }

        // Initialisierung der lokalen Datenbank
        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        // TextWatcher, um Änderungen im Textfeld zu überwachen und den Button entsprechend zu aktualisieren
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { //Wird nicht benutzt
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Überprüfen, ob der Text im Textfeld leer ist
                if(s.toString().trim().isEmpty()){
                    // Deaktivieren des Speichern-Buttons und Ändern der Textfarbe auf Grau
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    // Aktivieren des Speichern-Buttons und Ändern der Textfarbe auf die Primärfarbe
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { //Wird nicht benutzt
            }
        });

        final boolean finalIsUpdate = isUpdate;
        // OnClickListener für den Speichern-Button
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrufen des Texts aus dem Textfeld und Entfernen führender und endender Leerzeichen
                String text = newTaskText.getText().toString().trim();
                if (!text.isEmpty()) {
                    // Wenn es sich um eine Aktualisierung handelt, die Aufgabe aktualisieren
                    if (finalIsUpdate) {
                        db.updateTask(bundle.getInt("id"), text);
                        ((MainActivity) requireActivity()).tasksAdapter.notifyDataSetChanged();
                    } else { // Ansonsten eine neue Aufgabe einfügen
                        ToDoModel task = new ToDoModel();
                        task.setTask(text); // Task Name wird gesetzt
                        task.setStatus(0); // Task Status wird gesetzt (0 = Nicht erledigt)
                        db.insertTask(task); // Task wird der Lokalen Datenbank hinzugefügt
                        ((MainActivity) requireActivity()).tasksAdapter.addTask(task);
                    }
                    dismiss(); // Dialog schließen
                }
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)
            ((DialogCloseListener)activity).handleDialogClose(dialog);
    }
}