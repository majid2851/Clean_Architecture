package com.majid2851.clean_architecture.framework.datasource.network


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.di.TestAppComponent
import com.majid2851.clean_architecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.majid2851.clean_architecture.framework.datasource.network.implementation.NoteFireStoreServieImpl
import com.majid2851.clean_architecture.framework.datasource.network.mappers.NetworkMapper
import com.majid2851.clean_architecture.framework.presentation.TestBaseApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTests{

    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService

    // dependencies
    val application: TestBaseApplication
            = ApplicationProvider.getApplicationContext<Context>() as TestBaseApplication

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var noteFactory: NoteFactory

    @Inject
    lateinit var networkMapper: NetworkMapper

    init {
        (application.appComponent as TestAppComponent)
            .inject(this)
        signIn()
    }

    @Before
    fun before(){
        noteFirestoreService = NoteFireStoreServieImpl(
            firebaseAuth = FirebaseAuth.getInstance(),
            fireStore = firestore,
            networkMapper = networkMapper
        )
    }

    private fun signIn() = runBlocking{
        firebaseAuth.signInWithEmailAndPassword(
            EMAIL,
            PASSWORD
        ).await()
    }

    @Test
    fun insertSingleNote_CBS() = runBlocking{
        val note = noteFactory.createSingleNote(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        )

        noteFirestoreService.insertOrUpdateNote(note)

        val searchResult = noteFirestoreService.searchNote(note)

        Assert.assertEquals(note, searchResult)
    }

    companion object{
        const val EMAIL = "majidbagheri2851@gmail.com"
        const val PASSWORD = "28512851"
    }
}




