package com.example.ui.agent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentMessage
import com.example.data.model.CodeFile
import com.example.data.model.CustomAgent
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeEditorBg
import com.example.ui.theme.VsCodeGreen
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeSurfaceVariant
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary

@Composable
fun ReachAgentScreen(
    activeFile: CodeFile?,
    messages: List<AgentMessage>,
    isAgentThinking: Boolean,
    activeAgentName: String,
    activeAgentId: String,
    customAgents: List<CustomAgent>,
    onSelectAgent: (String, String, String) -> Unit,
    onCreateCustomAgent: (String, String, String, String, String, String) -> Unit,
    onUpdateCustomAgent: (CustomAgent) -> Unit,
    onDeleteCustomAgent: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onApplyCodeToEditor: (String) -> Unit,
    onRunInTerminal: (String) -> Unit
) {
    var promptInput by remember { mutableStateOf("") }
    var showAgentHub by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    var copiedMessageId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(messages.size, isAgentThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VsCodeBg)
    ) {
        // Reach Agent Top Bar with Agent Switcher Button
        ReachAgentHeader(
            activeFile = activeFile,
            activeAgentName = activeAgentName,
            onOpenHub = { showAgentHub = true }
        )

        // Quick Suggestion Chips based on active agent role
        QuickActionChips(
            activeFile = activeFile,
            agentName = activeAgentName,
            onActionSelected = { actionPrompt ->
                onSendMessage(actionPrompt)
            }
        )

        // Conversation List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    AgentIntroCard(activeFile = activeFile, agentName = activeAgentName)
                }
            }

            items(messages, key = { it.id }) { message ->
                AgentMessageBubble(
                    message = message,
                    isCopied = copiedMessageId == message.id,
                    agentName = activeAgentName,
                    onCopy = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        copiedMessageId = message.id
                    },
                    onApplyToEditor = { code ->
                        onApplyCodeToEditor(code)
                    },
                    onRunInTerminal = { code ->
                        onRunInTerminal(code)
                    }
                )
            }

            if (isAgentThinking) {
                item {
                    ThinkingIndicator(agentName = activeAgentName)
                }
            }
        }

        // Input Prompt Row
        AgentInputBar(
            promptInput = promptInput,
            onPromptChange = { promptInput = it },
            isThinking = isAgentThinking,
            onSend = {
                if (promptInput.isNotBlank()) {
                    val prompt = promptInput.trim()
                    promptInput = ""
                    onSendMessage(prompt)
                }
            }
        )
    }

    if (showAgentHub) {
        AgentHubDialog(
            activeAgentId = activeAgentId,
            customAgents = customAgents,
            onSelectAgent = onSelectAgent,
            onCreateCustomAgent = onCreateCustomAgent,
            onUpdateCustomAgent = onUpdateCustomAgent,
            onDeleteCustomAgent = onDeleteCustomAgent,
            onDismiss = { showAgentHub = false }
        )
    }
}

