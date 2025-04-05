package com.skillsurge.services

import com.google.firebase.firestore.FirebaseFirestore
import com.skillsurge.models.Follower
import com.skillsurge.models.Like
import com.skillsurge.models.Post

class PostService {
    private val db = FirebaseFirestore.getInstance()

    // Like a post
    fun likePost(like: Like) {
        val likeId = "${like.postId}_${like.userId}" // Unique ID for the like
        db.collection("likes")
            .document(likeId)
            .set(like)
            .addOnSuccessListener {
                println("Post liked successfully!")
            }
            .addOnFailureListener { e ->
                println("Failed to like post: ${e.message}")
            }
    }

    // Follow a user
    fun followUser(follower: Follower) {
        val followerId = "${follower.followerId}_${follower.followingId}" // Unique ID for the follower
        db.collection("followers")
            .document(followerId)
            .set(follower)
            .addOnSuccessListener {
                println("User followed successfully!")
            }
            .addOnFailureListener { e ->
                println("Failed to follow user: ${e.message}")
            }
    }
    fun savePostToFirebase(
        posts: Post,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .add(posts)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError("Failed to create course: ${e.localizedMessage ?: "Unknown error"}")
            }
    }

}
