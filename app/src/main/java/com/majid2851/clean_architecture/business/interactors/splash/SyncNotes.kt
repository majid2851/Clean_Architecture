package com.majid2851.clean_architecture.business.interactors.splash

import DataState
import com.codingwithmitch.cleannotes.business.data.network.ApiResponseHandler
import com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncNotes(
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource,
    private val noteNetworkDataSource:NoteNetworkDataSource,
)
{
    suspend fun syncNotes()
    {
        val cachedNoteList=getCachedNotes()

        syncNetworkNoteWithCachedNotes(ArrayList(cachedNoteList))

    }
    private suspend fun getCachedNotes():List<Note>
    {
        val cacheResult= safeCacheCall()
        {
            noteCacheDataSource.getAllNotes()
        }
        val response=object :
            com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler<List<Note>, List<Note>>(
            response = cacheResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>
            {
                return DataState.data(
                    response = null,
                    data=resultObj,
                    stateEvent = null
                )
            }
        }.getResult()


        return response.data?:ArrayList()
    }


    // get all notes from network
    // if they do not exist in cache, insert them
    // if they do exist in cache, make sure they are up to date
    // while looping, remove notes from the cachedNotes list. If any remain, it means they
    // should be in the network but aren't. So insert them.
    private suspend fun syncNetworkNoteWithCachedNotes(
        cachedNotes:ArrayList<Note>,
    )
    {
        return withContext(IO)
        {
            val networkResult= safeApiCall {
                noteNetworkDataSource.getAllNotes()
            }
            val response=object :ApiResponseHandler<List<Note>,List<Note>>(
                response = networkResult,
                stateEvent = null
            ){
                override suspend fun handleSuccess(resultObj: List<Note>): DataState<List<Note>>?
                {
                    return DataState.data(
                        response = null,
                        data=resultObj,
                        stateEvent = null
                    )
                }

            }.getResult()
            val noteList=response?.data?:ArrayList()
            val job=launch()
            {
                for(note in noteList)
                {
                    noteCacheDataSource.searchNoteById(note.id)?.let {cachedNote->
                        cachedNotes.remove(cachedNote)
                        checkIfCachedNoteRequiresUpdate(cachedNote=cachedNote, networkNote =note)
                    }?:noteCacheDataSource.insertNote(note)//if cachedNote doesn't exist in cacehdNotes
                }
            }
            job.join()//wait until that above for(note in noteLIst) be done
            //insert remaining into network
            for (cachedNote in cachedNotes)
            {
                safeApiCall {
                    noteNetworkDataSource.insertOrUpdateNote(cachedNote)
                }

            }

        }

    }

    private suspend fun checkIfCachedNoteRequiresUpdate(cachedNote:Note, networkNote: Note)
    {
        val cacheUpdatedAt=cachedNote.updated_at
        val networkNoteUpdatedAt=networkNote.updated_at

        if (networkNoteUpdatedAt>cacheUpdatedAt)
        {
            safeCacheCall()
            {
                noteCacheDataSource.updateNote(
                    primary = networkNote.id,
                    newTitle = networkNote.title,
                    newBody = networkNote.body
                )
            }
        }else
        {
            safeApiCall {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }



    }


}