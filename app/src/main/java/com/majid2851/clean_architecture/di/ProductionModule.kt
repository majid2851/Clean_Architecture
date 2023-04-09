package com.majid2851.clean_architecture.cleannotes.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.majid2851.clean_architecture.framework.datasource.database.NoteDatabase
import com.majid2851.clean_architecture.framework.datasource.prefrences.PrefrencesKeys
import com.majid2851.clean_architecture.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton


/*
    Dependencies in this class have test fakes for ui tests. See "TestModule.kt" in
    androidTest dir
 */
@ExperimentalCoroutinesApi
@FlowPreview
@Module
object ProductionModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: BaseApplication): NoteDatabase {
        return Room
            .databaseBuilder(app, NoteDatabase::class.java, NoteDatabase.DATABSE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefrences(
        application: BaseApplication
    ):SharedPreferences{
        return application.getSharedPreferences(
            PrefrencesKeys.NOTE_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }



}