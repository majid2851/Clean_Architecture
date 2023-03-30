package com.majid2851.clean_architecture.framework.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.clean_architecture.R
import com.google.firebase.auth.FirebaseAuth
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MainActivity : AppCompatActivity()
{

    private val TAG: String = "AppDebug"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        (application as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

}

























