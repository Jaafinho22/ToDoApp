package com.example.todoapp.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TODO_DATABASE";
    private static final String TABLE_NAME = "TODO_TABLE";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TASK";
    private static final String COL_3 = "STATUS";

    // SQL-Befehl zur Erstellung der Tabelle
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_2 + " TEXT, "
            + COL_3 + " INTEGER)";

    private SQLiteDatabase db;

    // Konstruktor für den DatabaseHandler
    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    // Methode zur Erstellung der Tabelle beim ersten Ausführen der App
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);
    }

    // Methode zum Aktualisieren der Datenbankstruktur beim Upgrade der App-Version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Öffnet die Datenbankverbindung zum Schreiben
    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    // Methode zum Einfügen einer neuen Aufgabe in die Datenbank
    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(COL_2, task.getTask()); //Task Name hinzufügen
        cv.put(COL_3, 0); //Task Status hinzufügen (0 = nicht erledigt)
        db.insert(TABLE_NAME, null, cv); //Task hinzufügen
    }

    // Methode zum Abrufen aller Aufgaben aus der Datenbank
    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();  // Liste zur Speicherung der Aufgaben
        Cursor cur = null;

        db.beginTransaction();
        try {
            // Ausführen der Abfrage, um alle Spalten und Zeilen der Tabelle abzurufen
            cur = db.query(TABLE_NAME, null, null, null, null, null, null, null);

            // Überprüfen, ob mindestens eine Zeile zurückgegeben wurde
            if(cur.moveToFirst()){
                do {
                    // Erstellen eines neuen ToDoModel-Objekts für jede Zeile in der Abfrage
                    ToDoModel task = new ToDoModel();

                    // Setzen der Werte für das ToDoModel-Objekt
                    task.setId(cur.getInt(cur.getColumnIndex(COL_1)));  // ID aus der Spalte "id"
                    task.setTask(cur.getString(cur.getColumnIndex(COL_2)));  // Aufgabentext aus der Spalte "task"
                    task.setStatus(cur.getInt(cur.getColumnIndex(COL_3)));  // Status aus der Spalte "status"

                    // Hinzufügen des ToDoModel-Objekts zur Liste taskList
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }

        return taskList;  // Rückgabe der Liste mit allen abgerufenen ToDoModel-Objekten
    }


    // Methode zum Aktualisieren des Status einer Aufgabe
    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(COL_3, status); //Task Status setzen
        db.update(TABLE_NAME, cv, "ID=?", new String[] {String.valueOf(id)}); //Task Status wird geupdated
    }

    // Methode zum Aktualisieren des Texts einer Aufgabe
    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(COL_2, task); //Task Text setzen
        db.update(TABLE_NAME, cv, "ID=?", new String[] {String.valueOf(id)}); //Task Text wird geupdated
    }

    // Methode zum Löschen einer Aufgabe aus der Datenbank
    public void deleteTask(int id){
        db.delete(TABLE_NAME, "ID=?", new String[] {String.valueOf(id)}); //Löschen der Task
    }
}