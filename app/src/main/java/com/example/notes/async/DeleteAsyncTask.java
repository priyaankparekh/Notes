package com.example.notes.async;

import android.os.AsyncTask;

import com.example.notes.models.NotesModel;
import com.example.notes.persistence.NoteDao;

//Async Task is useful for executing a single job on a BG Thread and destroyed when the task is completed
public class DeleteAsyncTask extends AsyncTask<NotesModel, Void, Void> {

    private NoteDao mNoteDao;

    public DeleteAsyncTask(NoteDao dao) {
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(NotesModel... notesModels) {
        mNoteDao.delete(notesModels);
        return null;
    }




}
