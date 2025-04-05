package com.skillsurge.models

data class Recommendation(
    val courseId: String,
    val courseTitle: String,
    val courseDescription: String,
    val confidenceScore: Float // Confidence score for the recommendation
)