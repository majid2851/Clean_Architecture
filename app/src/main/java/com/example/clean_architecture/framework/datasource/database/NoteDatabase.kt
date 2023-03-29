package com.example.clean_architecture.framework.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingwithmitch.cleannotes.framework.datasource.cache.database.NoteDao
import com.example.clean_architecture.framework.datasource.cache.model.NoteCacheEntity

@Database(entities = [NoteCacheEntity::class], version = 1)
abstract class NoteDatabase:RoomDatabase()
{
    abstract fun noteDao():NoteDao

    companion object{
        const val DATABSE_NAME:String="note_db"
    }







}