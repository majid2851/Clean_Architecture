package com.majid2851.clean_architecture.framework.presentation

import DialogInputCaptureCallback
import Response
import StateMessageCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.clean_architecture.R
import com.google.firebase.auth.FirebaseAuth
import com.majid2851.clean_architecture.framework.presentation.common.NoteFragmentFactory
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : AppCompatActivity(),
    UIController
{

    private val TAG: String = "AppDebug"

    @Inject
    lateinit var fragmentFactory: NoteFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        setFragmentFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    private fun setFragmentFactory(){
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    private fun inject(){
        (application as BaseApplication).appComponent
            .inject(this)
    }

    override fun displayProgressBar(isDisplayed: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun hideSoftKeyboard() {
        // TODO("Not yet implemented")
    }

    override fun displayInputCaptureDialog(title: String, callback: DialogInputCaptureCallback) {
        // TODO("Not yet implemented")
    }

    override fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    ) {
        // TODO("Not yet implemented")
        printLogD("MainActivity", "response: ${response}")
    }

}


































