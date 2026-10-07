package com.example.ui.agent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CustomAgent
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
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

data class PrebuiltAgent(
    val id: String,
    val name: String,
    val description: String,
    val systemInstruction: String,
    val iconName: String,
    val icon: ImageVector,
    val accentColor: Color,
    val category: String
)

val PREBUILT_AGENTS = listOf(
    PrebuiltAgent(
        id = "agent_general",
        name = "Reach Generalist",
        description = "Full-stack mobile & backend engineering, algorithms, refactoring, and code review.",
        systemInstruction = "You are Reach Generalist, an expert software architect and coding assistant in STF Code. Provide clean, idiomatic code and actionable architectural advice.",
        iconName = "AutoAwesome",
        icon = Icons.Default.AutoAwesome,
        accentColor = VsCodeBlue,
        category = "General"
    ),
    PrebuiltAgent(
        id = "agent_security",
        name = "Security Auditor",
        description = "Penetration tester & auditor focusing on zero-trust security, input sanitization, and OWASP safety.",
        systemInstruction = "You are Security Auditor inside STF Code. Identify security vulnerabilities, data leaks, OWASP risks, and insecure permission configurations. Propose hardened zero-trust fixes.",
        iconName = "Security",
        icon = Icons.Default.Security,
        accentColor = VsCodeRed,
        category = "Security"
    ),
    PrebuiltAgent(
        id = "agent_testing",
        name = "Test Architect",
        description = "Unit tests, Robolectric test suites, mock generation, and edge case test coverage.",
        systemInstruction = "You are Test Architect in STF Code. Write exhaustive, deterministic unit and integration tests with edge cases, failure tests, and boundary assertions.",
        iconName = "Science",
        icon = Icons.Default.Science,
        accentColor = VsCodeGreen,
        category = "Testing"
    ),
    PrebuiltAgent(
        id = "agent_frontend",
        name = "UI / UX Designer",
        description = "Jetpack Compose, HTML/CSS, Tailwind, beautiful responsive layouts and micro-interactions.",
        systemInstruction = "You are UI/UX Designer in STF Code. Craft stunning, accessible, modern UI layouts with Material Design 3, fluid animations, and crisp styling.",
        iconName = "Palette",
        icon = Icons.Default.Palette,
        accentColor = SyntaxKeyword,
        category = "Design"
    ),
    PrebuiltAgent(
        id = "agent_performance",
        name = "Performance Optimizer",
        description = "Algorithm efficiency, memory profiling, O(n) algorithmic speedups, and cache patterns.",
        systemInstruction = "You are Performance Optimizer in STF Code. Spot bottlenecks, unnecessary allocations, slow loops, and optimize time/space complexity.",
        iconName = "Speed",
        icon = Icons.Default.Speed,
        accentColor = SyntaxFunction,
        category = "Optimization"
    )
)

val AGENT_ICONS = listOf(
    "AutoAwesome" to Icons.Default.AutoAwesome,
    "Code" to Icons.Default.Code,
    "Security" to Icons.Default.Security,
    "Science" to Icons.Default.Science,
    "Speed" to Icons.Default.Speed,
    "Palette" to Icons.Default.Palette,
    "Terminal" to Icons.Default.Terminal,
    "BugReport" to Icons.Default.BugReport,
    "SmartToy" to Icons.Default.SmartToy,
    "Psychology" to Icons.Default.Psychology,
    "Build" to Icons.Default.Build,
    "RocketLaunch" to Icons.Default.RocketLaunch,
    "Storage" to Icons.Default.Storage
)

val AGENT_COLORS = listOf(
    "#007ACC" to "VS Blue",
    "#00D8D6" to "Electric Cyan",
    "#BD93F9" to "Dracula Purple",
    "#FF7EDB" to "Synthwave Pink",
    "#50FA7B" to "Matrix Green",
    "#E6DB74" to "Monokai Gold",
    "#FF9E64" to "Tokyo Orange",
    "#E06C75" to "Coral Red",
    "#4EC9B0" to "Emerald Teal",
    "#DCDCAA" to "Sand Yellow"
)

data class PromptPreset(
    val title: String,
    val category: String,
    val iconName: String,
    val colorHex: String,
    val prompt: String,
    val description: String
)

