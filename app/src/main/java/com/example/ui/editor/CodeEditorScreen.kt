package com.example.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodeFile
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeEditorBg
import com.example.ui.theme.VsCodeGreen
import com.example.ui.theme.VsCodeStatusBar
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeSurfaceVariant
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary

@Composable
fun CodeEditorScreen(
    activeFile: CodeFile?,
    openFiles: List<CodeFile>,
    fontSize: Int = 13,
    showLineNumbers: Boolean = true,
    wordWrap: Boolean = false,
    accentColorHex: String = "#007ACC",
    onSelectFile: (CodeFile) -> Unit,
    onCloseFile: (CodeFile) -> Unit,
    onSaveFile: (CodeFile, String) -> Unit,
    onRunCode: (CodeFile, String) -> Unit,
    onAskReachAgent: (CodeFile, String) -> Unit,
    onOpenDesignCustomizer: (() -> Unit)? = null
) {
    if (activeFile == null) {
        EmptyEditorState()
        return
    }

    val accentColor = try {
        Color(android.graphics.Color.parseColor(accentColorHex))
    } catch (e: Exception) {
        VsCodeBlue
    }

    var editorTextValue by remember(activeFile.id) {
        mutableStateOf(TextFieldValue(activeFile.content, TextRange(activeFile.content.length)))
    }

    var isSaving by remember { mutableStateOf(false) }
    var saveStatusMessage by remember { mutableStateOf<String?>(null) }
    val isDirty = editorTextValue.text != activeFile.content

    // Find and Replace state
    var showFindBar by remember { mutableStateOf(false) }
    var findQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }

    val lines = remember(editorTextValue.text) {
        editorTextValue.text.lines()
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VsCodeEditorBg)
    ) {
        // Top Action Bar
        EditorTopBar(
            activeFile = activeFile,
            isDirty = isDirty,
            isSaving = isSaving,
            saveStatusMessage = saveStatusMessage,
            showFindBar = showFindBar,
            onToggleFind = { showFindBar = !showFindBar },
            onSave = {
                isSaving = true
                onSaveFile(activeFile, editorTextValue.text)
                isSaving = false
                saveStatusMessage = "Saved"
            },
            onRun = {
                onRunCode(activeFile, editorTextValue.text)
            },
            onAskReachAgent = {
                onAskReachAgent(activeFile, editorTextValue.text)
            },
            onOpenDesign = onOpenDesignCustomizer
        )

        // Find & Replace Floating Tray
        AnimatedVisibility(
            visible = showFindBar,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FindReplaceBar(
                findQuery = findQuery,
                onFindQueryChange = { findQuery = it },
                replaceQuery = replaceQuery,
                onReplaceQueryChange = { replaceQuery = it },
                matchCount = if (findQuery.isNotBlank()) {
                    editorTextValue.text.split(findQuery).size - 1
                } else 0,
                onReplaceOne = {
                    if (findQuery.isNotBlank()) {
                        val currentText = editorTextValue.text
                        val index = currentText.indexOf(findQuery)
                        if (index != -1) {
                            val newText = currentText.replaceFirst(findQuery, replaceQuery)
                            editorTextValue = TextFieldValue(newText, TextRange(index + replaceQuery.length))
                            saveStatusMessage = null
                        }
                    }
                },
                onReplaceAll = {
                    if (findQuery.isNotBlank()) {
                        val newText = editorTextValue.text.replace(findQuery, replaceQuery)
                        editorTextValue = TextFieldValue(newText, TextRange(newText.length))
                        saveStatusMessage = null
                    }
                },
                onClose = { showFindBar = false }
            )
        }

        // Open Files Tab Row
        if (openFiles.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = openFiles.indexOfFirst { it.id == activeFile.id }.coerceAtLeast(0),
                containerColor = VsCodeSurface,
                edgePadding = 0.dp,
                divider = {},
                indicator = { tabPositions ->
                    val index = openFiles.indexOfFirst { it.id == activeFile.id }.coerceAtLeast(0)
                    if (index < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = VsCodeBlue,
                            height = 2.dp
                        )
                    }
                }
            ) {
                openFiles.forEach { file ->
                    val isActive = file.id == activeFile.id
                    val fileIsDirty = if (isActive) isDirty else false

                    Tab(
                        selected = isActive,
                        onClick = { onSelectFile(file) },
                        modifier = Modifier
                            .background(if (isActive) VsCodeEditorBg else VsCodeSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = file.name,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isActive) Color.White else VsCodeTextSecondary,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                            )

                            if (fileIsDirty) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(VsCodeBlue, CircleShape)
                                )
                            }

                            if (openFiles.size > 1) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close tab",
                                    tint = VsCodeTextSecondary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onCloseFile(file) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Code Editor Canvas (Gutter Line Numbers + Syntax Highlighted Content)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(VsCodeEditorBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Line Number Gutter
                if (showLineNumbers) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(VsCodeSurface.copy(alpha = 0.5f))
                            .border(width = 1.dp, color = VsCodeBorder.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        val lh = (fontSize + 7).sp
                        for (i in 1..maxOf(lines.size, 1)) {
                            Text(
                                text = "$i",
                                fontSize = fontSize.sp,
                                fontFamily = FontFamily.Monospace,
                                color = VsCodeTextSecondary.copy(alpha = 0.6f),
                                lineHeight = lh
                            )
                        }
                    }
                }

                // Code Input Area
                val inputModifier = if (wordWrap) {
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                } else {
                    Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                }

                Box(
                    modifier = inputModifier
                ) {
                    // Syntax Highlighting visualizer layer
                    val annotatedCode = remember(editorTextValue.text, activeFile.language) {
                        SyntaxHighlighter.highlight(editorTextValue.text, activeFile.language)
                    }

                    // Underlying text field for real-time mobile editing
                    BasicTextField(
                        value = editorTextValue,
                        onValueChange = {
                            editorTextValue = it
                            saveStatusMessage = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("code_editor_text_field"),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize + 7).sp,
                            color = VsCodeTextPrimary
                        ),
                        cursorBrush = SolidColor(accentColor),
                        visualTransformation = {
                            androidx.compose.ui.text.input.TransformedText(
                                annotatedCode,
                                androidx.compose.ui.text.input.OffsetMapping.Identity
                            )
                        }
                    )
                }
            }
        }

        // Desktop-Class VS Code Bottom Status Bar
        EditorStatusBar(
            activeFile = activeFile,
            lineCount = lines.size,
            cursorPosition = editorTextValue.selection.start
        )

        // Mobile Programming Accessory Keyboard Bar
        AccessoryKeyBar(
            onInsertKey = { key ->
                val currentText = editorTextValue.text
                val selection = editorTextValue.selection
                val newText = buildString {
                    append(currentText.substring(0, selection.start))
                    append(key)
                    append(currentText.substring(selection.end))
                }
                val newCursor = selection.start + key.length
                editorTextValue = TextFieldValue(newText, TextRange(newCursor))
                saveStatusMessage = null
            }
        )
    }
}

