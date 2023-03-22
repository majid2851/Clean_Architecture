package com.example.clean_architecture.business.data.cache

sealed class CacheResults <out T>
{
    data class SUCCESS<out T>(val value:T):CacheResults<T>()
    data class GenericError(
        val errorMessage:String ?=null
    ):CacheResults<Nothing>()



}