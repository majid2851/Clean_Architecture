package com.example.clean_architecture.business.data.cache

import DataState
import MessageType
import Response
import StateEvent
import UIComponentType
import com.example.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_DATA_NULL

abstract class CacheResponseHandler<ViewState,Data>(
    private val response:CacheResults<Data?>,
    private val stateEvent:StateEvent?
)
{
    suspend fun getResult():DataState<ViewState>?
    {
        return when(response)
        {
            is CacheResults.GenericError ->
            {
                DataState.error(
                    response=Response(
                        message = "${stateEvent?.errorInfo()}\n\n"+
                        "Reason:${response.errorMessage}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),stateEvent=stateEvent
                )
            }
            is CacheResults.SUCCESS ->
            {
                if (response.value==null)
                {
                    DataState.error(
                        response=Response(
                            message = "${stateEvent?.errorInfo()}\n\n"+
                                    "Reason:${CACHE_ERROR_DATA_NULL}",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),stateEvent=stateEvent
                    )
                }else{
                    handleSuccess(resultObj=response.value)
                }
            }

        }
    }

    abstract fun handleSuccess(resultObj: Data): DataState<ViewState>

}