package com.skillsurge.models


data class Post(
    val postId: String,
    val userId: String,
    val username: String?,
    val content: String,
    val timestamp: com.google.firebase.Timestamp?,
    val likes: List<String>?, // List of user IDs who liked the post
    val commentCount: Int? = 0 // Number of comments
)