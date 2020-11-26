package com.example.notes.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.notes.async.DeleteAsyncTask;
import com.example.notes.async.InsertAsyncTask;
import com.example.notes.async.UpdateAsyncTask;
import com.example.notes.models.NotesModel;

import java.util.List;

//Repository classes are most effective when there are multiple data sources
//This app only contains 1 - SQLite DB
public class NoteRepository {

    private NoteDatabase mNoteDatabse;

    public NoteRepository(Context context) {
        mNoteDatabse = NoteDatabase.getInstance(context);
    }

    public void insertNoteTask(NotesModel notesModel){
        new InsertAsyncTask(mNoteDatabse.getNoteDao()).execute(notesModel);
    }

    public void updateNote(NotesModel notesModel){
        new UpdateAsyncTask(mNoteDatabse.getNoteDao()).execute(notesModel);
    }

    public LiveData<List<NotesModel>> retrieveNotesTask(){
        return mNoteDatabse.getNoteDao().getNotes();
    }

    public void deleteNote(NotesModel notesModel){
        new DeleteAsyncTask(mNoteDatabse.getNoteDao()).execute(notesModel);
    }
}
