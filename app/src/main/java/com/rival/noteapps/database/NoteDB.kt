package com.rival.noteapps.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rival.noteapps.MainActivity
import com.rival.noteapps.room.Note
import com.rival.noteapps.room.NoteDao

@Database(entities = [Note::class], version = 1)

abstract class NoteDB: RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var instance : NoteDB? = null
        private val LOCK = Any()

        operator fun invoke(context: MainActivity) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDB::class.java,
            "rival.db"
        ).build()
    }



}