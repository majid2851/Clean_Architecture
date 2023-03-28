package com.example.clean_architecture.business.interactors.splash

import DataState
import com.codingwithmitch.cleannotes.business.data.network.ApiResponseHandler
import com.example.clean_architecture.business.data.cache.CacheResponseHandler
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.util.printLogD

class SyncDeletedNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
)
{
    suspend fun syncDeletedNotes()
    {
        val apiResult= safeApiCall{
            noteNetworkDataSource.getDeletedNote()
        }
        val response=object
            :ApiResponseHandler<List<Note>,List<Note>>(
                response=apiResult,
                stateEvent = null
            )
        {
            override suspend fun handleSuccess(resultObj: List<Note>):
                    DataState<List<Note>>?
            {
                return DataState.data(
                    response = null,
                    data=resultObj,
                    stateEvent = null
                )
            }

        }.getResult()

        val notes=response?.data?:ArrayList()
        val cacheResult= safeCacheCall {
            noteCacheDataSource.deleteMoreNotes(notes)
        }

        //just for debuging nothing important
        object :CacheResponseHandler<Int,Int>(
            response=cacheResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObj: Int): DataState<Int>
            {
                printLogD("SyncNote","num deleted notes:${resultObj}")
                return DataState.data(
                    response=null,
                    data=null,
                    stateEvent = null
                )
            }

        }

    }




}