package com.skillsurge.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ListenerRegistration
import com.skillsurge.models.Message
import com.skillsurge.ui.theme.SkillSurgeTheme
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    senderId: String,
    receiverId: String,
    onBackClick: () -> Unit,
    onSendMessage: suspend (Message) -> Unit,
    onListenMessages: (String, String, (Message) -> Unit) -> ListenerRegistration
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope()

    // Listen for new messages
    DisposableEffect(senderId, receiverId) {
        val listenerRegistration = onListenMessages(senderId, receiverId) { message ->
            messages.add(message)
        }
        onDispose {
            listenerRegistration.remove()
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
                    .padding(16.dp)
            ) {
                // Chat Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Chat with User",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Button(onClick = onBackClick) {
                        Text("Back")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chat Messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(messages) { message ->
                        ChatMessageItem(message, senderId)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Message Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val message = Message(
                                    senderId = senderId,
                                    receiverId = receiverId,
                                    text = messageText
                                )
                                onSendMessage(message) // Using the provided function
                                messageText = ""
                            }
                        }
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: Message, senderId: String) {
    val isSentByMe = message.senderId == senderId

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isSentByMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}