package com.example.clean_architecture.business.interactors.common

import DataState
import MessageType
import Response
import UIComponentType
import com.example.clean_architecture.business.data.cache.CacheResponseHandler
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNote<ViewState>(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource:NoteNetworkDataSource
)
{
    fun deleteNote(
        note:Note,
        stateEvent:StateEvent
    ): Flow<DataState<ViewState>?> =flow()
    {
        val cacheResult= safeCacheCall(IO)
        {
            noteCacheDataSource.deleteNote(note.id)
        }
        val response=object :CacheResponseHandler<ViewState,Int>(
            response = cacheResult,
            stateEvent=stateEvent
        ){
            override fun handleSuccess(resultObj: Int): DataState<ViewState>
            {
                return if (resultObj>0)
                {
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data=null,
                        stateEvent=stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data=null,
                        stateEvent=stateEvent
                    )
                }
            }

        }.getResult()
        emit(response)
        updateNetwork(
            message = response?.stateMessage?.response?.message,
            note=note)

    }
    private suspend fun updateNetwork(message:String?,note: Note)
    {
        if (message.equals(DELETE_NOTE_SUCCESS)) {
            safeApiCall(IO)
            {
                noteNetworkDataSource.deleteNote(note.id)
            }
            safeApiCall {
                noteNetworkDataSource.insertDeletedNote(note)
            }
        }

    }

    companion object
    {
        const val DELETE_NOTE_SUCCESS="note is deleted successfully."
        const val DELETE_NOTE_FAILED="Failed to delete Note."
    }








}