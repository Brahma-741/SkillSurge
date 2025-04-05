package com.skillsurge.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.skillsurge.models.Message
import kotlinx.coroutines.tasks.await

class MessagingService {
    private val db = FirebaseFirestore.getInstance()

    // Send a message
    suspend fun sendMessage(message: Message) {
        db.collection("messages")
            .add(message)
            .await()
    }

    // Listen for new messages
    fun listenForMessages(
        senderId: String,
        receiverId: String,
        onMessageReceived: (Message) -> Unit
    ): ListenerRegistration {
        return db.collection("messages")
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                snapshot?.documents?.forEach { doc ->
                    val message = doc.toObject(Message::class.java)
                    if (message != null) {
                        onMessageReceived(message)
                    }
                }
            }
    }
}