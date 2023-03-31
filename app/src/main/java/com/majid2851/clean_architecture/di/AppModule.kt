package com.majid2851.clean_architecture.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.data.cache.implementation.NoteCacheDataSourceImpl
import com.majid2851.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.data.network.implementation.NoteNetworkDataSourceImpl
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.business.interactors.common.DeleteNote
import com.majid2851.clean_architecture.business.interactors.notedetail.NoteDetailInteractors
import com.majid2851.clean_architecture.business.interactors.notedetail.UpdateNote
import com.majid2851.clean_architecture.business.interactors.notelist.DeleteMultipleNotes
import com.majid2851.clean_architecture.business.interactors.notelist.GetNumNotes
import com.majid2851.clean_architecture.business.interactors.notelist.InsertNewNote
import com.majid2851.clean_architecture.business.interactors.notelist.NoteListInteractors
import com.majid2851.clean_architecture.business.interactors.notelist.RestoreDeletedNote
import com.majid2851.clean_architecture.business.interactors.notelist.SearchNote
import com.majid2851.clean_architecture.business.interactors.splash.SyncDeletedNotes
import com.majid2851.clean_architecture.business.interactors.splash.SyncNotes
import com.majid2851.clean_architecture.framework.datasource.cache.abstraction.NoteDaoService
import com.majid2851.clean_architecture.framework.datasource.cache.database.NoteDao
import com.majid2851.clean_architecture.framework.datasource.cache.implementation.NoteDaoServiceImpl
import com.majid2851.clean_architecture.framework.datasource.cache.mapper.CacheMapper
import com.majid2851.clean_architecture.framework.datasource.database.NoteDatabase
import com.majid2851.clean_architecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.majid2851.clean_architecture.framework.datasource.network.implementation.NoteFireStoreServieImpl
import com.majid2851.clean_architecture.framework.datasource.network.mappers.NetworkMapper
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Module
object AppModule {


    // https://developer.android.com/reference/java/text/SimpleDateFormat.html?hl=pt-br
    @JvmStatic
    @Singleton
    @Provides
    fun provideDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC-7") // match firestore
        return sdf
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDateUtil(dateFormat: SimpleDateFormat): DateUtil {
        return DateUtil(
            dateFormat
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSharedPrefsEditor(
        sharedPreferences: SharedPreferences
    ): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteFactory(dateUtil: DateUtil): NoteFactory {
        return NoteFactory(
            dateUtil
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDAO(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.noteDao()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteCacheMapper(dateUtil: DateUtil): CacheMapper {
        return CacheMapper(dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteNetworkMapper(dateUtil: DateUtil): NetworkMapper {
        return NetworkMapper(dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDaoService(
        noteDao: NoteDao,
        noteEntityMapper: CacheMapper,
        dateUtil: DateUtil
    ): NoteDaoService {
        return NoteDaoServiceImpl(noteDao, noteEntityMapper, dateUtil)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteCacheDataSource(
        noteDaoService: NoteDaoService
    ): NoteCacheDataSource {
        return NoteCacheDataSourceImpl(noteDaoService)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirestoreService(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        networkMapper: NetworkMapper
    ): NoteFirestoreService {
        return NoteFireStoreServieImpl(
            firebaseAuth,
            firebaseFirestore,
            networkMapper
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteNetworkDataSource(
        firestoreService: NoteFireStoreServieImpl
    ): NoteNetworkDataSource {
        return NoteNetworkDataSourceImpl(
            firestoreService
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncNotes(
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource
    ): SyncNotes {
        return SyncNotes(
            noteCacheDataSource,
            noteNetworkDataSource
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideSyncDeletedNotes(
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource
    ): SyncDeletedNotes {
        return SyncDeletedNotes(
            noteCacheDataSource,
            noteNetworkDataSource
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDetailInteractors(
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource
    ): NoteDetailInteractors{
        return NoteDetailInteractors(
            DeleteNote(noteCacheDataSource, noteNetworkDataSource),
            UpdateNote(noteCacheDataSource, noteNetworkDataSource)
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteListInteractors(
        noteCacheDataSource: NoteCacheDataSource,
        noteNetworkDataSource: NoteNetworkDataSource,
        noteFactory: NoteFactory
    ): NoteListInteractors {
        return NoteListInteractors(
            InsertNewNote(noteCacheDataSource, noteNetworkDataSource, noteFactory),
            DeleteNote(noteCacheDataSource, noteNetworkDataSource),
            SearchNote(noteCacheDataSource),
            GetNumNotes(noteCacheDataSource),
            RestoreDeletedNote(noteCacheDataSource, noteNetworkDataSource),
            DeleteMultipleNotes(noteCacheDataSource, noteNetworkDataSource)
        )
    }



}
