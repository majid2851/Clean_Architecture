package com.majid2851.clean_architecture.business.data.network.abstraction

import com.majid2851.clean_architecture.business.domain.model.Note

interface NoteNetworkDataSource
{
    suspend fun insertOrUpdateNote(note:Note)
    suspend fun deleteNote(primaryKey:String)

    suspend fun insertDeletedNote(note:Note)

    suspend fun insertMoreDeletedNotes(notes:List<Note>)

    suspend fun deleteDeletedNote(note: Note)

    suspend fun getDeletedNote():List<Note>

    suspend fun deleteAllNotes()

    suspend fun searchNote(note: Note):Note?

    suspend fun getAllNotes():List<Note>

    suspend fun insertOrUpdateNotes(notes:List<Note>)


}