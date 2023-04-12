package com.majid2851.clean_architecture.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.majid2851.clean_architecture.util.Constants.DEBUG
import com.majid2851.clean_architecture.util.Constants.MAG2851
import com.majid2851.clean_architecture.util.Constants.TEST2851

var isUnitTest = false

fun printLogD(className: String?, message: String ) {
    if (DEBUG && !isUnitTest) {
        Log.d(MAG2851, "$className: $message")
    }
    else if(DEBUG && isUnitTest){
        Log.i(TEST2851,"$className: $message")
    }
}

fun cLog(msg: String?){
    msg?.let {
        if(!DEBUG){
            FirebaseCrashlytics.getInstance().log(it)
        }
    }
}


