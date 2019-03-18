package id.uchidd.notesapp.view;

import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.uchidd.notesapp.R;
import id.uchidd.notesapp.database.DatabaseHelper;
import id.uchidd.notesapp.database.model.Note;

public class HomeActivity extends AppCompatActivity {

    //TODO Step 1
    private NotesAdapter notesAdapter;
    private List<Note> noteList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView rv_listnote;
    private TextView noNotesView;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //TODO Step 2
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.clMain);
        rv_listnote = (RecyclerView) findViewById(R.id.rvMain);
        noNotesView = (TextView) findViewById(R.id.tvNoFound);

        db = new DatabaseHelper(this);
        noteList.addAll(db.getAllNotes());

        FloatingActionButton fab_newnote = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab_newnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Step 7
                showNoteDialog(false, null, -1);
            }
        });

        //TODO Step 8
        notesAdapter = new NotesAdapter(this, noteList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_listnote.setLayoutManager(layoutManager);

        rv_listnote.setAdapter(notesAdapter);

        toggleEmptyNote();
    }

    //TODO Step 3
    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int postion) {
        LayoutInflater layoutInflaterAddNote = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAddNote.inflate(R.layout.add_note_dialog, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeActivity.this);
        alertBuilder.setView(view);

        final EditText getInputNote = view.findViewById(R.id.etNewNote);
        TextView dialogTitle = view.findViewById(R.id.tvDialogAddTitle);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.add_set) : getString(R.string.edit_set));

        if (shouldUpdate && note != null) {
            getInputNote.setText(note.getNote());
        }

        alertBuilder.setCancelable(false)
                .setPositiveButton(shouldUpdate ? "UPDATE" : "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                });

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getInputNote.getText().toString())) {
                    Toast.makeText(HomeActivity.this, "Gak mau!!! Isi dulu!!! :P", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                if (shouldUpdate && note != null) {
                    //TODO Step 4
                    updateNote(getInputNote.getText().toString(), postion);
                } else {
                    //TODO Step 6
                    createNote(getInputNote.getText().toString());
                }
            }
        });
    }

    private void createNote(String note) {
        long id = db.insertNote(note);

        Note getIdNote = db.getNote(id);

        if (getIdNote != null){
            noteList.add(0, getIdNote);
            notesAdapter.notifyDataSetChanged();
            toggleEmptyNote();
        }
    }

    private void updateNote(String note, int position) {
        Note getNoteList = noteList.get(position);

        getNoteList.setNote(note);
        db.updateNote(getNoteList);

        noteList.set(position, getNoteList);
        notesAdapter.notifyItemChanged(position);

        //TODO Step 5
        toggleEmptyNote();
    }

    private void toggleEmptyNote() {
        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

}
