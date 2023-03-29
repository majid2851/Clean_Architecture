package com.majid2851.clean_architecture.business.data.cache

sealed class CacheResult <out T>
{
    data class SUCCESS<out T>(val value:T):CacheResult<T>()
    data class GenericError(
        val errorMessage:String ?=null
    ):CacheResult<Nothing>()



}