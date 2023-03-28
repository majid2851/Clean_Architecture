package com.example.clean_architecture.business.interactors.splash

import DataState
import com.codingwithmitch.cleannotes.business.data.network.ApiResponseHandler
import com.example.clean_architecture.business.data.cache.CacheResponseHandler
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class SyncDeletedNotesTest()
{


    /*
    Test cases:
    1. deleteNetworkNotes_confirmCacheSync()
        a) select some notes for deleting from network
        b) delete from network
        c) perform sync
        d) confirm notes from cache were deleted
     */

    private val syncDeletedNotes: SyncDeletedNotes
    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory
    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        syncDeletedNotes = SyncDeletedNotes(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
        )
    }

    @Test
    fun deleteNetworkNotes_confirmCacheSync()
    {
        runBlocking()
        {
            val allNotes=noteNetworkDataSource.getAllNotes()
            val someNotes:ArrayList<Note> =ArrayList()
            for (i in 0..4){
                someNotes.add(allNotes.get(i))
            }

            for (note in someNotes){
                noteNetworkDataSource.deleteNote(note.id)
            }

            syncDeletedNotes.syncDeletedNotes()

            val allCachedNotes=noteCacheDataSource.getAllNotes()
            assertFalse{
                allCachedNotes.containsAll(someNotes)
            }

        }




    }












}