package com.majid2851.clean_architecture.framework.datasource.network.implementation

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.framework.datasource.network.abstraction.NoteFirestoreService
import com.majid2851.clean_architecture.framework.datasource.network.mappers.NetworkMapper
import com.majid2851.clean_architecture.framework.datasource.network.model.NoteNetworkEntity
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteFireStoreServieImpl @Inject
    constructor(
        private val firebaseAuth:FirebaseAuth,
        private val fireStore:FirebaseFirestore,
        private val networkMapper:NetworkMapper
    ):NoteFirestoreService
{
    override suspend fun insertOrUpdateNote(note: Note) {
        val entity=networkMapper.mapToEntity(note)
        entity.updated_at= Timestamp.now()
        fireStore.collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(entity.id)
            .set(entity)
            .await()
    }

    override suspend fun insertOrUpdateNotes(notes: List<Note>) {
       if (notes.size>500) {
            throw Exception("Cannote insert more than 500 notes at a time")
       }
       val collectionRef=fireStore
           .collection(NOTES_COLLECTION)
           .document(USER_ID)
           .collection(NOTES_COLLECTION)
       fireStore.runBatch {batch->
            for (note in notes){
                val entity=networkMapper.mapToEntity(note)
                entity.updated_at= Timestamp.now()
                val documentRef=collectionRef.document(note.id)
                batch.set(documentRef,entity)
            }
       }

    }

    override suspend fun deleteNote(primaryKey: String) {
        fireStore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document()
            .delete()
            .await()
    }

    override suspend fun insertDeletedNote(note: Note) {
        val entity=networkMapper.mapToEntity(note)
        fireStore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .set(entity)
            .await()

    }

    override suspend fun insertDeletedNotes(notes: List<Note>) {
        if (notes.size>500) {
            throw Exception("Cannote insert more than 500 notes at a time")
        }
        val collectionRef=fireStore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
        fireStore.runBatch {batch->
            for (note in notes){
                val entity=networkMapper.mapToEntity(note)
                val documentRef=collectionRef.document(note.id)
                batch.set(documentRef,entity)
            }
        }
    }

    override suspend fun deleteDeletedNote(note: Note) {
        val entity=networkMapper.mapToEntity(note)
        fireStore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .delete()
            .await()
    }

    override suspend fun deleteAllNotes() {
        fireStore
            .collection(DELETES_COLLECTION)
            .document(USER_ID)
            .delete()
        fireStore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .delete()
    }

    override suspend fun getDeletedNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            fireStore
                .collection(DELETES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    override suspend fun searchNote(note: Note): Note? {
        return fireStore
            .collection(NOTES_COLLECTION)
            .document(USER_ID)
            .collection(NOTES_COLLECTION)
            .document(note.id)
            .get()
            .await()
            .toObject(NoteNetworkEntity::class.java)?.let {
                networkMapper.mapFromEntity(it)
            }
    }

    override suspend fun getAllNotes(): List<Note> {
        return networkMapper.entityListToNoteList(
            fireStore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .get()
                .await().toObjects(NoteNetworkEntity::class.java)
        )
    }

    companion object {
        const val NOTES_COLLECTION = "notes"
        const val USERS_COLLECTION = "users"
        const val DELETES_COLLECTION = "deletes"
        const val USER_ID = "hRZy091OVBWOdNeojrHxlHRzWwB3" // hardcoded for single user
        const val EMAIL = "majidbagheri2851@gmail.com"
    }

}