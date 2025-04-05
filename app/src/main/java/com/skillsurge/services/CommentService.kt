package com.skillsurge.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.skillsurge.models.Comment
import kotlinx.coroutines.tasks.await

class CommentService {
    private val db: FirebaseFirestore = Firebase.firestore

    suspend fun addComment(comment: Comment, onSuccess: () -> Unit, onError: (String) -> Unit) {
        try {
            db.collection("comments")
                .add(comment)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onError(e.message ?: "Failed to add comment") }
                .await()
        } catch (e: Exception) {
            onError(e.message ?: "Failed to add comment")
        }
    }

    suspend fun getComments(postId: String): List<Comment> {
        return try {
            db.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .await()
                .toObjects(Comment::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}