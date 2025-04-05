package com.skillsurge.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillsurge.models.Recommendation
import com.skillsurge.ui.theme.SkillSurgeTheme
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onCreateCourseClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChatClick: () -> Unit,
    onSkillerClick: () -> Unit,
    onPostClick: () -> Unit,
    recommendations: List<Recommendation>
) {
    SkillSurgeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var welcomeAnimationState by remember { mutableStateOf(false) }
            var loggedInAnimationState by remember { mutableStateOf(false) }
            var recommendationsVisible by remember { mutableStateOf(false) }
            var buttonsVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                welcomeAnimationState = true
                delay(500)
                loggedInAnimationState = true
                delay(750)
                recommendationsVisible = true
                delay(1000)
                buttonsVisible = true
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Welcome Message
                AnimatedVisibility(
                    visible = welcomeAnimationState,
                    enter = fadeIn(animationSpec = tween(durationMillis = 500, easing = EaseOutBack)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Welcome to SkillSurge!",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Animated Logged-in Message
                AnimatedVisibility(
                    visible = loggedInAnimationState,
                    enter = fadeIn(animationSpec = tween(durationMillis = 700, easing = EaseOutBack)),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Ready to level up your skills?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Animated Recommendations Section
                AnimatedVisibility(
                    visible = recommendationsVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 800, easing = EaseOut)),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Top Picks For You",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (recommendations.isEmpty()) {
                            Text(
                                text = "No recommendations available yet. Explore more!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(recommendations) { recommendation ->
                                    AnimatedRecommendationItem(recommendation = recommendation)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Animated Action Buttons
                AnimatedVisibility(
                    visible = buttonsVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 900, easing = EaseOutBack)),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedActionButton(
                            text = "Create Your Course",
                            icon = Icons.Default.Create,
                            onClick = onCreateCourseClick,
                            animationDelay = 0
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedActionButton(
                            text = "Edit Your Profile",
                            icon = Icons.Default.AccountCircle,
                            onClick = onEditProfileClick,
                            animationDelay = 100
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedActionButton(
                            text = "Connect with Others",
                            icon = Icons.Default.Email,
                            onClick = onChatClick,
                            animationDelay = 200
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedActionButton(
                            text = "Chat with Skiller AI",
                            icon = Icons.Default.Face,
                            onClick = onSkillerClick,
                            animationDelay = 300
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AnimatedActionButton(
                            text = "Share Your Thoughts",
                            icon = Icons.Default.Create,
                            onClick = onPostClick,
                            animationDelay = 400
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedRecommendationItem(recommendation: Recommendation) {
    val infiniteTransition = rememberInfiniteTransition(label = "recommendationScale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recommendationScale"
    )

    Card(
        modifier = Modifier
            .width(180.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable {
                // Handle recommendation click
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = recommendation.courseTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.courseDescription,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /*TODO*/ }, shape = RoundedCornerShape(4.dp)) {
                Text("Learn More")
            }
        }
    }
}

@Composable
fun AnimatedActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    animationDelay: Int
) {
    var buttonState by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200, easing = EaseOut),
        label = "buttonScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (buttonState) 0.8f else 1f,
        animationSpec = tween(durationMillis = 200, easing = EaseOut),
        label = "buttonAlpha"
    )

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        buttonState = true
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val recommendations = listOf(
        Recommendation("1", "AI Fundamentals", "Explore the basics of Artificial Intelligence and its applications.", 0.9f),
        Recommendation("2", "Kotlin for Beginners", "Start your journey into Android development with Kotlin.", 0.8f),
        Recommendation("3", "UI/UX Design Principles", "Learn the core principles of creating user-friendly interfaces.", 0.75f),
        Recommendation("4", "Data Science Essentials", "Discover the fundamental concepts of data science and analysis.", 0.85f)
    )

    HomeScreen(
        onCreateCourseClick = { /* Navigate to Course Creation Screen */ },
        onEditProfileClick = { /* Navigate to Profile Editing Screen */ },
        onChatClick = { /* Navigate to Chat Screen */ },
        onSkillerClick = { /* Navigate to Skiller AI Screen */ },
        onPostClick = { /* Navigate to Post Creation Screen */ },
        recommendations = recommendations
    )
}