package com.majid2851.clean_architecture.business.data.util

import android.util.Log
import android.widget.Toast
import com.majid2851.clean_architecture.business.data.cache.CacheConstants.CACHE_TIMEOUT
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_TIMEOUT
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.majid2851.clean_architecture.business.data.cache.CacheResult
import com.majid2851.clean_architecture.business.data.util.GenericErrors.ERROR_UNKNOWN
import com.majid2851.clean_architecture.business.data.network.ApiResult
import com.majid2851.clean_architecture.business.data.network.NetworkConstants.REQUEST_TIMEOUT
import com.majid2851.clean_architecture.business.data.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.majid2851.clean_architecture.business.data.network.NetworkErrors.NETWORK_ERROR_TIMEOUT
import com.majid2851.clean_architecture.framework.presentation.MainActivity
import com.majid2851.clean_architecture.util.cLog
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher=Dispatchers.IO,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher)
    {
        try {
            // throws TimeoutCancellationException
            withTimeout(REQUEST_TIMEOUT)
            {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            Log.i("insertNote-firebaseErr",throwable.message.toString())
            throwable.printStackTrace()
            cLog(throwable.message)
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    cLog(errorResponse)
                    ApiResult.GenericError(
                        code,
                        errorResponse
                    )
                }
                else -> {
                    cLog(NETWORK_ERROR_UNKNOWN)
                    ApiResult.GenericError(
                        null,
                        NETWORK_ERROR_UNKNOWN
                    )
                }
            }
        }
    }
}


suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher=Dispatchers.IO,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT){
                CacheResult.SUCCESS(cacheCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            cLog(throwable.message)
            when (throwable) {

                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    cLog(CACHE_ERROR_UNKNOWN)
                    CacheResult.GenericError(CACHE_ERROR_UNKNOWN)
                }
            }
        }
    }
}


private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}









