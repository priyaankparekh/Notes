package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.notes.adapters.NotesRecyclerAdapter;
import com.example.notes.models.NotesModel;
import com.example.notes.persistence.NoteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class HomeScreen extends AppCompatActivity implements
        NotesRecyclerAdapter.OnNoteListener,
        FloatingActionButton.OnClickListener
{

    private RecyclerView mRecyclerView;

    private ArrayList<NotesModel> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNoteRecyclerAdapter;

    private NoteRepository mNoteRepositoy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        findViewById(R.id.fab).setOnClickListener(this);

        mNoteRepositoy = new NoteRepository(this);

        initRecyclerView();
        retrieveNotes();
    }

    private void retrieveNotes(){
        mNoteRepositoy.retrieveNotesTask().observe(this, new Observer<List<NotesModel>>() {
            @Override
            //Any database calls using LiveData are ASYNCHRONOUS i.e they operate on BG Thread
            public void onChanged(List<NotesModel> notesModels) {
                if(mNotes.size() > 0)
                {
                    mNotes.clear();
                }
                if(notesModels != null)
                {
                    mNotes.addAll(notesModels);
                }
                mNoteRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initRecyclerView()
    {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mNoteRecyclerAdapter = new NotesRecyclerAdapter(mNotes,this);
        mRecyclerView.setAdapter(mNoteRecyclerAdapter);
    }

    @Override
    public void onNoteClick(int position) {

        Log.d("TAG", "onNoteClick: Clicked !"+position);

        Intent noteActivity = new Intent(this,NoteActivity.class);
        noteActivity.putExtra("selected_note",mNotes.get(position));
        startActivity(noteActivity);
    }

    @Override
    public void onClick(View v) {

        Intent newNote = new Intent(this,NoteActivity.class);
        startActivity(newNote);
    }

    private void deleteNote(NotesModel note)
    {
        mNotes.remove(note);
        mNoteRecyclerAdapter.notifyDataSetChanged();

        mNoteRepositoy.deleteNote(note);
    }

    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
        }
    };
}