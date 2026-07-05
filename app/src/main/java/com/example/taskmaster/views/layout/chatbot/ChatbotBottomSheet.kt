package com.example.taskmaster.views.layout.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmaster.viewmodel.data.chatbot.ChatSender
import com.example.taskmaster.viewmodel.data.chatbot.ChatUiMessage
import com.example.taskmaster.viewmodel.model.ChatbotViewModel
import com.example.taskmaster.viewmodel.model.ProjectsViewModel

private val ChatBackground = Color(0xFFF9FAFB)
private val ChatSurface = Color(0xFFFFFFFF)
private val ChatField = Color(0xFFF4F5F7)
private val ChatBotBubble = Color(0xFFF3F4F6)
private val ChatBorder = Color(0xFFE5E7EB)
private val ChatTextPrimary = Color(0xFF111827)
private val ChatTextSecondary = Color(0xFF6B7280)
private val ChatBrand = Color(0xFFEC1926)
private val ChatBrandSoft = Color(0xFFFFE8EE)
private val ChatUserBubble = Color(0xFFEC0056)

private data class QuickAction(
    val label: String,
    val prompt: String
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ChatbotBottomSheet(
    visible: Boolean,
    projectId: Long?,
    viewModel: ChatbotViewModel,
    onDismiss: () -> Unit
) {
    if (!visible) return

    val locale = LocalConfiguration.current.locales[0]
    val isSpanish = locale?.language?.startsWith("es") == true
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val lastFailedMessage by viewModel.lastFailedMessage.collectAsState()
    val configuration = LocalConfiguration.current
    val maxHeight = configuration.screenHeightDp.dp * 0.72f
    val projectVm = remember { ProjectsViewModel() }
    val currentProject by projectVm.current.collectAsState()
    val projectContextLabel = if (projectId != null && projectId > 0L) {
        currentProject?.name?.takeIf { it.isNotBlank() }
    } else {
        null
    }
    val quickActions = remember(isSpanish) {
        listOf(
            QuickAction(
                label = if (isSpanish) "Resumen del proyecto" else "Project Summary",
                prompt = if (isSpanish) "Dame un resumen del proyecto actual." else "Give me a summary of the current project."
            ),
            QuickAction(
                label = if (isSpanish) "Mis tareas" else "My Tasks",
                prompt = if (isSpanish) "Muéstrame mis tareas pendientes." else "Show me my pending tasks."
            ),
            QuickAction(
                label = if (isSpanish) "Riesgos" else "Risks",
                prompt = if (isSpanish) "¿Qué riesgos detectas ahora?" else "What risks do you detect right now?"
            ),
            QuickAction(
                label = if (isSpanish) "Fechas límite" else "Deadlines",
                prompt = if (isSpanish) "Muéstrame las próximas fechas límite." else "Show me the upcoming deadlines."
            ),
            QuickAction(
                label = if (isSpanish) "Carga del equipo" else "Team Workload",
                prompt = if (isSpanish) "Analiza la carga actual del equipo." else "Analyze the current team workload."
            )
        )
    }
    var input by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(visible) {
        if (visible) viewModel.clearError()
    }

    LaunchedEffect(projectId) {
        if (projectId != null && projectId > 0L) {
            projectVm.loadById(projectId)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = {}
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .heightIn(max = maxHeight),
            shape = RoundedCornerShape(28.dp),
            color = ChatSurface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChatSurface)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ChatBrandSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = ChatBrand,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ares",
                            color = ChatTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "AI assistant",
                            color = ChatTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = if (isSpanish) "Cerrar" else "Close",
                            tint = ChatTextSecondary
                        )
                    }
                }

                if (!projectContextLabel.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isSpanish) {
                            "Contexto del proyecto: $projectContextLabel"
                        } else {
                            "Project context: $projectContextLabel"
                        },
                        color = ChatTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(min = 180.dp, max = maxHeight - 144.dp),
                    contentPadding = PaddingValues(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            ChatMessageBubble(
                                message = ChatUiMessage(
                                    text = if (isSpanish) {
                                        "¡Hola! ¿En qué puedo ayudarte?"
                                    } else {
                                        "Hi! How can I help?"
                                    },
                                    sender = ChatSender.BOT
                                ),
                                isSpanish = isSpanish
                            )
                        }
                        item {
                            QuickActionsRow(
                                actions = quickActions,
                                onClick = { action ->
                                    if (!isLoading) viewModel.sendMessage(action.prompt)
                                }
                            )
                        }
                    }

                    items(messages) { message ->
                        ChatMessageBubble(message = message, isSpanish = isSpanish)
                    }

                    if (isLoading) {
                        item {
                            ChatTypingBubble(
                                text = if (isSpanish) "Ares está escribiendo..." else "Ares is typing..."
                            )
                        }
                    }
                }

                if (!error.isNullOrBlank() && lastFailedMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ChatBorder, RoundedCornerShape(16.dp))
                            .background(ChatBrandSoft, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isSpanish) {
                                "No se pudo obtener respuesta. Intenta nuevamente."
                            } else {
                                "Could not get a response. Try again."
                            },
                            fontSize = 12.sp,
                            color = ChatBrand,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isSpanish) "Reintentar" else "Retry",
                            color = ChatBrand,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { viewModel.retryLastMessage() }
                                .padding(start = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        minLines = 1,
                        maxLines = 3,
                        shape = RoundedCornerShape(18.dp),
                        placeholder = {
                            Text(
                                text = if (isSpanish) "Pregunta lo que quieras..." else "Ask anything...",
                                fontSize = 13.sp,
                                color = ChatTextSecondary
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            color = ChatTextPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ChatField,
                            unfocusedContainerColor = ChatField,
                            focusedBorderColor = ChatBrand.copy(alpha = 0.28f),
                            unfocusedBorderColor = ChatBorder
                        )
                    )
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = ChatBrand,
                        shadowElevation = 6.dp
                    ) {
                        IconButton(
                            onClick = {
                                if (input.isNotBlank() && !isLoading) {
                                    viewModel.sendMessage(input)
                                    input = ""
                                }
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = if (isSpanish) "Enviar" else "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    actions: List<QuickAction>,
    onClick: (QuickAction) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 4.dp)
    ) {
        items(actions) { action ->
            Surface(
                modifier = Modifier.clickable { onClick(action) },
                shape = RoundedCornerShape(16.dp),
                color = ChatSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, ChatBorder),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(ChatBrandSoft)
                    )
                    Text(
                        text = action.label,
                        color = ChatTextPrimary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatUiMessage,
    isSpanish: Boolean
) {
    val isUser = message.sender == ChatSender.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 8.dp,
                bottomEnd = if (isUser) 8.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isUser -> ChatUserBubble
                    message.isError -> ChatBrandSoft
                    else -> ChatBotBubble
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isUser) 0.dp else 1.dp)
        ) {
            Text(
                text = when {
                    message.isError && isSpanish -> "No se pudo obtener respuesta. Intenta nuevamente."
                    message.isError -> "Could not get a response. Try again."
                    else -> message.text
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = when {
                    isUser -> Color.White
                    message.isError -> ChatBrand
                    else -> ChatTextPrimary
                }
            )
        }
    }
}

@Composable
private fun ChatTypingBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ChatBotBubble,
            border = androidx.compose.foundation.BorderStroke(1.dp, ChatBorder)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                color = ChatTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
