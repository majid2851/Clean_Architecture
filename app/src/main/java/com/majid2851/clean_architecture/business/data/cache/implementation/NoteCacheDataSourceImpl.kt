package com.majid2851.clean_architecture.business.data.cache.implementation

import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.framework.datasource.cache.abstraction.NoteDaoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteCacheDataSourceImpl @Inject
    constructor(private val noteDaoService:NoteDaoService):
    com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
{
    override suspend fun insertNote(note: Note): Long {
        return noteDaoService.insertNote(note)
    }

    override suspend fun deleteNote(primary: String): Int {
        return noteDaoService.deleteNote(primary)
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteDaoService.getAllNotes()
    }

    override suspend fun deleteMoreNotes(notes: List<Note>): Int {
        return noteDaoService.deleteNotes(notes)
    }

    override suspend fun updateNote(primaryKey: String, newTitle: String, newBody: String): Int {
        return noteDaoService.updateNote(primaryKey,newTitle,newBody)
    }

    override suspend fun searchNotes(query: String, filterAndOrder: String, page: Int): List<Note> {
        return noteDaoService.searchNotes()
    }


    override suspend fun searchNoteById(primaryKey: String): Note? {
        return noteDaoService.searchNoteById(primaryKey)
    }

    override suspend fun getNumNotes(): Int {
        return noteDaoService.getNumNotes()
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDaoService.insertNotes(notes)
    }


}