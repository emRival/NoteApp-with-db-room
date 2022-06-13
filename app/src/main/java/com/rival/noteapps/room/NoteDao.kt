package com.rival.noteapps.room

import androidx.room.*

@Dao
interface NoteDao {

    @Insert
    suspend fun createNote(note:Note)

    @Update
    suspend fun updateNote(note:Note)

    @Delete
    suspend fun deleteNote(note:Note)

    @Query("SELECT * FROM note ORDER BY id DESC")
    suspend fun getAllNotes():List<Note>

    @Query("SELECT * FROM note WHERE id = :note_id")
    suspend fun getAllNote(note_id:Int):List<Note>
}