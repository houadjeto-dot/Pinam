package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.ui.components.ScreenScaffold
import kotlinx.coroutines.launch

// OWNER: Pair 1 (Auth + Circles). Replace this placeholder with the real Login / Sign Up UI.
@Composable
fun LoginScreen(onLoggedIn: () -> Unit) {
    val context = LocalContext.current
    val firebaseConnected = remember { FirebaseApp.getApps(context).isNotEmpty() }
    val scope = rememberCoroutineScope()
    ScreenScaffold("Pinam") { pad ->
        Column(Modifier.padding(pad).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Login / Sign Up (placeholder)")
            Text(
                if (firebaseConnected) "Firebase: connected"
                else "Firebase: NOT connected yet (add app/google-services.json)"
            )
            Button(onClick = {
                scope.launch {
                    ServiceLocator.auth.signIn("ama@example.com", "password")
                    onLoggedIn()
                }
            }) { Text("Continue with fake login") }
        }
    }
}
