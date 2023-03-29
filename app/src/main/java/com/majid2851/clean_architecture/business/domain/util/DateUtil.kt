package com.majid2851.clean_architecture.business.domain.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil @Inject constructor(
    private val dateFormat:SimpleDateFormat,
)
{
    //dateFormat=>"2023-03-20 HH:mm:SS"
    fun removeTimeFromDateString(sd:String): String {
        return sd.substring(0,sd.indexOf(" "))
    }

    fun convertFirebaseTimeStampToStringDate(timeStamp:Timestamp):String
    {
        return dateFormat.format(timeStamp.toDate())
    }

    fun convertStringDateToFirebaseTimeStamp(date:String):Timestamp
    {
        return Timestamp(dateFormat.parse(date))
    }
    fun getCurrentTimeStamp():String
    {
        return dateFormat.format(Date())
    }



}