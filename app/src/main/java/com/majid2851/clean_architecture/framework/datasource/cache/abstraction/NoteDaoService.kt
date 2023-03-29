package com.majid2851.clean_architecture.framework.datasource.cache.abstraction

import com.majid2851.clean_architecture.framework.datasource.cache.database.NOTE_PAGINATION_PAGE_SIZE
import com.majid2851.clean_architecture.business.domain.model.Note


interface NoteDaoService {

    suspend fun insertNote(note: Note): Long

    suspend fun insertNotes(notes: List<Note>): LongArray

    suspend fun searchNoteById(id: String): Note?

    suspend fun updateNote(
        primaryKey: String,
        title: String,
        body: String?
    ): Int

    suspend fun deleteNote(primaryKey: String): Int

    suspend fun deleteNotes(notes: List<Note>): Int

    suspend fun searchNotes(): List<Note>

    suspend fun getAllNotes():List<Note>

    suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun getNumNotes(): Int

    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note>
}