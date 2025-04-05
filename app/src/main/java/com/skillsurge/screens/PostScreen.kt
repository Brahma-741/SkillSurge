package com.skillsurge.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skillsurge.models.*
import com.skillsurge.services.PostService
import com.skillsurge.ui.theme.SkillSurgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    posts: List<Post>,
    currentUserId: String,
    currentUsername: String,
    onBackClick: () -> Unit,
    onNavigateToComments: (String) -> Unit // Add this parameter
) {
    val postService = PostService()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    SkillSurgeTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Community Posts") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick)  {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    when {
                        posts.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center) {
                                Text("No posts available",
                                    style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(posts) { post ->
                                    PostItem(
                                        post = post,
                                        currentUserId = currentUserId,
                                        onLikePost = {
                                            isLoading = true
                                            postService.likePost(
                                                Like(postId = post.postId, userId = currentUserId)
                                            )
                                        },
                                        onCommentPost = {
                                            // Navigate to comments screen
                                            onNavigateToComments(post.postId)
                                        },
                                        onFollowUser = {
                                            isLoading = true
                                            postService.followUser(
                                                Follower(
                                                    followerId = currentUserId,
                                                    followingId = post.userId
                                                )
                                            )
                                        },
                                        onSaveToFirebase = {
                                            isLoading = true
                                            postService.savePostToFirebase(
                                                post,
                                                onSuccess = {
                                                    isLoading = false
                                                },
                                                onError = {
                                                    errorMessage = it
                                                    isLoading = false
                                                }

                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    currentUserId: String,
    onLikePost: () -> Unit,
    onCommentPost: () -> Unit, // Pass the post as a parameter
    onSaveToFirebase: () -> Unit,
    onFollowUser: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User info and follow button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.username ?: "Anonymous",
                    style = MaterialTheme.typography.titleMedium
                )

                if (post.userId != currentUserId) {
                    IconButton(
                        onClick = onFollowUser,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Follow user",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post content
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )

            if (post.content.length > 150 && !isExpanded) {
                Text(
                    text = "...read more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { isExpanded = true }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.timestamp?.toDate().toString().take(10),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Show comment count if available
                Text(
                    text = "${post.commentCount ?: 0} comments",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Like button
                IconButton(onClick = onLikePost) {
                    Icon(
                        imageVector = if (post.likes?.contains(currentUserId) == true) {
                            Icons.Filled.ThumbUp
                        } else {
                            Icons.Outlined.ThumbUp
                        },
                        contentDescription = "Like post",
                        tint = if (post.likes?.contains(currentUserId) == true) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Comment button
                IconButton(onClick = onCommentPost) {
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = "Comment on post",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Share button
                IconButton(onClick = { /* Handle share */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share post",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onSaveToFirebase) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Save post",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }


            }
        }
    }
}
