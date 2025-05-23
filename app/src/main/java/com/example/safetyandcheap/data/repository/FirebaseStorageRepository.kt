package com.example.safetyandcheap.data.repository

import android.net.Uri
import com.example.safetyandcheap.service.dto.property.Image
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseStorageRepository @Inject constructor() {

    private val storage = Firebase.storage.reference

    object Paths {
        private const val IMAGES_DIR = "images"

        fun generateImagePath(index: Int): String {
            return "$IMAGES_DIR/${System.currentTimeMillis()}_$index.jpg"
        }
    }

    suspend fun uploadImages(uris: List<Uri>): List<Image> = coroutineScope {
        uris.mapIndexed { index, uri ->
            async {
                val filePath = Paths.generateImagePath(index)
                val imageRef = storage.child(filePath)

                imageRef.putFile(uri).await()
                Image(
                    0L,
                    filePath
                )
            }
        }.awaitAll()
    }

    suspend fun deleteImage(path: String) {
        storage.child(path).delete().await()
    }

    suspend fun downloadImages(paths: List<String>): List<String> = coroutineScope {
        paths.map { path ->
            async {
                storage.child(path).downloadUrl.await().toString()
            }
        }.awaitAll()
    }

    suspend fun downloadImage(path: String): String = coroutineScope {
        storage.child(path).downloadUrl.await().toString()
    }

}
