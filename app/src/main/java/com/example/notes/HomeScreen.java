package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.notes.adapters.NotesRecyclerAdapter;
import com.example.notes.models.NotesModel;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity implements NotesRecyclerAdapter.OnNoteListener {

    private RecyclerView mRecyclerView;

    private ArrayList<NotesModel> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNoteRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        initRecyclerView();
        insertFakeNotes();
    }

    private void insertFakeNotes()
    {
        for(int i = 0;i<1000;i++)
        {
            NotesModel note = new NotesModel();
            note.setTitle("Title # "+ i);
            note.setContent("Content # "+i);
            note.setTimestamp("Nov 2020");
            mNotes.add(note);
        }
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView()
    {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);
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
}