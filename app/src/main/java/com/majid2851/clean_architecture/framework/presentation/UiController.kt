package com.majid2851.clean_architecture.framework.presentation

import DialogInputCaptureCallback
import Response
import StateMessageCallback

interface UIController
{

    fun displayProgressBar(isDisplayed: Boolean)

    fun hideSoftKeyboard()

    fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback)

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

}