@Composable
private fun ReachAgentHeader(
    activeFile: CodeFile?,
    activeAgentName: String,
    onOpenHub: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface)
            .border(1.dp, VsCodeBorder)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(VsCodeBlue.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, VsCodeBlue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = VsCodeBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = activeAgentName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(VsCodeGreen, CircleShape)
                    )
                }
                Text(
                    text = if (activeFile != null) "File: ${activeFile.name}" else "No active file",
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Switch / Add Agents Button
        Button(
            onClick = onOpenHub,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VsCodeSurfaceVariant,
                contentColor = Color.White
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(VsCodeBorder)
            ),
            modifier = Modifier
                .height(34.dp)
                .testTag("switch_agent_button"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
        ) {
            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp), tint = VsCodeBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Switch Agent", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuickActionChips(
    activeFile: CodeFile?,
    agentName: String,
    onActionSelected: (String) -> Unit
) {
    val quickActions = remember(activeFile, agentName) {
        val fileName = activeFile?.name ?: "code"
        when {
            agentName.contains("Security", ignoreCase = true) -> listOf(
                "Audit $fileName for vulnerabilities",
                "Sanitize inputs & SQL safety",
                "Check zero-trust permissions",
                "Inspect memory & auth leaks"
            )
            agentName.contains("Test", ignoreCase = true) -> listOf(
                "Generate unit tests for $fileName",
                "Mock edge cases & failures",
                "Write Robolectric test suite",
                "Assert boundary invariants"
            )
            agentName.contains("UI", ignoreCase = true) || agentName.contains("Designer", ignoreCase = true) -> listOf(
                "Refactor to stunning Compose UI",
                "Add smooth animations & ripples",
                "Enhance color palette & typography",
                "Make layout responsive"
            )
            agentName.contains("Performance", ignoreCase = true) -> listOf(
                "Optimize time & space complexity",
                "Eliminate redundant allocations",
                "Cache heavy computations",
                "Profile execution bottlenecks"
            )
            else -> listOf(
                "Explain $fileName",
                "Fix bugs & optimize",
                "Generate unit tests",
                "Refactor code cleanly",
                "Add docstrings & types"
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface.copy(alpha = 0.5f))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        quickActions.forEach { action ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(VsCodeSurfaceVariant)
                    .border(1.dp, VsCodeBorder, RoundedCornerShape(8.dp))
                    .clickable { onActionSelected(action) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = action,
                    fontSize = 11.sp,
                    color = SyntaxType,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AgentIntroCard(activeFile: CodeFile?, agentName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VsCodeSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = VsCodeBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$agentName Active",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = "Ask questions, generate solutions, or audit your codebase. You can also tap 'Switch Agent' above to switch specializations or create your own custom AI Agent.",
                fontSize = 12.sp,
                color = VsCodeTextSecondary,
                lineHeight = 17.sp
            )
            if (activeFile != null) {
                Text(
                    text = "Loaded file: ${activeFile.name} (${activeFile.language})",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SyntaxFunction
                )
            }
        }
    }
}

@Composable
private fun AgentMessageBubble(
    message: AgentMessage,
    isCopied: Boolean,
    agentName: String,
    onCopy: (String) -> Unit,
    onApplyToEditor: (String) -> Unit,
    onRunInTerminal: (String) -> Unit
) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(VsCodeBlue.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = VsCodeBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUser) VsCodeBlue.copy(alpha = 0.85f) else VsCodeSurface)
                .border(
                    1.dp,
                    if (isUser) VsCodeBlue else VsCodeBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = if (isUser) "You" else agentName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) Color.White.copy(alpha = 0.9f) else SyntaxType
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.content,
                fontSize = 13.sp,
                color = Color.White,
                lineHeight = 18.sp
            )

            val snippet = message.codeSnippet ?: extractCodeBlock(message.content)
            if (!snippet.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(VsCodeEditorBg)
                        .border(1.dp, VsCodeBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = snippet,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SyntaxFunction,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onApplyToEditor(snippet) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VsCodeBlue,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("apply_code_to_editor_button"),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.IntegrationInstructions, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Insert to Editor", fontSize = 10.sp)
                            }

                            Button(
                                onClick = { onCopy(snippet) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VsCodeSurfaceVariant,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.height(28.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCopied) "Copied!" else "Copy", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingIndicator(agentName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 36.dp, top = 4.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = VsCodeBlue
        )
        Text(
            text = "$agentName is analyzing...",
            fontSize = 12.sp,
            color = SyntaxComment,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun AgentInputBar(
    promptInput: String,
    onPromptChange: (String) -> Unit,
    isThinking: Boolean,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface)
            .border(1.dp, VsCodeBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = promptInput,
            onValueChange = onPromptChange,
            placeholder = { Text("Ask your agent to write, refactor, or test...", fontSize = 12.sp) },
            modifier = Modifier
                .weight(1f)
                .testTag("reach_agent_prompt_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VsCodeBlue,
                unfocusedBorderColor = VsCodeBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onSend,
            enabled = promptInput.isNotBlank() && !isThinking,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (promptInput.isNotBlank() && !isThinking) VsCodeBlue else VsCodeSurfaceVariant)
                .testTag("reach_agent_send_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (promptInput.isNotBlank() && !isThinking) Color.White else VsCodeTextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun extractCodeBlock(text: String): String? {
    val regex = "```(?:[a-zA-Z]+)?\\n([\\s\\S]*?)```".toRegex()
    val match = regex.find(text)
    return match?.groups?.get(1)?.value?.trim()
}
