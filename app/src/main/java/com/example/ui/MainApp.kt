package com.example.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.ui.agent.ReachAgentScreen
import com.example.ui.auth.AuthManager
import com.example.ui.editor.CodeEditorScreen
import com.example.ui.explorer.ExplorerScreen
import com.example.ui.settings.AccountSettingsDialog
import com.example.ui.terminal.TerminalScreen
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.VsCodeActivityBar
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary
import com.example.ui.viewmodel.WorkspaceViewModel

enum class StudioTab(val title: String, val icon: ImageVector, val tag: String) {
    EXPLORER("Explorer", Icons.Default.Folder, "nav_explorer"),
    EDITOR("Editor", Icons.Default.Code, "nav_editor"),
    AGENT("Reach", Icons.Default.AutoAwesome, "nav_agent"),
    TERMINAL("Terminal", Icons.Default.Terminal, "nav_terminal")
}

@Composable
fun MainApp(
    viewModel: WorkspaceViewModel,
    onSignOutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedTab by remember { mutableStateOf(StudioTab.EDITOR) }
    var showAccountSettings by remember { mutableStateOf(false) }

    val userProfile by viewModel.userProfile.collectAsState()
    val editorFontSize by viewModel.editorFontSize.collectAsState()
    val showLineNumbers by viewModel.showLineNumbers.collectAsState()
    val wordWrap by viewModel.wordWrap.collectAsState()
    val customAccentColorHex by viewModel.customAccentColorHex.collectAsState()

    val projects by viewModel.projects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val allFiles by viewModel.allFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val openFiles by viewModel.openFiles.collectAsState()
    val agentMessages by viewModel.agentMessages.collectAsState()
    val isAgentThinking by viewModel.isAgentThinking.collectAsState()
    val activeAgentName by viewModel.activeAgentName.collectAsState()
    val activeAgentId by viewModel.activeAgentId.collectAsState()
    val customAgents by viewModel.customAgents.collectAsState()

    // Handle back button on secondary tabs to return to Editor
    BackHandler(enabled = selectedTab != StudioTab.EDITOR) {
        selectedTab = StudioTab.EDITOR
    }

    Scaffold(
        topBar = {
            StudioTopBar(
                activeProjectName = activeProject?.name,
                activeFileName = activeFile?.name,
                onDesignClick = { showAccountSettings = true },
                onAccountClick = { showAccountSettings = true }
            )
        },
        bottomBar = {
            StudioBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                agentUnread = isAgentThinking
            )
        },
        containerColor = VsCodeBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                StudioTab.EXPLORER -> {
                    ExplorerScreen(
                        currentProject = activeProject,
                        allProjects = projects,
                        files = allFiles.filter { it.projectId == activeProject?.id },
                        activeFileId = activeFile?.id,
                        onSelectFile = { file ->
                            viewModel.selectFile(file)
                            selectedTab = StudioTab.EDITOR
                        },
                        onCreateFile = { name, content ->
                            viewModel.createFile(name, content)
                            selectedTab = StudioTab.EDITOR
                        },
                        onDeleteFile = { file ->
                            viewModel.deleteFile(file)
                        },
                        onSelectProject = { proj ->
                            viewModel.selectProject(proj)
                        },
                        onCreateProject = { name, desc, template ->
                            viewModel.createProject(name, desc, template)
                            selectedTab = StudioTab.EDITOR
                        }
                    )
                }

                StudioTab.EDITOR -> {
                    CodeEditorScreen(
                        activeFile = activeFile,
                        openFiles = openFiles,
                        fontSize = editorFontSize,
                        showLineNumbers = showLineNumbers,
                        wordWrap = wordWrap,
                        accentColorHex = customAccentColorHex,
                        onSelectFile = { file ->
                            viewModel.selectFile(file)
                        },
                        onCloseFile = { file ->
                            viewModel.closeFile(file)
                        },
                        onSaveFile = { file, content ->
                            viewModel.saveFileContent(file, content)
                        },
                        onRunCode = { file, content ->
                            viewModel.saveFileContent(file, content)
                            viewModel.executeTerminalCommand("run")
                            selectedTab = StudioTab.TERMINAL
                        },
                        onAskReachAgent = { file, content ->
                            viewModel.sendAgentPrompt("Explain and optimize ${file.name}")
                            selectedTab = StudioTab.AGENT
                        },
                        onOpenDesignCustomizer = {
                            showAccountSettings = true
                        }
                    )
                }

                StudioTab.AGENT -> {
                    ReachAgentScreen(
                        activeFile = activeFile,
                        messages = agentMessages,
                        isAgentThinking = isAgentThinking,
                        activeAgentName = activeAgentName,
                        activeAgentId = activeAgentId,
                        customAgents = customAgents,
                        onSelectAgent = { id, name, prompt ->
                            viewModel.setActiveAgent(id, name, prompt)
                        },
                        onCreateCustomAgent = { name, desc, prompt, icon, cat, colorHex ->
                            viewModel.createCustomAgent(name, desc, prompt, icon, cat, colorHex)
                        },
                        onUpdateCustomAgent = { agent ->
                            viewModel.updateCustomAgent(agent)
                        },
                        onDeleteCustomAgent = { id ->
                            viewModel.deleteCustomAgent(id)
                        },
                        onSendMessage = { prompt ->
                            viewModel.sendAgentPrompt(prompt)
                        },
                        onApplyCodeToEditor = { code ->
                            viewModel.applyAgentCodeToEditor(code)
                            selectedTab = StudioTab.EDITOR
                        },
                        onRunInTerminal = { code ->
                            viewModel.applyAgentCodeToEditor(code)
                            viewModel.executeTerminalCommand("run")
                            selectedTab = StudioTab.TERMINAL
                        }
                    )
                }

                StudioTab.TERMINAL -> {
                    TerminalScreen(
                        activeFile = activeFile,
                        terminalLogs = viewModel.terminalLogs,
                        onExecuteCommand = { cmd ->
                            viewModel.executeTerminalCommand(cmd)
                        },
                        onClearTerminal = {
                            viewModel.clearTerminal()
                        }
                    )
                }
            }
        }
    }

    if (showAccountSettings) {
        AccountSettingsDialog(
            userProfile = userProfile,
            currentTheme = userProfile?.theme ?: "dark_modern",
            onSelectTheme = { themeName ->
                viewModel.updateTheme(themeName)
            },
            fontSize = editorFontSize,
            onFontSizeChange = { viewModel.setEditorFontSize(it) },
            showLineNumbers = showLineNumbers,
            onToggleLineNumbers = { viewModel.toggleLineNumbers() },
            wordWrap = wordWrap,
            onToggleWordWrap = { viewModel.toggleWordWrap() },
            accentColorHex = customAccentColorHex,
            onSelectAccentColor = { viewModel.setCustomAccentColor(it) },
            onDismiss = { showAccountSettings = false },
            onSignOut = {
                showAccountSettings = false
                AuthManager.signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = onSignOutSuccess,
                    scope = coroutineScope
                )
            }
        )
    }
}

