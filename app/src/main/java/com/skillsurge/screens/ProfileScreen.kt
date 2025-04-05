package com.skillsurge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onPrivacyPolicyClick: () -> Unit,
    onAboutUsClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPrivacyPolicyClick) {
            Text("Privacy Policy")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAboutUsClick) {
            Text("About Us")
        }
    }
}