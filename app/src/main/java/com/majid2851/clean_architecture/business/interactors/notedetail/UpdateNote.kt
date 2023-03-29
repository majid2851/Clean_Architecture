package com.majid2851.clean_architecture.business.interactors.notedetail

import DataState
import MessageType
import Response
import UIComponentType
import com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.framework.presentation.notedetail.state.NoteDetailViewState
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateNote(
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource,
    private val noteNetworkDataSource:NoteNetworkDataSource
)
{
    fun updateNote(
        note:Note,
        stateEvent:StateEvent
    ): Flow<DataState<NoteDetailViewState>?> = flow()
    {
        val cacheResult= safeCacheCall()
        {
            noteCacheDataSource.updateNote(
                primary = note.id,
                newTitle = note.title,
                newBody = note.body
            )
        }
        val response=object : com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler<NoteDetailViewState, Int>(
            response=cacheResult,
            stateEvent=stateEvent
        )
        {
            override suspend fun handleSuccess(resultObj: Int): DataState<NoteDetailViewState>
            {
                return if (resultObj>0)
                {
                    DataState.data(
                        response = Response(
                            message = UPDATE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent=stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = UPDATE_NOTE_FAILED,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Error()
                        ),
                        data = null,
                        stateEvent=stateEvent
                    )
                }


            }
        }.getResult()
        emit(response)
        updateNetwork(response.stateMessage?.response?.message,note)

    }
    private suspend fun updateNetwork(response:String?,note:Note)
    {
        if (response.equals(UPDATE_NOTE_SUCCESS)){
            safeApiCall {
                noteNetworkDataSource.insertOrUpdateNote(note)
            }
        }
    }

    companion object{
        val UPDATE_NOTE_SUCCESS = "Successfully updated note."
        val UPDATE_NOTE_FAILED = "Failed to update note."
        val UPDATE_NOTE_FAILED_PK = "Update failed. Note is missing primary key."

    }










}