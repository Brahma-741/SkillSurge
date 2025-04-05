package com.skillsurge.models

import com.google.firebase.Timestamp

data class UserProfile(
    val userId: String = "", // ID of the user
    val name: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val profilePictureUrl: String = "", // URL of the profile picture
    val updatedAt: Timestamp
)