package com.example.ui.explorer

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CodeFile
import com.example.data.model.Project
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.VsCodeBg
import com.example.ui.theme.VsCodeBlue
import com.example.ui.theme.VsCodeBorder
import com.example.ui.theme.VsCodeRed
import com.example.ui.theme.VsCodeSurface
import com.example.ui.theme.VsCodeSurfaceVariant
import com.example.ui.theme.VsCodeTextPrimary
import com.example.ui.theme.VsCodeTextSecondary

@Composable
fun ExplorerScreen(
    currentProject: Project?,
    allProjects: List<Project>,
    files: List<CodeFile>,
    activeFileId: String?,
    onSelectFile: (CodeFile) -> Unit,
    onCreateFile: (String, String) -> Unit, // name, content
    onDeleteFile: (CodeFile) -> Unit,
    onSelectProject: (Project) -> Unit,
    onCreateProject: (String, String, String) -> Unit // name, description, template
) {
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var fileToDelete by remember { mutableStateOf<CodeFile?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VsCodeBg)
    ) {
        // Explorer Header & Project Bar
        ExplorerHeader(
            currentProject = currentProject,
            onNewFileClick = { showNewFileDialog = true },
            onNewProjectClick = { showNewProjectDialog = true }
        )

        // Project files list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = SyntaxType,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = (currentProject?.name ?: "WORKSPACE").uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VsCodeTextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (files.isEmpty()) {
                item {
                    EmptyFilesPlaceholder(
                        onCreateFile = { showNewFileDialog = true }
                    )
                }
            } else {
                items(files, key = { it.id }) { file ->
                    val isActive = file.id == activeFileId
                    FileRowItem(
                        file = file,
                        isActive = isActive,
                        onSelect = { onSelectFile(file) },
                        onDelete = { fileToDelete = file }
                    )
                }
            }

            // Quick templates section if needed
            if (allProjects.size > 1) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "OTHER WORKSPACES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VsCodeTextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(allProjects.filter { it.id != currentProject?.id }, key = { it.id }) { proj ->
                    ProjectRowItem(
                        project = proj,
                        onSelect = { onSelectProject(proj) }
                    )
                }
            }
        }
    }

    // New File Dialog
    if (showNewFileDialog) {
        NewFileDialog(
            onDismiss = { showNewFileDialog = false },
            onCreate = { fileName, initialContent ->
                onCreateFile(fileName, initialContent)
                showNewFileDialog = false
            }
        )
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { name, desc, template ->
                onCreateProject(name, desc, template)
                showNewProjectDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (fileToDelete != null) {
        AlertDialog(
            onDismissRequest = { fileToDelete = null },
            title = { Text("Delete File", color = Color.White) },
            text = {
                Text(
                    "Are you sure you want to delete '${fileToDelete?.name}'? This cannot be undone.",
                    color = VsCodeTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        fileToDelete?.let { onDeleteFile(it) }
                        fileToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VsCodeRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { fileToDelete = null }) {
                    Text("Cancel", color = VsCodeTextSecondary)
                }
            },
            containerColor = VsCodeSurface
        )
    }
}

@Composable
private fun ExplorerHeader(
    currentProject: Project?,
    onNewFileClick: () -> Unit,
    onNewProjectClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VsCodeSurface)
            .border(width = 1.dp, color = VsCodeBorder)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "EXPLORER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VsCodeTextSecondary,
                letterSpacing = 1.2.sp
            )
            Text(
                text = currentProject?.name ?: "Active Project",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onNewFileClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue),
                modifier = Modifier
                    .height(34.dp)
                    .testTag("explorer_new_file_button"),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("New File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(
                onClick = onNewProjectClick,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("explorer_new_project_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = "New Project",
                    tint = VsCodeTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FileRowItem(
    file: CodeFile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val fileExtension = file.name.substringAfterLast('.', "")
    val badgeColor = when (fileExtension.lowercase()) {
        "py" -> SyntaxFunction
        "js", "ts" -> SyntaxNumber
        "kt" -> SyntaxKeyword
        "json" -> SyntaxString
        "html", "css" -> SyntaxType
        else -> VsCodeTextSecondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isActive) VsCodeSurfaceVariant else Color.Transparent)
            .border(
                1.dp,
                if (isActive) VsCodeBlue.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = fileExtension.ifEmpty { "TXT" }.uppercase(),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = file.name,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (isActive) Color.White else VsCodeTextPrimary,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${file.content.lines().size} lines • ${file.language}",
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = VsCodeTextSecondary.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ProjectRowItem(
    project: Project,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(VsCodeSurface.copy(alpha = 0.4f))
            .border(1.dp, VsCodeBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = SyntaxKeyword,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = project.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            if (project.description.isNotBlank()) {
                Text(
                    text = project.description,
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary
                )
            }
        }
    }
}

@Composable
private fun EmptyFilesPlaceholder(
    onCreateFile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VsCodeSurface.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = VsCodeTextSecondary,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "No files in this project yet",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Button(
                onClick = onCreateFile,
                colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Create First File")
            }
        }
    }
}

@Composable
private fun NewFileDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf("python") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New File", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name (e.g. main.py, app.js)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_file_name_input")
                )

                Text(
                    text = "Quick Starter Templates:",
                    fontSize = 12.sp,
                    color = VsCodeTextSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Python" to "main.py", "JavaScript" to "index.js", "Kotlin" to "Main.kt").forEach { (title, defaultName) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VsCodeSurfaceVariant)
                                .border(1.dp, VsCodeBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    fileName = defaultName
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(title, fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        val initialContent = when {
                            fileName.endsWith(".py") -> "# STF Code Python Workspace\n\ndef main():\n    print(\"Hello from STF Code!\")\n\nif __name__ == \"__main__\":\n    main()\n"
                            fileName.endsWith(".js") -> "// STF Code JavaScript Workspace\nconsole.log(\"Hello from STF Code!\");\n"
                            fileName.endsWith(".kt") -> "// STF Code Kotlin Workspace\nfun main() {\n    println(\"Hello from STF Code!\")\n}\n"
                            else -> "# STF Code File\n"
                        }
                        onCreate(fileName.trim(), initialContent)
                    }
                },
                enabled = fileName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue)
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VsCodeTextSecondary)
            }
        },
        containerColor = VsCodeSurface
    )
}

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var template by remember { mutableStateOf("python") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Workspace Project", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onCreate(projectName.trim(), description.trim(), template)
                    }
                },
                enabled = projectName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue)
            ) {
                Text("Create Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VsCodeTextSecondary)
            }
        },
        containerColor = VsCodeSurface
    )
}