val PROMPT_PRESETS = listOf(
    PromptPreset(
        title = "Full-Stack Refactorer",
        category = "Architecture",
        iconName = "Build",
        colorHex = "#007ACC",
        description = "Modernizes legacy functions, cleans up technical debt, and improves separation of concerns.",
        prompt = "You are Full-Stack Refactorer in STF Code. Audit the provided code for bad code smells, antipatterns, duplication, and poor modularity. Rewrite cleanly while preserving semantics."
    ),
    PromptPreset(
        title = "Bug Hunter & Diagnostician",
        category = "Testing",
        iconName = "BugReport",
        colorHex = "#E06C75",
        description = "Finds silent regressions, off-by-one errors, null crashes, and race conditions.",
        prompt = "You are Bug Hunter in STF Code. Scrutinize the codebase for subtle runtime bugs, missing bounds checks, null safety violations, memory leaks, and asynchronous race conditions."
    ),
    PromptPreset(
        title = "Database & SQL Architect",
        category = "Backend",
        iconName = "Storage",
        colorHex = "#4EC9B0",
        description = "Schema design, index tuning, query performance, and transactional safety.",
        prompt = "You are Database Architect in STF Code. Design normalized schemas, write high-performance queries, advise on NoSQL collections / Firestore indices, and ensure transaction ACID guarantees."
    ),
    PromptPreset(
        title = "Python Data Scientist",
        category = "Data Science",
        iconName = "Psychology",
        colorHex = "#FF9E64",
        description = "NumPy, Pandas, Scikit-learn pipelines, statistical algorithms, and data parsing.",
        prompt = "You are Python Data Scientist in STF Code. Help write vectorized calculations, clean data structures, extract insights, and optimize numerical computations."
    ),
    PromptPreset(
        title = "DevOps & Cloud Engineer",
        category = "DevOps",
        iconName = "RocketLaunch",
        colorHex = "#00D8D6",
        description = "CI/CD pipelines, Docker, shell scripts, cloud functions, and deployment workflows.",
        prompt = "You are DevOps Engineer in STF Code. Write resilient bash scripts, Dockerfiles, cloud deployment pipelines, and verify environment variable hygiene."
    )
)

fun getIconForName(name: String): ImageVector {
    return AGENT_ICONS.firstOrNull { it.first.equals(name, ignoreCase = true) }?.second
        ?: Icons.Default.AutoAwesome
}

