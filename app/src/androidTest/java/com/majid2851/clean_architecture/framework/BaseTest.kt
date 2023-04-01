package com.majid2851.clean_architecture.framework

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.majid2851.clean_architecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.majid2851.clean_architecture.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@FlowPreview
abstract class BaseTest
{
    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService

    // dependencies
    val application: TestBaseApplication
            =  ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication
    abstract fun injectTest()







}