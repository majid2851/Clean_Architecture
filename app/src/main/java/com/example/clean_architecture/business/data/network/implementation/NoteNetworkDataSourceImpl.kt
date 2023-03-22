package com.example.clean_architecture.business.data.network.implementation

import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteNetworkDataSourceImpl
    @Inject constructor(private val fireStoreService:NoteFireStoreService)
    :NoteNetworkDataSource
{
    override suspend fun insertOrUpdateNote(note: Note) {
        fireStoreService.insertOrUpdateNote(note)
    }

    override suspend fun deleteNote(primaryKey: String) {
        fireStoreService.deleteNote(primaryKey)
    }

    override suspend fun insertDeletedNote(note: Note) {
        fireStoreService.insertDeletedNote(note)
    }

    override suspend fun insertMoreDeletedNotes(notes: List<Note>) {
        fireStoreService.insertMoreDeletedNotes(notes)
    }

    override suspend fun deleteDeletedNote(note: Note) {
        fireStoreService.deleteDeletedNote(note)
    }

    override suspend fun getDeletedNote(): List<Note> {
        return fireStoreService.getDeletedNote()
    }

    override suspend fun deleteAllNotes() {
        fireStoreService.deleteAllNotes()
    }

    override suspend fun searchNote(note: Note): Note {
        return fireStoreService.searchNote(note)
    }

    override suspend fun getAllNotes(): List<Note> {
        return fireStoreService.getAllNotes()
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
        fireStoreService.insertOrUpdateNotes(notes)
    }


}