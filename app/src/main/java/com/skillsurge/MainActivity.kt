package com.skillsurge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.skillsurge.screens.*
import com.skillsurge.ai.Skiller
import com.skillsurge.components.SkillerChat
import com.skillsurge.models.Post
import com.skillsurge.services.MessagingService
import com.skillsurge.ui.theme.SkillSurgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillSurgeApp()
        }
    }
}

@Composable
fun SkillSurgeApp() {
    SkillSurgeTheme {
        val navController = rememberNavController()
        val auth = FirebaseAuth.getInstance()
        val messagingService = remember { MessagingService() }
        val db = FirebaseFirestore.getInstance()

        val posts = remember { mutableStateListOf<Post>() }
        var showLogin by remember { mutableStateOf(auth.currentUser == null) }

        DisposableEffect(Unit) {
            val authListener = FirebaseAuth.AuthStateListener {
                showLogin = it.currentUser == null
            }
            auth.addAuthStateListener(authListener)
            onDispose {
                auth.removeAuthStateListener(authListener)
            }
        }

        LaunchedEffect(Unit) {
            db.collection("posts").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error fetching posts: ${error.message}")
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val fetchedPosts = it.documents.mapNotNull { doc ->
                        @Suppress("DEPRECATION")
                        doc.toObject<Post>()?.copy(postId = doc.id)
                    }
                    posts.clear()
                    posts.addAll(fetchedPosts)
                }
            }
        }

        Scaffold(
            bottomBar = { if (!showLogin) NavigationBar(navController) }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = if (showLogin) "login" else "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                showLogin = false
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onSignUpClick = { navController.navigate("signup") }
                        )
                    }
                    composable("signup") {
                        SignupScreen(
                            onSignUpSuccess = {
                                showLogin = false
                                navController.navigate("home") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            },
                            onLoginClick = { navController.navigate("login") }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onCreateCourseClick = { navController.navigate("courseCreation") },
                            onEditProfileClick = { navController.navigate("profileEdit") },
                            onChatClick = { navController.navigate("chat") },
                            onSkillerClick = { navController.navigate("skillerChat") },
                            onPostClick = { navController.navigate("posts") },
                            recommendations = listOf()
                        )
                    }
                    composable("posts") {
                        PostScreen(
                            posts = posts,
                            currentUserId = auth.currentUser?.uid ?: "",
                            currentUsername = auth.currentUser?.displayName ?: "User",
                            onBackClick = { navController.popBackStack() },
                            onNavigateToComments = { postId ->
                                navController.navigate("comments/$postId")
                            }
                        )
                    }
                    composable(
                        route = "comments/{postId}",
                        arguments = listOf(navArgument("postId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId") ?: ""
                        CommentsScreen(
                            postId = postId,
                            currentUserId = auth.currentUser?.uid ?: "",
                            currentUsername = auth.currentUser?.displayName ?: "User",
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("chat") {
                        ChatScreen(
                            senderId = auth.currentUser?.uid ?: "",
                            receiverId = "user2",
                            onSendMessage = messagingService::sendMessage,
                            onListenMessages = messagingService::listenForMessages,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onLogout = {
                                auth.signOut()
                                showLogin = true
                                navController.navigate("login") {
                                    popUpTo("profile") { inclusive = true }
                                }
                            },
                            onPrivacyPolicyClick = { navController.navigate("privacyPolicy") },
                            onAboutUsClick = { navController.navigate("aboutUs") }
                        )
                    }
                    composable("privacyPolicy") {
                        PrivacyPolicyScreen(onBackClick = { navController.popBackStack() })
                    }
                    composable("aboutUs") {
                        AboutUsScreen(onBackClick = { navController.popBackStack() })
                    }
                    composable("skillerChat") {
                        SkillerChat(
                            skiller = Skiller(),
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("courseCreation") {
                        CourseCreationScreen(
                            onCourseCreated = { navController.popBackStack() }
                        )
                    }
                    composable("profileEdit") {
                        ProfileEditScreen(
                            onProfileUpdated = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Chat") },
            selected = currentRoute == "chat",
            onClick = { navController.navigate("chat") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") { launchSingleTop = true } }
        )
    }
}