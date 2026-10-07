package com.example.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.agent.ReachAgentService
import com.example.data.model.AgentMessage
import com.example.data.model.CodeFile
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.example.data.repository.CodeRepository
import com.example.ui.terminal.TerminalLog
import com.example.ui.terminal.TerminalLogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class WorkspaceViewModel(
    private val repository: CodeRepository,
    val currentUserId: String
) : ViewModel() {

    private val reachAgentService = ReachAgentService()

    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val projects: StateFlow<List<Project>> = repository.observeProjects(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFiles: StateFlow<List<CodeFile>> = repository.observeFiles(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customAgents: StateFlow<List<com.example.data.model.CustomAgent>> = repository.observeCustomAgents(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agentMessages: StateFlow<List<AgentMessage>> = repository.observeAgentMessages(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeAgentId = MutableStateFlow("agent_general")
    val activeAgentId: StateFlow<String> = _activeAgentId.asStateFlow()

    private val _activeAgentName = MutableStateFlow("Reach Generalist")
    val activeAgentName: StateFlow<String> = _activeAgentName.asStateFlow()

    private val _activeAgentPrompt = MutableStateFlow(
        "You are Reach Generalist, an expert software architect and coding assistant in STF Code. Provide clean, idiomatic code and actionable architectural advice."
    )
    val activeAgentPrompt: StateFlow<String> = _activeAgentPrompt.asStateFlow()

    private val _activeAgentApiType = MutableStateFlow("gemini")
    val activeAgentApiType: StateFlow<String> = _activeAgentApiType.asStateFlow()

    private val _activeAgentEndpointUrl = MutableStateFlow("")
    val activeAgentEndpointUrl: StateFlow<String> = _activeAgentEndpointUrl.asStateFlow()

    private val _activeAgentModelName = MutableStateFlow("")
    val activeAgentModelName: StateFlow<String> = _activeAgentModelName.asStateFlow()

    private val _activeAgentApiKey = MutableStateFlow("")
    val activeAgentApiKey: StateFlow<String> = _activeAgentApiKey.asStateFlow()

    private val _activeAgentIsLocalhost = MutableStateFlow(false)
    val activeAgentIsLocalhost: StateFlow<Boolean> = _activeAgentIsLocalhost.asStateFlow()

    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    private val _activeFile = MutableStateFlow<CodeFile?>(null)
    val activeFile: StateFlow<CodeFile?> = _activeFile.asStateFlow()

    private val _openFiles = MutableStateFlow<List<CodeFile>>(emptyList())
    val openFiles: StateFlow<List<CodeFile>> = _openFiles.asStateFlow()

    private val _isAgentThinking = MutableStateFlow(false)
    val isAgentThinking: StateFlow<Boolean> = _isAgentThinking.asStateFlow()

    val terminalLogs = mutableStateListOf<TerminalLog>(
        TerminalLog("Welcome to STF Code Terminal v1.0", TerminalLogType.INFO),
        TerminalLog("Type 'run' to execute active script or 'help' for instructions.", TerminalLogType.INFO)
    )

    private val _editorFontSize = MutableStateFlow(13)
    val editorFontSize: StateFlow<Int> = _editorFontSize.asStateFlow()

    private val _showLineNumbers = MutableStateFlow(true)
    val showLineNumbers: StateFlow<Boolean> = _showLineNumbers.asStateFlow()

    private val _wordWrap = MutableStateFlow(false)
    val wordWrap: StateFlow<Boolean> = _wordWrap.asStateFlow()

    private val _customAccentColorHex = MutableStateFlow("#007ACC")
    val customAccentColorHex: StateFlow<String> = _customAccentColorHex.asStateFlow()


    init {
        viewModelScope.launch {
            allFiles.collect { filesList ->
                // Ensure active and open files stay synchronized with updated contents from Firestore
                val currentActive = _activeFile.value
                if (currentActive != null) {
                    val updated = filesList.find { it.id == currentActive.id }
                    if (updated != null) {
                        _activeFile.value = updated
                    }
                } else if (filesList.isNotEmpty() && _openFiles.value.isEmpty()) {
                    // Auto-open first file
                    selectFile(filesList.first())
                }

                // Update open files list
                _openFiles.value = _openFiles.value.mapNotNull { openFile ->
                    filesList.find { it.id == openFile.id }
                }
            }
        }

        viewModelScope.launch {
            projects.collect { projectList ->
                if (_activeProject.value == null && projectList.isNotEmpty()) {
                    _activeProject.value = projectList.first()
                } else if (projectList.isEmpty()) {
                    seedDefaultWorkspace()
                }
            }
        }
    }

    private fun seedDefaultWorkspace() {
        viewModelScope.launch {
            val projId = "proj_starter_" + UUID.randomUUID().toString().take(6)
            val starterProject = Project(
                id = projId,
                userId = currentUserId,
                name = "Python Starter",
                description = "Starter workspace with Python scripts and Reach Agent",
                template = "python"
            )
            repository.createProject(starterProject)
            _activeProject.value = starterProject

            val mainPy = CodeFile(
                id = "file_" + UUID.randomUUID().toString().take(8),
                userId = currentUserId,
                projectId = projId,
                name = "main.py",
                path = "main.py",
                language = "python",
                content = """
                    # STF Code Mobile IDE
                    # Welcome to your cloud workspace!
                    
                    def fibonacci(n):
                        sequence = [0, 1]
                        while len(sequence) < n:
                            sequence.append(sequence[-1] + sequence[-2])
                        return sequence
                    
                    def main():
                        print("=== STF Code Runner ===")
                        count = 8
                        fib = fibonacci(count)
                        print(f"Fibonacci sequence (first {count} terms): {fib}")
                        print("Reach Agent is ready to assist with your code!")
                    
                    if __name__ == "__main__":
                        main()
                """.trimIndent()
            )

            val utilsPy = CodeFile(
                id = "file_" + UUID.randomUUID().toString().take(8),
                userId = currentUserId,
                projectId = projId,
                name = "utils.py",
                path = "utils.py",
                language = "python",
                content = """
                    # Utility functions
                    
                    def add(a, b):
                        return a + b
                    
                    def multiply(a, b):
                        return a * b
                """.trimIndent()
            )

            repository.createFile(mainPy)
            repository.createFile(utilsPy)
            selectFile(mainPy)
        }
    }

    fun selectProject(project: Project) {
        _activeProject.value = project
    }

    fun createProject(name: String, desc: String, template: String) {
        viewModelScope.launch {
            val projId = "proj_" + UUID.randomUUID().toString().take(8)
            val newProj = Project(
                id = projId,
                userId = currentUserId,
                name = name,
                description = desc,
                template = template
            )
            repository.createProject(newProj)
            _activeProject.value = newProj

            // Add default main file for new project
            val initialFileName = if (template == "javascript") "index.js" else "main.py"
            val initialContent = if (template == "javascript") {
                "console.log('STF Code: Welcome to $name');\n"
            } else {
                "print('STF Code: Welcome to $name')\n"
            }
            createFile(initialFileName, initialContent)
        }
    }

    fun selectFile(file: CodeFile) {
        _activeFile.value = file
        if (_openFiles.value.none { it.id == file.id }) {
            _openFiles.value = _openFiles.value + file
        }
    }

    fun closeFile(file: CodeFile) {
        val remaining = _openFiles.value.filter { it.id != file.id }
        _openFiles.value = remaining
        if (_activeFile.value?.id == file.id) {
            _activeFile.value = remaining.lastOrNull()
        }
    }

    fun createFile(name: String, content: String) {
        viewModelScope.launch {
            val projId = _activeProject.value?.id ?: "proj_default"
            val lang = when {
                name.endsWith(".py") -> "python"
                name.endsWith(".js") -> "javascript"
                name.endsWith(".kt") -> "kotlin"
                name.endsWith(".json") -> "json"
                name.endsWith(".html") -> "html"
                name.endsWith(".css") -> "css"
                else -> "text"
            }

            val newFile = CodeFile(
                id = "file_" + UUID.randomUUID().toString().take(8),
                userId = currentUserId,
                projectId = projId,
                name = name,
                path = name,
                language = lang,
                content = content
            )
            repository.createFile(newFile)
            selectFile(newFile)
        }
    }

    fun saveFileContent(file: CodeFile, newContent: String) {
        viewModelScope.launch {
            _activeFile.value = file.copy(content = newContent)
            repository.updateFile(file.id, newContent)
        }
    }

    fun deleteFile(file: CodeFile) {
        viewModelScope.launch {
            closeFile(file)
            repository.deleteFile(file.id)
        }
    }

    fun executeTerminalCommand(command: String) {
        terminalLogs.add(TerminalLog(command, TerminalLogType.COMMAND))

        val trimmed = command.trim()
        val parts = trimmed.split("\\s+".toRegex())
        val cmd = parts.firstOrNull()?.lowercase() ?: ""

        when {
            cmd == "run" || cmd.startsWith("python") || cmd.startsWith("node") -> {
                val file = _activeFile.value
                if (file == null) {
                    terminalLogs.add(TerminalLog("Error: No file open in editor to run.", TerminalLogType.ERROR))
                } else {
                    terminalLogs.add(TerminalLog("▶ Running ${file.name}...", TerminalLogType.INFO))
                    val startTime = System.currentTimeMillis()

                    // Parse and run code output simulations
                    val outputs = simulateCodeExecution(file)
                    outputs.forEach { out ->
                        terminalLogs.add(TerminalLog(out, TerminalLogType.SUCCESS))
                    }
                    val elapsed = System.currentTimeMillis() - startTime
                    terminalLogs.add(TerminalLog("Process finished with exit code 0 (${elapsed}ms)", TerminalLogType.INFO))
                }
            }
            cmd == "ls" -> {
                val currentFiles = allFiles.value.filter { it.projectId == _activeProject.value?.id }
                if (currentFiles.isEmpty()) {
                    terminalLogs.add(TerminalLog("(empty directory)", TerminalLogType.INFO))
                } else {
                    val fileListStr = currentFiles.joinToString("   ") { it.name }
                    terminalLogs.add(TerminalLog(fileListStr, TerminalLogType.SUCCESS))
                }
            }
            cmd == "pwd" -> {
                terminalLogs.add(TerminalLog("/workspace/${_activeProject.value?.name ?: "default"}", TerminalLogType.INFO))
            }
            cmd == "git" -> {
                if (parts.getOrNull(1) == "status") {
                    terminalLogs.add(TerminalLog("On branch main\nYour branch is up to date with 'origin/main'.\nNothing to commit, working tree clean", TerminalLogType.SUCCESS))
                } else {
                    terminalLogs.add(TerminalLog("git status, git pull, git commit are supported", TerminalLogType.INFO))
                }
            }
            cmd == "reach" && parts.getOrNull(1) == "agent" -> {
                val prompt = parts.drop(2).joinToString(" ")
                if (prompt.isBlank()) {
                    terminalLogs.add(TerminalLog("Usage: reach agent <your request>", TerminalLogType.WARNING))
                } else {
                    sendAgentPrompt(prompt)
                    terminalLogs.add(TerminalLog("Reach Agent prompt dispatched: '$prompt'. Check Reach Agent tab for response.", TerminalLogType.SUCCESS))
                }
            }
            cmd == "help" -> {
                terminalLogs.add(TerminalLog("""
                    Available commands:
                      run              - Execute current active editor file
                      python <file>    - Run python script
                      node <file>      - Run javascript script
                      ls               - List files in workspace
                      pwd              - Print current working directory
                      git status       - Check workspace version control state
                      reach agent <q>  - Dispatch prompt to Reach Agent
                      clear            - Clear terminal screen
                """.trimIndent(), TerminalLogType.INFO))
            }
            else -> {
                terminalLogs.add(TerminalLog("command not found: $cmd. Type 'help' for commands.", TerminalLogType.ERROR))
            }
        }
    }

    private fun simulateCodeExecution(file: CodeFile): List<String> {
        val outputs = mutableListOf<String>()
        val lines = file.content.lines()

        for (line in lines) {
            val trimmedLine = line.trim()
            // Look for print(...) in Python or console.log(...) in JS
            if (trimmedLine.startsWith("print(") && trimmedLine.endsWith(")")) {
                val inside = trimmedLine.removePrefix("print(").removeSuffix(")")
                val clean = inside.trim('"', '\'')
                outputs.add(clean)
            } else if (trimmedLine.startsWith("console.log(") && trimmedLine.endsWith(");")) {
                val inside = trimmedLine.removePrefix("console.log(").removeSuffix(");")
                val clean = inside.trim('"', '\'')
                outputs.add(clean)
            }
        }

        if (outputs.isEmpty()) {
            outputs.add("Executing ${file.name} (no output printed)")
        }
        return outputs
    }

    fun setActiveAgent(
        id: String,
        name: String,
        prompt: String,
        apiType: String = "gemini",
        endpointUrl: String = "",
        modelName: String = "",
        apiKey: String = "",
        isLocalhost: Boolean = false
    ) {
        _activeAgentId.value = id
        _activeAgentName.value = name
        _activeAgentPrompt.value = prompt
        _activeAgentApiType.value = apiType
        _activeAgentEndpointUrl.value = endpointUrl
        _activeAgentModelName.value = modelName
        _activeAgentApiKey.value = apiKey
        _activeAgentIsLocalhost.value = isLocalhost
    }

    suspend fun testAgentEndpoint(
        apiType: String,
        endpointUrl: String,
        apiKey: String,
        modelName: String
    ): Pair<Boolean, String> {
        return reachAgentService.testConnection(apiType, endpointUrl, apiKey, modelName)
    }

    fun createCustomAgent(
        name: String,
        desc: String,
        prompt: String,
        icon: String,
        category: String,
        accentColorHex: String = "#007ACC",
        apiType: String = "gemini",
        endpointUrl: String = "",
        modelName: String = "",
        apiKey: String = "",
        isLocalhost: Boolean = false
    ) {
        viewModelScope.launch {
            val agentId = "agent_custom_" + UUID.randomUUID().toString().take(8)
            val newAgent = com.example.data.model.CustomAgent(
                id = agentId,
                userId = currentUserId,
                name = name,
                description = desc,
                systemInstruction = prompt,
                iconName = icon,
                accentColorHex = accentColorHex,
                category = category,
                apiType = apiType,
                endpointUrl = endpointUrl,
                modelName = modelName,
                apiKey = apiKey,
                isLocalhost = isLocalhost
            )
            repository.createCustomAgent(newAgent)
            setActiveAgent(agentId, name, prompt, apiType, endpointUrl, modelName, apiKey, isLocalhost)
        }
    }

    fun updateCustomAgent(agent: com.example.data.model.CustomAgent) {
        viewModelScope.launch {
            repository.updateCustomAgent(agent)
            if (_activeAgentId.value == agent.id) {
                setActiveAgent(
                    agent.id,
                    agent.name,
                    agent.systemInstruction,
                    agent.apiType,
                    agent.endpointUrl,
                    agent.modelName,
                    agent.apiKey,
                    agent.isLocalhost
                )
            }
        }
    }

    fun deleteCustomAgent(agentId: String) {
        viewModelScope.launch {
            if (_activeAgentId.value == agentId) {
                setActiveAgent(
                    "agent_general",
                    "Reach Generalist",
                    "You are Reach Generalist, an expert software architect and coding assistant in STF Code. Provide clean, idiomatic code and actionable architectural advice.",
                    "gemini",
                    "",
                    "",
                    "",
                    false
                )
            }
            repository.deleteCustomAgent(agentId)
        }
    }

    fun setEditorFontSize(size: Int) {
        _editorFontSize.value = size
    }

    fun toggleLineNumbers() {
        _showLineNumbers.value = !_showLineNumbers.value
    }

    fun toggleWordWrap() {
        _wordWrap.value = !_wordWrap.value
    }

    fun setCustomAccentColor(hex: String) {
        _customAccentColorHex.value = hex
    }

    fun updateTheme(themeName: String) {
        viewModelScope.launch {
            repository.updateUserProfileTheme(themeName)
        }
    }

    fun sendAgentPrompt(prompt: String) {
        viewModelScope.launch {
            val userMsg = AgentMessage(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                userId = currentUserId,
                role = "user",
                content = prompt
            )
            repository.createAgentMessage(userMsg)

            _isAgentThinking.value = true

            val active = _activeFile.value
            val responseText = reachAgentService.promptAgent(
                systemInstruction = _activeAgentPrompt.value,
                userPrompt = prompt,
                fileContext = active?.content,
                language = active?.language,
                apiType = _activeAgentApiType.value,
                endpointUrl = _activeAgentEndpointUrl.value,
                modelName = _activeAgentModelName.value,
                apiKey = _activeAgentApiKey.value,
                isLocalhost = _activeAgentIsLocalhost.value
            )

            val agentMsg = AgentMessage(
                id = "msg_" + UUID.randomUUID().toString().take(8),
                userId = currentUserId,
                role = "agent",
                content = responseText
            )
            repository.createAgentMessage(agentMsg)
            _isAgentThinking.value = false
        }
    }

    fun applyAgentCodeToEditor(code: String) {
        val active = _activeFile.value ?: return
        saveFileContent(active, code)
    }

    fun clearTerminal() {
        terminalLogs.clear()
        terminalLogs.add(TerminalLog("Terminal cleared.", TerminalLogType.INFO))
    }
}