@Composable
private fun EditorTopBar(
    activeFile: CodeFile,
    isDirty: Boolean,
    isSaving: Boolean,
    saveStatusMessage: String?,
    showFindBar: Boolean,
    onToggleFind: () -> Unit,
    onSave: () -> Unit,
    onRun: () -> Unit,
    onAskReachAgent: () -> Unit,
    onOpenDesign: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface)
            .border(width = 1.dp, color = VsCodeBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(VsCodeSurfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = activeFile.language.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SyntaxType,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = activeFile.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )

            if (saveStatusMessage != null) {
                Text(
                    text = "• $saveStatusMessage",
                    fontSize = 11.sp,
                    color = VsCodeGreen
                )
            } else if (isDirty) {
                Text(
                    text = "• Modified",
                    fontSize = 11.sp,
                    color = SyntaxComment
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Themes and Design customizer button
            if (onOpenDesign != null) {
                IconButton(
                    onClick = onOpenDesign,
                    modifier = Modifier.size(34.dp).testTag("editor_design_palette_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Themes & Design",
                        tint = VsCodeBlue,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Find in file button
            IconButton(
                onClick = onToggleFind,
                modifier = Modifier.size(34.dp).testTag("editor_find_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Find and replace",
                    tint = if (showFindBar) VsCodeBlue else VsCodeTextSecondary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Save Button
            IconButton(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("editor_save_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save file",
                        tint = if (isDirty) VsCodeBlue else VsCodeTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Run Button
            Button(
                onClick = onRun,
                modifier = Modifier
                    .height(32.dp)
                    .testTag("editor_run_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VsCodeGreen.copy(alpha = 0.2f),
                    contentColor = VsCodeGreen
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Run", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Reach Agent Button
            Button(
                onClick = onAskReachAgent,
                modifier = Modifier
                    .height(32.dp)
                    .testTag("editor_reach_agent_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VsCodeBlue,
                    contentColor = Color.White
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reach", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FindReplaceBar(
    findQuery: String,
    onFindQueryChange: (String) -> Unit,
    replaceQuery: String,
    onReplaceQueryChange: (String) -> Unit,
    matchCount: Int,
    onReplaceOne: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = VsCodeSurfaceVariant),
        border = androidx.compose.material3.CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(VsCodeBorder)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = findQuery,
                    onValueChange = onFindQueryChange,
                    placeholder = { Text("Find...", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(44.dp)
                )

                Text(
                    text = if (findQuery.isNotBlank()) "$matchCount matches" else "",
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary,
                    fontFamily = FontFamily.Monospace
                )

                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = VsCodeTextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = replaceQuery,
                    onValueChange = onReplaceQueryChange,
                    placeholder = { Text("Replace with...", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(44.dp)
                )

                Button(
                    onClick = onReplaceOne,
                    colors = ButtonDefaults.buttonColors(containerColor = VsCodeSurface),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Text("Replace", fontSize = 10.sp)
                }

                Button(
                    onClick = onReplaceAll,
                    colors = ButtonDefaults.buttonColors(containerColor = VsCodeSurface),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Text("All", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun EditorStatusBar(
    activeFile: CodeFile,
    lineCount: Int,
    cursorPosition: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeStatusBar)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "🌿 main",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "0 ⓧ  0 ⚠",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "● Reach Agent",
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ln $lineCount, Pos $cursorPosition",
                fontSize = 10.sp,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Spaces: 4",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "UTF-8",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = activeFile.language.replaceFirstChar { it.uppercase() },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun AccessoryKeyBar(
    onInsertKey: (String) -> Unit
) {
    val programmingKeys = remember {
        listOf(
            "Tab" to "    ",
            "{" to "{",
            "}" to "}",
            "(" to "(",
            ")" to ")",
            "[" to "[",
            "]" to "]",
            ";" to ";",
            ":" to ":",
            "=" to "=",
            "+" to "+",
            "-" to "-",
            "*" to "*",
            "/" to "/",
            "<" to "<",
            ">" to ">",
            "\"" to "\"",
            "'" to "'",
            "->" to "->",
            "=>" to "=>",
            "//" to "//",
            "#" to "#",
            "$" to "$",
            "_" to "_",
            "&" to "&",
            "|" to "|",
            "!" to "!",
            "?" to "?"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface)
            .border(width = 1.dp, color = VsCodeBorder)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        programmingKeys.forEach { (label, value) ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(VsCodeSurfaceVariant)
                    .border(1.dp, VsCodeBorder, RoundedCornerShape(6.dp))
                    .clickable { onInsertKey(value) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun EmptyEditorState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VsCodeBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = VsCodeTextSecondary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "No File Open",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Open a file from the Explorer tab or create a new one to begin coding.",
                fontSize = 13.sp,
                color = VsCodeTextSecondary
            )
        }
    }
}
