package com.example.feature.chat.presentation.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.components.AppBottomBar
import com.example.designsystem.components.AppSnackBar
import com.example.designsystem.components.SnackBarType
import com.example.designsystem.components.showAppSnackbar
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.chat.R
import com.example.feature.chat.domain.model.ChatMessage
import com.example.feature.chat.domain.model.Sender
import com.example.feature.chat.presentation.state.ChatUiState
import com.example.feature.chat.presentation.ui.components.ConnectionIndicator
import com.example.feature.chat.presentation.viewmodel.ChatNavigationEvent
import com.example.feature.chat.presentation.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    onNavigateToPlanGeneration: (String) -> Unit,
    navigateToHome: () -> Unit,
    navigateToFavorite: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is ChatNavigationEvent.NavigateToPlanGeneration -> {
                    onNavigateToPlanGeneration(event.threadId)
                }
            }
        }
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.snackbarEvent) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showAppSnackbar(
                message = message,
                type = SnackBarType.ERROR
            )
        }
    }

    ChatScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onInputTextChanged = viewModel::onInputTextChanged,
        onSendMessage = viewModel::onSendMessage,
        onBottomBarItemSelected = { index ->
            when (index) {
                0 -> navigateToHome()
                1 -> navigateToFavorite()
                2 -> navigateToCommunity()
                4 -> navigateToProfile()
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    state: ChatUiState,
    snackbarHostState: SnackbarHostState,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBottomBarItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    if (state is ChatUiState.Success) {
        LaunchedEffect(state.messages.size) {
            if (state.messages.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ChatTopBar() },
        bottomBar = {
            AppBottomBar(
                selectedItem = 3,
                onItemSelected = onBottomBarItemSelected
            )
        },
        snackbarHost = { AppSnackBar(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is ChatUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ChatUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        ConnectionIndicator(state = state.connectionState)

                        if (state.messages.isEmpty() && !state.isAiThinking) {
                            EmptyStateContent(modifier = Modifier.weight(1f))
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.md),
                                reverseLayout = true
                            ) {
                                if (state.isAiThinking) {
                                    item {
                                        ThinkingIndicatorItem()
                                    }
                                }
                                items(state.messages.reversed(), key = { it.id }) { message ->
                                    MessageItem(message = message)
                                }
                            }
                        }

                        ChatInputArea(
                            text = state.inputText,
                            onTextChanged = onInputTextChanged,
                            onSend = onSendMessage,
                            isSending = state.isSending,
                            isConnected = state.connectionState == com.example.feature.chat.domain.model.ConnectionState.CONNECTED
                        )
                    }
                }
                is ChatUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatTopBar() {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.md)
                .padding(horizontal = MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.xxxl - MaterialTheme.spacing.xs)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.example.designsystem.R.drawable.ic_airplane),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
            }
            Text(
                text = stringResource(R.string.travel_assistant),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EmptyStateContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = com.example.designsystem.R.drawable.ai_chat_fill),
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.spacing.xxxl * 2 + MaterialTheme.spacing.lg),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        Text(
            text = stringResource(R.string.travel_ai_assistant),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
        Text(
            text = stringResource(R.string.travel_ai_assistant_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val isUser = message.sender == Sender.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    }
    val textColor = MaterialTheme.colorScheme.onSurface

    val visible = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        visible.value = true
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = visible.value,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = MaterialTheme.spacing.xs),
            contentAlignment = alignment
        ) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs,
                    topEnd = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs,
                    bottomStart = if (isUser) MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs else MaterialTheme.spacing.xxs,
                    bottomEnd = if (isUser) MaterialTheme.spacing.xxs else MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs
                )
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm
                    ),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ChatInputArea(
    text: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean,
    isConnected: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.md),
        color = Color.Transparent
    ) {
        TextField(
            value = text,
            onValueChange = onTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xs)
                .border(MaterialTheme.elevation.xs, MaterialTheme.colorScheme.outline, CircleShape)
                .clip(CircleShape),
            placeholder = {
                Text(
                    stringResource(R.string.ask_chatgpt),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            enabled = !isSending && isConnected,
            trailingIcon = {
                IconButton(
                    onClick = onSend,
                    enabled = text.isNotBlank() && !isSending && isConnected
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(MaterialTheme.spacing.lg),
                            strokeWidth = MaterialTheme.elevation.sm
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.send_icon),
                            contentDescription = stringResource(R.string.send_message),
                            tint = if (text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    TravioTheme {
        ChatScreenContent(
            state = ChatUiState.Success(
                messages = listOf(
                    ChatMessage(threadId = "1", sender = Sender.USER, content = "Hi"),
                    ChatMessage(
                        threadId = "1",
                        sender = Sender.AI,
                        content = "Hello! How can I help you today?"
                    )
                )
            ),
            snackbarHostState = SnackbarHostState(),
            onInputTextChanged = {},
            onSendMessage = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenEmptyPreview() {
    TravioTheme {
        ChatScreenContent(
            state = ChatUiState.Success(
                messages = emptyList()
            ),
            snackbarHostState = SnackbarHostState(),
            onInputTextChanged = {},
            onSendMessage = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenLoadingPreview() {
    TravioTheme {
        ChatScreenContent(
            state = ChatUiState.Loading,
            snackbarHostState = SnackbarHostState(),
            onInputTextChanged = {},
            onSendMessage = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenErrorPreview() {
    TravioTheme {
        ChatScreenContent(
            state = ChatUiState.Error("Failed to load messages"),
            snackbarHostState = SnackbarHostState(),
            onInputTextChanged = {},
            onSendMessage = {},
            onBottomBarItemSelected = {}
        )
    }
}

@Composable
fun ThinkingIndicatorItem() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.xs),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            shape = RoundedCornerShape(
                topStart = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs,
                topEnd = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs,
                bottomStart = MaterialTheme.spacing.xxs,
                bottomEnd = MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs
            ),
            modifier = Modifier.alpha(alpha)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.spacing.sm - MaterialTheme.spacing.xxs / 2)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Text(
                    text = stringResource(id = R.string.travel_assistant_thinking),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

// Removed Dimensions object as its values were replaced by MaterialTheme tokens
