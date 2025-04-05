package com.skillsurge.screens

import androidx.compose.runtime.*
import com.skillsurge.ai.Skiller
import com.skillsurge.components.SkillerChat

@Composable
fun SkillerChatScreen(skiller: Skiller, onBackClick: () -> Unit) {
    SkillerChat(skiller = skiller, onBackClick = onBackClick)
}
