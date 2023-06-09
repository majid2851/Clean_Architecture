package com.majid2851.clean_architecture.framework.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.majid2851.clean_architecture.framework.datasource.cache.database.NoteDao
import com.majid2851.clean_architecture.framework.datasource.cache.model.NoteCacheEntity

@Database(entities = [NoteCacheEntity::class], version = 1)
abstract class NoteDatabase:RoomDatabase()
{
    abstract fun noteDao():NoteDao

    companion object{
        const val DATABSE_NAME:String="note_db"
    }







}