@Composable
private fun StudioTopBar(
    activeProjectName: String?,
    activeFileName: String?,
    onDesignClick: () -> Unit,
    onAccountClick: () -> Unit
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(VsCodeBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = "STF Code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${activeProjectName ?: "Project"} > ${activeFileName ?: "welcome"}",
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onDesignClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(VsCodeBlue.copy(alpha = 0.15f))
                    .testTag("topbar_design_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Themes & Design",
                    tint = VsCodeBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onAccountClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(VsCodeBlue.copy(alpha = 0.15f))
                    .testTag("account_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Account Settings",
                    tint = VsCodeBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun StudioBottomBar(
    selectedTab: StudioTab,
    onTabSelected: (StudioTab) -> Unit,
    agentUnread: Boolean
) {
    NavigationBar(
        containerColor = VsCodeActivityBar,
        tonalElevation = 0.dp,
        modifier = Modifier.border(1.dp, VsCodeBorder)
    ) {
        StudioTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == StudioTab.AGENT && agentUnread) {
                        BadgedBox(badge = { Badge { Text("●", fontSize = 8.sp) } }) {
                            Icon(imageVector = tab.icon, contentDescription = tab.title)
                        }
                    } else {
                        Icon(imageVector = tab.icon, contentDescription = tab.title)
                    }
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.testTag(tab.tag),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = VsCodeBlue,
                    unselectedIconColor = VsCodeTextSecondary,
                    unselectedTextColor = VsCodeTextSecondary
                )
            )
        }
    }
}
