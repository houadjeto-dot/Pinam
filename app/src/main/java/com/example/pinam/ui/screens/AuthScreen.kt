package com.example.pinam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.ui.components.PinamButton
import com.example.pinam.ui.components.PinamTextField
import com.example.pinam.ui.theme.PinamTheme
import com.example.pinam.ui.theme.PrimaryPurple
import com.example.pinam.ui.theme.TextNearBlack
import com.example.pinam.ui.theme.TextSecondaryGray
import com.example.pinam.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = remember { AuthViewModel() },
    onAuthSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pinam",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextNearBlack
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Drop memories where they happened.",
                fontSize = 14.sp,
                color = TextSecondaryGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message Banner
            state.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Optional Display Name Field (for Sign Up)
            if (state.isSignUpMode) {
                PinamTextField(
                    value = state.displayName,
                    onValueChange = viewModel::onDisplayNameChanged,
                    label = "Display Name",
                    placeholder = "Ama Osei"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            PinamTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email",
                placeholder = "ama@example.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            PinamTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                label = "Password",
                placeholder = "••••••••"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Submit Action Button (Sign Up or Log In)
            PinamButton(
                text = if (state.isSignUpMode) "Sign Up" else "Log In",
                onClick = { viewModel.submitAuth(onAuthSuccess) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // OR Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Text(
                    text = "  or  ",
                    fontSize = 12.sp,
                    color = TextSecondaryGray
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Continue with Google Button
            PinamButton(
                text = "Continue with Google",
                onClick = {
                    if (state.email.isBlank()) viewModel.onEmailChanged("ama@example.com")
                    if (state.password.isBlank()) viewModel.onPasswordChanged("password123")
                    viewModel.submitAuth(onAuthSuccess)
                },
                isOutlined = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Mode Link (Log In <-> Sign Up)
            Row(
                modifier = Modifier
                    .clickable { viewModel.toggleAuthMode() }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.isSignUpMode) "Already have an account? " else "Don't have an account? ",
                    fontSize = 14.sp,
                    color = TextSecondaryGray
                )
                Text(
                    text = if (state.isSignUpMode) "Log in" else "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    PinamTheme {
        AuthScreen()
    }
}
