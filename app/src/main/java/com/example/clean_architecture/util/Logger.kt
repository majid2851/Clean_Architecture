package com.majid2851.clean_architecture.util

import android.util.Log
import com.majid2851.clean_architecture.util.Constants.DEBUG
import com.majid2851.clean_architecture.util.Constants.TAG

var isUnitTest = false

fun printLogD(className: String?, message: String ) {
    if (DEBUG && !isUnitTest) {
        Log.d(TAG, "$className: $message")
    }
    else if(DEBUG && isUnitTest){
        println("$className: $message")
    }
}

