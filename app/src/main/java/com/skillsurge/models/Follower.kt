package com.skillsurge.models

data class Follower(
    val followerId: String = "", // ID of the follower
    val followingId: String = "" // ID of the user being followed
)