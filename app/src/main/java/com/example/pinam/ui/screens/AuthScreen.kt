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

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("ama@example.com") }
    var password by remember { mutableStateOf("••••••••") }

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

            Spacer(modifier = Modifier.height(36.dp))

            // Email & Password Fields
            PinamTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "ama@example.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            PinamTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "••••••••"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign Up Button
            PinamButton(
                text = "Sign Up",
                onClick = onAuthSuccess,
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
                onClick = onAuthSuccess,
                isOutlined = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Log in link
            Row(
                modifier = Modifier.clickable { onAuthSuccess() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = TextSecondaryGray
                )
                Text(
                    text = "Log in",
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
