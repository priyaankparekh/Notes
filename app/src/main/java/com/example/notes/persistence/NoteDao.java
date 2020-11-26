package com.example.notes.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notes.models.NotesModel;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    long[] insertNotes(NotesModel... notesModels);

    @Query("SELECT * FROM notes")
    LiveData<List<NotesModel>> getNotes();

    @Update
    int update(NotesModel... notesModels);

    @Delete
    int delete(NotesModel... notesModels);

}
