package com.example.pinam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.ui.components.PinamButton
import com.example.pinam.ui.components.PinamTextField
import com.example.pinam.ui.theme.*

// Delete Pin Confirmation Dialog
@Composable
fun DeletePinDialog(
    onDismiss: () -> Unit = {},
    onConfirmDelete: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete this pin?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextNearBlack
            )
        },
        text = {
            Text(
                text = "Your friends will no longer see it.",
                fontSize = 14.sp,
                color = TextSecondaryGray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(
                    text = "Delete",
                    color = DestructiveRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

// Leave Circle Confirmation Dialog
@Composable
fun LeaveCircleDialog(
    circleName: String = "Osu Crew",
    onDismiss: () -> Unit = {},
    onConfirmLeave: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Leave $circleName?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextNearBlack
            )
        },
        text = {
            Text(
                text = "You can rejoin with a code.",
                fontSize = 14.sp,
                color = TextSecondaryGray
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmLeave) {
                Text(
                    text = "Leave",
                    color = DestructiveRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = PrimaryPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

// Create Circle Bottom Sheet / Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCircleBottomSheet(
    onDismiss: () -> Unit = {},
    onCreateSuccess: (String) -> Unit = {}
) {
    var circleName by remember { mutableStateOf("Osu Crew") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "New circle",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextNearBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            PinamTextField(
                value = circleName,
                onValueChange = { circleName = it },
                label = "Circle name",
                placeholder = "e.g. Osu Crew"
            )

            Spacer(modifier = Modifier.height(28.dp))

            PinamButton(
                text = "Share invite link",
                onClick = { onCreateSuccess(circleName) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeletePinDialogPreview() {
    PinamTheme {
        DeletePinDialog()
    }
}

@Preview(showBackground = true)
@Composable
fun LeaveCircleDialogPreview() {
    PinamTheme {
        LeaveCircleDialog()
    }
}