@Composable
fun AgentHubDialog(
    activeAgentId: String,
    customAgents: List<CustomAgent>,
    onSelectAgent: (String, String, String) -> Unit, // id, name, systemInstruction
    onCreateCustomAgent: (String, String, String, String, String, String) -> Unit, // name, desc, prompt, icon, category, color
    onUpdateCustomAgent: (CustomAgent) -> Unit,
    onDeleteCustomAgent: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var agentToEdit by remember { mutableStateOf<CustomAgent?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Core", "Custom", "General", "Security", "Testing", "Design", "Optimization", "Architecture", "Backend", "Data Science", "DevOps")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agent_studio_hero_1791329603834),
                        contentDescription = "Agent Studio Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, VsCodeSurface.copy(alpha = 0.9f))
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .align(Alignment.BottomStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(VsCodeBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Agent Studio",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Add, modify, or activate any Reach Agent to assist your code.",
                    fontSize = 12.sp,
                    color = VsCodeTextSecondary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Action Bar: Create Custom Agent button
                Button(
                    onClick = {
                        agentToEdit = null
                        showEditDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("create_custom_agent_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Create Any Custom Agent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search agents...", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = VsCodeTextSecondary, modifier = Modifier.size(16.dp))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                )

                // Category Chips Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) VsCodeBlue else VsCodeSurfaceVariant)
                                .clickable { selectedCategoryFilter = cat }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else VsCodeTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Filtered Agents List
                val filteredPrebuilt = PREBUILT_AGENTS.filter { agent ->
                    val matchesQuery = searchQuery.isBlank() || agent.name.contains(searchQuery, true) || agent.description.contains(searchQuery, true)
                    val matchesCat = when (selectedCategoryFilter) {
                        "All", "Core" -> true
                        "Custom" -> false
                        else -> agent.category.equals(selectedCategoryFilter, true)
                    }
                    matchesQuery && matchesCat
                }

                val filteredCustom = customAgents.filter { agent ->
                    val matchesQuery = searchQuery.isBlank() || agent.name.contains(searchQuery, true) || agent.description.contains(searchQuery, true)
                    val matchesCat = when (selectedCategoryFilter) {
                        "All", "Custom" -> true
                        "Core" -> false
                        else -> agent.category.equals(selectedCategoryFilter, true)
                    }
                    matchesQuery && matchesCat
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredPrebuilt.isNotEmpty()) {
                        item {
                            Text(
                                text = "CORE AGENTS (${filteredPrebuilt.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VsCodeTextSecondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        items(filteredPrebuilt) { agent ->
                            val isSelected = activeAgentId == agent.id
                            AgentCard(
                                name = agent.name,
                                description = agent.description,
                                category = agent.category,
                                icon = agent.icon,
                                accentColor = agent.accentColor,
                                isSelected = isSelected,
                                isCustom = false,
                                onSelect = {
                                    onSelectAgent(agent.id, agent.name, agent.systemInstruction)
                                    onDismiss()
                                },
                                onClone = {
                                    // Clone prebuilt agent to create a customized version
                                    agentToEdit = CustomAgent(
                                        name = "${agent.name} (Custom)",
                                        description = agent.description,
                                        systemInstruction = agent.systemInstruction,
                                        iconName = agent.iconName,
                                        category = agent.category
                                    )
                                    showEditDialog = true
                                },
                                onEdit = {},
                                onDelete = {}
                            )
                        }
                    }

                    if (filteredCustom.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CUSTOM AGENTS (${filteredCustom.size})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VsCodeTextSecondary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        items(filteredCustom) { customAgent ->
                            val isSelected = activeAgentId == customAgent.id
                            val customColor = try {
                                Color(android.graphics.Color.parseColor(customAgent.accentColorHex))
                            } catch (e: Exception) {
                                VsCodeBlue
                            }
                            AgentCard(
                                name = customAgent.name,
                                description = customAgent.description,
                                category = customAgent.category,
                                icon = getIconForName(customAgent.iconName),
                                accentColor = customColor,
                                isSelected = isSelected,
                                isCustom = true,
                                onSelect = {
                                    onSelectAgent(customAgent.id, customAgent.name, customAgent.systemInstruction)
                                    onDismiss()
                                },
                                onClone = {
                                    agentToEdit = customAgent.copy(
                                        id = "",
                                        name = "${customAgent.name} (Copy)"
                                    )
                                    showEditDialog = true
                                },
                                onEdit = {
                                    agentToEdit = customAgent
                                    showEditDialog = true
                                },
                                onDelete = {
                                    onDeleteCustomAgent(customAgent.id)
                                }
                            )
                        }
                    }

                    if (filteredPrebuilt.isEmpty() && filteredCustom.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No agents found matching '$searchQuery'",
                                    fontSize = 12.sp,
                                    color = VsCodeTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = VsCodeSurface,
        shape = RoundedCornerShape(16.dp)
    )

    if (showEditDialog) {
        CreateOrEditCustomAgentDialog(
            initialAgent = agentToEdit,
            onDismiss = {
                showEditDialog = false
                agentToEdit = null
            },
            onSave = { name, desc, prompt, icon, cat, colorHex ->
                val current = agentToEdit
                if (current != null && current.id.isNotBlank()) {
                    // Update existing custom agent
                    val updated = current.copy(
                        name = name,
                        description = desc,
                        systemInstruction = prompt,
                        iconName = icon,
                        category = cat,
                        accentColorHex = colorHex
                    )
                    onUpdateCustomAgent(updated)
                } else {
                    // Create brand new custom agent
                    onCreateCustomAgent(name, desc, prompt, icon, cat, colorHex)
                }
                showEditDialog = false
                agentToEdit = null
            }
        )
    }
}

@Composable
private fun AgentCard(
    name: String,
    description: String,
    category: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    isCustom: Boolean,
    onSelect: () -> Unit,
    onClone: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.5.dp,
                if (isSelected) accentColor else VsCodeBorder.copy(alpha = 0.5f),
                RoundedCornerShape(10.dp)
            )
            .clickable { onSelect() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) VsCodeSurfaceVariant else VsCodeSurface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = category.uppercase(),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = VsCodeTextSecondary,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 2
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCustom) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Agent",
                            tint = VsCodeBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = VsCodeRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    IconButton(onClick = onClone, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Customize as New",
                            tint = VsCodeTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateOrEditCustomAgentDialog(
    initialAgent: CustomAgent?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit // name, desc, prompt, icon, category, colorHex
) {
    var agentName by remember { mutableStateOf(initialAgent?.name ?: "") }
    var agentDescription by remember { mutableStateOf(initialAgent?.description ?: "") }
    var systemPrompt by remember { mutableStateOf(initialAgent?.systemInstruction ?: "") }
    var selectedCategory by remember { mutableStateOf(initialAgent?.category ?: "Custom") }
    var selectedIconName by remember { mutableStateOf(initialAgent?.iconName ?: "SmartToy") }
    var selectedColorHex by remember { mutableStateOf(initialAgent?.accentColorHex ?: "#007ACC") }

    val isEditing = initialAgent != null && initialAgent.id.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit Agent: ${initialAgent?.name}" else "Create Custom Agent",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = VsCodeTextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Preset Templates Chips
                Text("QUICK PROMPT TEMPLATES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VsCodeTextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PROMPT_PRESETS.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VsCodeSurfaceVariant)
                                .clickable {
                                    agentName = preset.title
                                    agentDescription = preset.description
                                    systemPrompt = preset.prompt
                                    selectedCategory = preset.category
                                    selectedIconName = preset.iconName
                                    selectedColorHex = preset.colorHex
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(preset.title, fontSize = 10.sp, color = VsCodeBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Name
                OutlinedTextField(
                    value = agentName,
                    onValueChange = { agentName = it },
                    label = { Text("Agent Name (e.g. Flutter Wizard)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_agent_name_input")
                )

                // Category & Description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { selectedCategory = it },
                        label = { Text("Category") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VsCodeBlue,
                            unfocusedBorderColor = VsCodeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = agentDescription,
                        onValueChange = { agentDescription = it },
                        label = { Text("Short Description") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VsCodeBlue,
                            unfocusedBorderColor = VsCodeBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1.5f)
                    )
                }

                // Icon Picker
                Text("AVATAR ICON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VsCodeTextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AGENT_ICONS.forEach { (iconKey, vector) ->
                        val isSelected = selectedIconName == iconKey
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) VsCodeBlue else VsCodeSurfaceVariant)
                                .border(1.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                            .clickable { selectedIconName = iconKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = vector,
                                contentDescription = iconKey,
                                tint = if (isSelected) Color.White else VsCodeTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Color Picker
                Text("ACCENT COLOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VsCodeTextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AGENT_COLORS.forEach { (hex, _) ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (e: Exception) {
                            VsCodeBlue
                        }
                        val isSelected = selectedColorHex.equals(hex, true)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                .clickable { selectedColorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // System Instruction Prompt
                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("System Instructions (Persona & Rules)") },
                    placeholder = { Text("You are an expert in... Follow clean code conventions...") },
                    minLines = 4,
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VsCodeBlue,
                        unfocusedBorderColor = VsCodeBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_agent_prompt_input")
                )

                // Live Agent Preview
                Text("LIVE AGENT PREVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VsCodeTextSecondary)
                val previewColor = try {
                    Color(android.graphics.Color.parseColor(selectedColorHex))
                } catch (e: Exception) {
                    VsCodeBlue
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(VsCodeSurfaceVariant)
                        .border(1.dp, previewColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(previewColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForName(selectedIconName),
                                contentDescription = null,
                                tint = previewColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = agentName.ifBlank { "Agent Name" },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(previewColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = selectedCategory.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = previewColor
                                    )
                                }
                            }
                            Text(
                                text = agentDescription.ifBlank { "Agent description preview" },
                                fontSize = 10.sp,
                                color = VsCodeTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (agentName.isNotBlank() && systemPrompt.isNotBlank()) {
                        onSave(
                            agentName.trim(),
                            agentDescription.ifBlank { "Custom assistant" }.trim(),
                            systemPrompt.trim(),
                            selectedIconName,
                            selectedCategory.trim(),
                            selectedColorHex
                        )
                    }
                },
                enabled = agentName.isNotBlank() && systemPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = VsCodeBlue)
            ) {
                Text(if (isEditing) "Save Changes" else "Create Agent")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = VsCodeTextSecondary)
            }
        },
        containerColor = VsCodeSurface,
        shape = RoundedCornerShape(16.dp)
    )
}
