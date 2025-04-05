package com.skillsurge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skillsurge.models.UserProfile
import com.skillsurge.ui.theme.SkillSurgeTheme
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileEditScreen(onProfileUpdated: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: run {
        errorMessage = "You must be logged in to edit your profile"
        ""
    }

    // Load existing profile data
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            isLoading = true
            try {
                val document = FirebaseFirestore.getInstance()
                    .collection("profiles")
                    .document(userId)
                    .get()
                    .await()

                document.toObject(UserProfile::class.java)?.let { profile ->
                    name = profile.name
                    bio = profile.bio
                    skills = profile.skills.joinToString(", ")
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    SkillSurgeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Edit Profile",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading && name.isEmpty()) {
                    CircularProgressIndicator()
                } else {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name*") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = name.isBlank() && errorMessage != null,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio*") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = bio.isBlank() && errorMessage != null,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills (comma-separated)*") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = skills.isBlank() && errorMessage != null,
                        placeholder = { Text("e.g., Kotlin, Compose, Firebase") }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (currentUser == null) {
                                errorMessage = "You must be logged in to edit your profile"
                                return@Button
                            }

                            when {
                                name.isBlank() -> errorMessage = "Name is required"
                                bio.isBlank() -> errorMessage = "Bio is required"
                                skills.isBlank() -> errorMessage = "Skills are required"
                                else -> {
                                    isLoading = true
                                    errorMessage = null

                                    val userProfile = UserProfile(
                                        userId = userId,
                                        name = name,
                                        bio = bio,
                                        skills = skills.split(",")
                                            .map { it.trim() }
                                            .filter { it.isNotEmpty() },
                                        updatedAt = Timestamp.now()
                                    )

                                    saveProfileToFirestore(
                                        userProfile = userProfile,
                                        onSuccess = onProfileUpdated,
                                        onError = {
                                            errorMessage = it
                                            isLoading = false
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save Profile")
                        }
                    }
                }
            }
        }
    }
}

private fun saveProfileToFirestore(
    userProfile: UserProfile,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("profiles")
        .document(userProfile.userId)
        .set(userProfile)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e ->
            onError("Failed to update profile: ${e.localizedMessage ?: "Unknown error"}")
        }
}