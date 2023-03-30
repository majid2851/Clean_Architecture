package com.majid2851.clean_architecture.framework.presentation

import android.app.Application
import com.majid2851.clean_architecture.di.AppComponent
import com.majid2851.clean_architecture.di.DaggerAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
open class BaseApplication : Application()
{
    lateinit var appComponent: AppComponent


    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    open fun initAppComponent()
    {
        appComponent=DaggerAppComponent.factory()
            .create(this)
    }







}