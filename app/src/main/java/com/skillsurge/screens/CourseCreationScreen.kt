package com.skillsurge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skillsurge.models.Course
import com.skillsurge.ui.theme.SkillSurgeTheme

@Composable
fun CourseCreationScreen(onCourseCreated: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: run {
        errorMessage = "You must be logged in to create a course"
        ""
    }

    SkillSurgeTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create a New Course",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = title.isBlank() && errorMessage != null,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Course Description*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = description.isBlank() && errorMessage != null,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            price = it
                        }
                    },
                    label = { Text("Course Price*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = price.isBlank() && errorMessage != null,
                    singleLine = true,
                    prefix = { Text(text = "$") }
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            errorMessage = "You must be logged in to create a course"
                            return@Button
                        }

                        when {
                            title.isBlank() -> errorMessage = "Course title is required"
                            description.isBlank() -> errorMessage = "Course description is required"
                            price.isBlank() -> errorMessage = "Course price is required"
                            price.toDoubleOrNull() == null -> errorMessage = "Invalid price format"
                            else -> {
                                isLoading = true
                                errorMessage = null

                                val course = Course(
                                    title = title,
                                    description = description,
                                    price = price.toDouble(),
                                    instructorId = userId,
                                )

                                saveCourseToFirestore(
                                    course = course,
                                    onSuccess = onCourseCreated,
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
                        Text("Create Course")
                    }
                }
            }
        }
    }
}

private fun saveCourseToFirestore(
    course: Course,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("courses")
        .add(course)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e ->
            onError("Failed to create course: ${e.localizedMessage ?: "Unknown error"}")
        }
}