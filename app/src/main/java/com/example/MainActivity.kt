package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.model.UserProfile
import com.example.data.repository.CodeRepository
import com.example.ui.MainApp
import com.example.ui.auth.AuthManager
import com.example.ui.auth.AuthScreen
import com.example.ui.theme.StfCodeTheme
import com.example.ui.viewmodel.WorkspaceViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StfCodeTheme {
                AppRootContent()
            }
        }
    }
}

@Composable
fun AppRootContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentUser by remember { mutableStateOf<FirebaseUser?>(Firebase.auth.currentUser) }

    // Listen to Firebase auth state changes
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    // Silent auto-sign-in on startup
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            AuthManager.attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {
                    currentUser = Firebase.auth.currentUser
                },
                onUnauthenticated = {
                    // Stay on AuthScreen
                },
                scope = scope
            )
        }
    }

    val user = currentUser
    if (user == null) {
        // Unauthenticated session: Render login & account creation interface
        AuthScreen(
            onAuthSuccess = {
                val loggedInUser = Firebase.auth.currentUser
                currentUser = loggedInUser
                if (loggedInUser != null) {
                    scope.launch {
                        val repo = CodeRepository(context)
                        val existingProfile = repo.getUserProfile(loggedInUser.uid).getOrNull()
                        if (existingProfile == null) {
                            repo.saveUserProfile(
                                UserProfile(
                                    userId = loggedInUser.uid,
                                    email = loggedInUser.email ?: "",
                                    displayName = loggedInUser.displayName ?: "Developer",
                                    photoUrl = loggedInUser.photoUrl?.toString(),
                                    theme = "dark_modern"
                                )
                            )
                        }
                    }
                }
            }
        )
    } else {
        // Authenticated session: Key WorkspaceViewModel by current user ID
        val workspaceViewModel: WorkspaceViewModel = viewModel(
            key = user.uid,
            factory = viewModelFactory {
                initializer {
                    val repo = CodeRepository(context)
                    WorkspaceViewModel(repo, user.uid)
                }
            }
        )

        val userProfile by workspaceViewModel.userProfile.collectAsState()
        val currentTheme = userProfile?.theme ?: "dark_modern"

        StfCodeTheme(themeName = currentTheme) {
            MainApp(
                viewModel = workspaceViewModel,
                onSignOutSuccess = {
                    currentUser = null
                }
            )
        }
    }
}
