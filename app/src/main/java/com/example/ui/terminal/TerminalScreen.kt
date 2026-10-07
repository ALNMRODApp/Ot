package com.example.ui.terminal

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodeFile
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeGreen
import com.example.ui.theme.VsCodeRed
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeSurfaceVariant
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary
import com.example.ui.theme.VsCodeYellow
import kotlinx.coroutines.launch

data class TerminalLog(
    val text: String,
    val type: TerminalLogType = TerminalLogType.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TerminalLogType {
    COMMAND, INFO, SUCCESS, ERROR, WARNING
}

@Composable
fun TerminalScreen(
    activeFile: CodeFile?,
    terminalLogs: List<TerminalLog>,
    onExecuteCommand: (String) -> Unit,
    onClearTerminal: () -> Unit
) {
    var commandInput by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: bash, 1: Output, 2: Debug Console
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            listState.animateScrollToItem(terminalLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141414))
    ) {
        // Terminal Top Toolbar with Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VsCodeSurface)
                .border(1.dp, VsCodeBorder)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("1: bash", "Output", "Debug").forEachIndexed { index, tabTitle ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) VsCodeSurfaceVariant else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tabTitle,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else VsCodeTextSecondary
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onClearTerminal,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ClearAll,
                        contentDescription = "Clear",
                        tint = VsCodeTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Quick Command Action Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VsCodeSurface.copy(alpha = 0.5f))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val quickCommands = listOf(
                "run" to "▶ Run active file",
                "ls" to "ls",
                "git status" to "git status",
                "reach agent --help" to "reach agent",
                "python --version" to "python -v",
                "clear" to "clear"
            )

            quickCommands.forEach { (cmd, label) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(VsCodeSurfaceVariant)
                        .border(1.dp, VsCodeBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            if (cmd == "clear") {
                                onClearTerminal()
                            } else {
                                onExecuteCommand(cmd)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SyntaxType
                    )
                }
            }
        }

        // Terminal Output Logs
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "STF Code Mobile Shell v1.0 [Connected: Reach Agent Engine]",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SyntaxComment
                )
                Text(
                    text = "Type 'help' for available commands or 'run' to execute active script.",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VsCodeTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(terminalLogs) { log ->
                val textColor = when (log.type) {
                    TerminalLogType.COMMAND -> SyntaxKeyword
                    TerminalLogType.INFO -> VsCodeTextPrimary
                    TerminalLogType.SUCCESS -> VsCodeGreen
                    TerminalLogType.ERROR -> VsCodeRed
                    TerminalLogType.WARNING -> VsCodeYellow
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                ) {
                    if (log.type == TerminalLogType.COMMAND) {
                        Text(
                            text = "stf@mobile:~$ ",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SyntaxFunction
                        )
                    }
                    Text(
                        text = log.text,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = textColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Interactive Command Input Prompt
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VsCodeSurface)
                .border(1.dp, VsCodeBorder)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = VsCodeGreen
            )

            BasicTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_command_input"),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color.White
                ),
                cursorBrush = SolidColor(VsCodeGreen),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        val cmd = commandInput.trim()
                        commandInput = ""
                        if (cmd.equals("clear", ignoreCase = true)) {
                            onClearTerminal()
                        } else {
                            onExecuteCommand(cmd)
                        }
                    }
                },
                modifier = Modifier
                    .size(34.dp)
                    .testTag("terminal_send_command_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Command",
                    tint = VsCodeBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
