package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CodeFile
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class CodeRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveUserProfile_authenticatedUser_success(): Unit = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = CodeRepository(firestore)

    val profile = UserProfile(
      userId = uid,
      email = ALICE_EMAIL,
      displayName = "Alice Dev",
      theme = "dark_modern"
    )

    val result = withTimeout(DEFAULT_TIMEOUT_MS) { repo.saveUserProfile(profile) }
    assertTrue(result.isSuccess)

    val fetched = withTimeout(DEFAULT_TIMEOUT_MS) { repo.getUserProfile(uid) }
    assertTrue(fetched.isSuccess)
    assertEquals("Alice Dev", fetched.getOrNull()?.displayName)
  }

  @Test
  fun createProjectAndFile_authenticatedUser_success(): Unit = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = CodeRepository(firestore)

    val projectId = "proj_" + UUID.randomUUID().toString().take(8)
    val project = Project(
      id = projectId,
      userId = uid,
      name = "Demo Python",
      description = "Demo project",
      template = "python"
    )

    val projResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.createProject(project) }
    assertTrue(projResult.isSuccess)

    val fileId = "file_" + UUID.randomUUID().toString().take(8)
    val codeFile = CodeFile(
      id = fileId,
      userId = uid,
      projectId = projectId,
      name = "main.py",
      path = "main.py",
      language = "python",
      content = "print('hello')"
    )

    val fileResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.createFile(codeFile) }
    assertTrue(fileResult.isSuccess)

    val file = withTimeout(DEFAULT_TIMEOUT_MS) { repo.getFile(fileId) }
    assertTrue(file.isSuccess)
    assertEquals("print('hello')", file.getOrNull()?.content)
  }

  @Test
  fun crossUserAccess_bobCannotReadFileOfAlice(): Unit = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = CodeRepository(firestore)

    val fileId = "file_" + UUID.randomUUID().toString().take(8)
    val codeFile = CodeFile(
      id = fileId,
      userId = aliceUid,
      projectId = "proj1",
      name = "secret.py",
      path = "secret.py",
      language = "python",
      content = "SECRET=123"
    )
    val fileResult = withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.createFile(codeFile) }
    assertTrue(fileResult.isSuccess)

    signInTestUser(BOB_EMAIL)
    // Directly query Alice's file path as Bob
    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        val snapshot = firestore.collection("users").document(aliceUid).collection("files").document(fileId).get()
        snapshot.await()
      }
      fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }
  }

  @Test
  fun unauthenticatedAccess_failsWithPermissionDenied(): Unit = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = CodeRepository(firestore)

    val fileId = "file_" + UUID.randomUUID().toString().take(8)
    val codeFile = CodeFile(
      id = fileId,
      userId = aliceUid,
      projectId = "proj1",
      name = "test.py",
      path = "test.py",
      language = "python",
      content = "print('hi')"
    )
    aliceRepo.createFile(codeFile)

    auth.signOut()
    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        val snapshot = firestore.collection("users").document(aliceUid).collection("files").document(fileId).get()
        snapshot.await()
      }
      fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@stfcode.com"
    const val BOB_EMAIL = "bob@stfcode.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
  }
}
