package com.example.clean_architecture.business.data.cache

import DataState
import MessageType
import Response
import UIComponentType
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_DATA_NULL
import com.majid2851.clean_architecture.business.data.cache.CacheResult
import com.majid2851.clean_architecture.business.domain.state.StateEvent

abstract class CacheResponseHandler<ViewState,Data>(
    private val response: CacheResult<Data?>,
    private val stateEvent: StateEvent?
)
{
    suspend fun getResult():DataState<ViewState>?
    {
        return when(response)
        {
            is CacheResult.GenericError ->
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
            is CacheResult.SUCCESS ->
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