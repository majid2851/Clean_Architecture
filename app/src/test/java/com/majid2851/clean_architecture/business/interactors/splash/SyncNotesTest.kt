package com.majid2851.clean_architecture.business.interactors.splash

import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.notedetail.UpdateNote
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

/*
Test cases:
1. insertNetworkNotesIntoCache()
    a) insert a bunch of new notes into the cache
    b) perform the sync
    c) check to see that those notes were inserted into the network
2. insertCachedNotesIntoNetwork()
    a) insert a bunch of new notes into the network
    b) perform the sync
    c) check to see that those notes were inserted into the cache
3. checkCacheUpdateLogicSync()
    a) select some notes from the cache and update them
    b) perform sync
    c) confirm network reflects the updates
4. checkNetworkUpdateLogicSync()
    a) select some notes from the network and update them
    b) perform sync
    c) confirm cache reflects the updates
 */
class SyncNotesTest
{
    // system in test
    private val syncNotes: SyncNotes

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        syncNotes = SyncNotes(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
        )
    }
    @Test
    fun insertNetworkNotesIntoCache()
    {
//        a) insert a bunch of new notes into the cache
//        b) perform the sync
//        c) check to see that those notes were inserted into the network
        runBlocking()
        {
            //a-
            val notes=noteFactory.createNoteList(5)
            noteCacheDataSource.insertNotes(notes)

            //b-
            syncNotes.syncNotes()
            val allNotes=noteNetworkDataSource.getAllNotes()
            assertTrue{
                allNotes.containsAll(notes)
            }
        }
    }

    @Test
    fun insertCachedNotesIntoNetwork()
    {
//        a) insert a bunch of new notes into the network
//        b) perform the sync
//        c) check to see that those notes were inserted into the cache
//a-
        runBlocking()
        {
            val notes=noteFactory.createNoteList(5)
            noteNetworkDataSource.insertOrUpdateNotes(notes)

            //b-
            syncNotes.syncNotes()
            val allNotes=noteCacheDataSource.getAllNotes()
            assertTrue{
                allNotes.containsAll(notes)
            }
        }
    }

    @Test
    fun checkCacheUpdateLogicSync()
    {
//        a) select some notes from the cache and update them
//        b) perform sync
//        c) confirm network reflects the updates
        runBlocking()
        {
              val note=noteCacheDataSource.getAllNotes().get(0)
              noteCacheDataSource.updateNote(note.id,"majid2851","body2851")
              syncNotes.syncNotes()
             val updateNote=noteCacheDataSource.getAllNotes().get(0)
             assertTrue{
                 updateNote.title.equals("majid2851")
             }

            val networkUpdateNote= noteNetworkDataSource.getAllNotes().get(0)
            assertTrue {
               networkUpdateNote.title.equals("majid2851")
            }

        }
    }

    @Test
    fun checkNetworkUpdateLogicSync()
    {
//        a) select some notes from the network and update them
//        b) perform sync
//        c) confirm cache reflects the updates
        runBlocking()
        {
            val note=noteNetworkDataSource.getAllNotes().get(0)
            val networkNote=noteFactory.createSingleNote(note.id,"majid2851","body2851")
            noteNetworkDataSource.insertOrUpdateNote(networkNote)
            syncNotes.syncNotes()
            val updateNote=noteNetworkDataSource.getAllNotes().get(0)
            assertTrue{
                updateNote.title.equals("majid2851")
            }

            val cacheUpdateNote= noteCacheDataSource.getAllNotes().get(0)
            assertTrue {
                cacheUpdateNote.title.equals("majid2851")
            }

        }

    }








}