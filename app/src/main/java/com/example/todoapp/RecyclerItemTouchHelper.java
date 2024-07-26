package com.example.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.Adapter.ToDoAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ToDoAdapter adapter; // Adapter für die To-Do-Liste

    // Konstruktor, der den Adapter als Parameter entgegennimmt
    public RecyclerItemTouchHelper(ToDoAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); // Konfiguration für horizontales Wischen
        this.adapter = adapter; // Adapter setzen
    }

    // Wird aufgerufen, wenn ein Item verschoben wird (hier nicht verwendet)
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    // Wird aufgerufen, wenn ein Item gewischt wird
    @Override
    public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition(); // Position des gewischten Items
        if (direction == ItemTouchHelper.LEFT) { // Wenn nach links gewischt wird
            // Dialog zur Bestätigung der Löschung anzeigen
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext());
            builder.setTitle(R.string.delete_task); // Titel des Dialogs
            builder.setMessage(R.string.delete_task_message); // Nachricht des Dialogs
            builder.setPositiveButton(R.string.Confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.deleteItem(position); // Item löschen, wenn bestätigt wird
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition()); // Swipe-Aktion rückgängig machen
                }
            });

            // Listener für Abbrechen des Dialogs
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition()); // Swipe-Aktion rückgängig machen
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCancelable(true); // Dialog kann abgebrochen werden
            dialog.setCanceledOnTouchOutside(true); // Löschen wird abgebrochen, wenn außerhalb des Dialogs geklickt wird
            dialog.show();

        } else { // Wenn nach rechts gewischt wird
            adapter.editItem(position); // Item bearbeiten
        }
    }

    // Zeichnet die Swipe-Indikatoren (z.B. Symbole und Hintergründe)
    @Override
    public void onChildDraw(Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        Drawable icon;
        ColorDrawable background;

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; // Offset für abgerundete Ecken des Hintergrunds

        if (dX > 0) { // Wenn nach rechts gewischt wird
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_edit); // Bearbeiten-Symbol
            background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.colorPrimaryDark)); // Hintergrundfarbe für Bearbeiten
        } else { // Wenn nach links gewischt wird
            icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_baseline_delete); // Löschen-Symbol
            background = new ColorDrawable(Color.RED); // Hintergrundfarbe für Löschen
        }

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2; // Berechnung des Symbolrandes
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2; // Symbol oben
        int iconBottom = iconTop + icon.getIntrinsicHeight(); // Symbol unten

        if (dX > 0) { // Wenn nach rechts gewischt wird
            int iconLeft = itemView.getLeft() + iconMargin; // Symbol links
            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth(); // Symbol rechts
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom); // Symbol-Position setzen

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom()); // Hintergrund-Position setzen
        } else if (dX < 0) { // Wenn nach links gewischt wird
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth(); // Symbol links
            int iconRight = itemView.getRight() - iconMargin; // Symbol rechts
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom); // Symbol-Position setzen

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(),
                    itemView.getRight(), itemView.getBottom()); // Hintergrund-Position setzen
        } else {
            background.setBounds(0, 0, 0, 0); // Hintergrund zurücksetzen
        }

        background.draw(c); // Hintergrund zeichnen
        icon.draw(c); // Symbol zeichnen
    }
}