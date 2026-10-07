package com.example.data.model

import com.google.firebase.Timestamp

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val theme: String = "dark_modern",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class Project(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val template: String = "python",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class CodeFile(
    val id: String = "",
    val userId: String = "",
    val projectId: String = "",
    val name: String = "",
    val path: String = "",
    val language: String = "python",
    val content: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class AgentMessage(
    val id: String = "",
    val userId: String = "",
    val role: String = "user", // "user" or "agent"
    val content: String = "",
    val codeSnippet: String? = null,
    val createdAt: Timestamp? = null
)

data class CustomAgent(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val systemInstruction: String = "",
    val iconName: String = "AutoAwesome",
    val accentColorHex: String = "#007ACC",
    val category: String = "general",
    val apiType: String = "gemini", // "gemini", "ollama_localhost", "lm_studio_localhost", "openai_compatible", "custom_rest"
    val endpointUrl: String = "",
    val modelName: String = "",
    val apiKey: String = "",
    val isLocalhost: Boolean = false,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

