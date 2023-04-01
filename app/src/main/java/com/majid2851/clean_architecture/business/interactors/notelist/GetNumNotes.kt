package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import UIComponentType
import com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNumNotes(
    private val noteCacheDataSource: NoteCacheDataSource
)
{
    fun getNumNotes(
        stateEvent:StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow()
    {
        val cacheResult = safeCacheCall(IO)
        {
            noteCacheDataSource.getNumNotes()
        }
        val response = object :  CacheResponseHandler<NoteListViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        )
        {
            override suspend fun handleSuccess(resultObj: Int): DataState<NoteListViewState>
            {
                 val viewState= NoteListViewState(
                     numNotesInCache = resultObj
                 )
                 return DataState.data(
                     response = Response(
                         message=GET_NUM_NOTES_SUCCESS,
                         uiComponentType = UIComponentType.None(),
                         messageType = MessageType.Success()
                     ),
                     data = viewState,
                     stateEvent=stateEvent
                 )
            }

        }.getResult()

        emit(response)

    }
    companion object{
        const val GET_NUM_NOTES_SUCCESS="Successfully retrieved number of notes in Cache"
        const val GET_NUM_NOTES_FAILED="Failded to get the number of notes from the Cache"
    }














}