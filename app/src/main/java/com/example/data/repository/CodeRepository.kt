package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AgentMessage
import com.example.data.model.CodeFile
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CodeRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val snapshot = docRef.get().await()
            val data = mutableMapOf<String, Any>(
                "userId" to uid,
                "email" to profile.email,
                "displayName" to profile.displayName,
                "theme" to profile.theme,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (profile.photoUrl != null) {
                data["photoUrl"] = profile.photoUrl
            }

            if (!snapshot.exists()) {
                data["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(data).await()
            } else {
                docRef.update(data as Map<String, Any>).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else null
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                throw e
            }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        val path = "users/$userId"
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            val profile = if (snapshot.exists()) {
                snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else null
            Result.success(profile)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun createProject(project: Project): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/projects"
        val docRef = db.collection("users").document(uid).collection("projects").document(project.id)
        return try {
            val data = mapOf(
                "id" to project.id,
                "userId" to uid,
                "name" to project.name,
                "description" to project.description,
                "template" to project.template,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(project.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeProjects(userId: String): Flow<List<Project>> {
        val path = "users/$userId/projects"
        return db.collection("users").document(userId).collection("projects")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Project::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun createFile(file: CodeFile): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/files"
        val docRef = db.collection("users").document(uid).collection("files").document(file.id)
        return try {
            val data = mapOf(
                "id" to file.id,
                "userId" to uid,
                "projectId" to file.projectId,
                "name" to file.name,
                "path" to file.path,
                "language" to file.language,
                "content" to file.content,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(file.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateFile(fileId: String, content: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/files/$fileId"
        val docRef = db.collection("users").document(uid).collection("files").document(fileId)
        return try {
            val data = mapOf(
                "content" to content,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteFile(fileId: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/files/$fileId"
        val docRef = db.collection("users").document(uid).collection("files").document(fileId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    fun observeFiles(userId: String): Flow<List<CodeFile>> {
        val path = "users/$userId/files"
        return db.collection("users").document(userId).collection("files")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(CodeFile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun getFile(fileId: String): Result<CodeFile?> {
        val uid = requireUserId()
        val path = "users/$uid/files/$fileId"
        return try {
            val snapshot = db.collection("users").document(uid).collection("files").document(fileId).get().await()
            val file = if (snapshot.exists()) {
                snapshot.toObject(CodeFile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            } else null
            Result.success(file)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun createAgentMessage(message: AgentMessage): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/agent_messages"
        val docRef = db.collection("users").document(uid).collection("agent_messages").document(message.id)
        return try {
            val data = mutableMapOf<String, Any>(
                "id" to message.id,
                "userId" to uid,
                "role" to message.role,
                "content" to message.content,
                "createdAt" to FieldValue.serverTimestamp()
            )
            if (message.codeSnippet != null) {
                data["codeSnippet"] = message.codeSnippet
            }
            docRef.set(data).await()
            Result.success(message.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeAgentMessages(userId: String): Flow<List<AgentMessage>> {
        val path = "users/$userId/agent_messages"
        return db.collection("users").document(userId).collection("agent_messages")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(AgentMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.createdAt?.seconds ?: 0L }
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun createCustomAgent(agent: com.example.data.model.CustomAgent): Result<String> {
        val uid = requireUserId()
        val path = "users/$uid/custom_agents"
        val docRef = db.collection("users").document(uid).collection("custom_agents").document(agent.id)
        return try {
            val data = mapOf(
                "id" to agent.id,
                "userId" to uid,
                "name" to agent.name,
                "description" to agent.description,
                "systemInstruction" to agent.systemInstruction,
                "iconName" to agent.iconName,
                "accentColorHex" to agent.accentColorHex,
                "category" to agent.category,
                "apiType" to agent.apiType,
                "endpointUrl" to agent.endpointUrl,
                "modelName" to agent.modelName,
                "apiKey" to agent.apiKey,
                "isLocalhost" to agent.isLocalhost,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(agent.id)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateCustomAgent(agent: com.example.data.model.CustomAgent): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/custom_agents/${agent.id}"
        val docRef = db.collection("users").document(uid).collection("custom_agents").document(agent.id)
        return try {
            val data = mapOf(
                "name" to agent.name,
                "description" to agent.description,
                "systemInstruction" to agent.systemInstruction,
                "iconName" to agent.iconName,
                "accentColorHex" to agent.accentColorHex,
                "category" to agent.category,
                "apiType" to agent.apiType,
                "endpointUrl" to agent.endpointUrl,
                "modelName" to agent.modelName,
                "apiKey" to agent.apiKey,
                "isLocalhost" to agent.isLocalhost,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    fun observeCustomAgents(userId: String): Flow<List<com.example.data.model.CustomAgent>> {
        val path = "users/$userId/custom_agents"
        return db.collection("users").document(userId).collection("custom_agents")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(com.example.data.model.CustomAgent::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                throw e
            }
    }

    suspend fun deleteCustomAgent(agentId: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid/custom_agents/$agentId"
        val docRef = db.collection("users").document(uid).collection("custom_agents").document(agentId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfileTheme(theme: String): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        return try {
            val data = mapOf(
                "theme" to theme,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }
}

