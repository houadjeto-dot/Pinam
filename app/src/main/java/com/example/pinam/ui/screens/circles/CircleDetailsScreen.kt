package com.example.pinam.ui.screens.circles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.data.model.User
import com.example.pinam.ui.components.ConfirmationDialog
import com.example.pinam.ui.theme.CoralPrimary
import com.example.pinam.viewmodel.CircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleDetailsScreen(
    circleId: String,
    viewModel: CircleViewModel,
    currentUserId: String,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val circle = state.circles.find { it.id == circleId } ?: state.activeCircle

    val clipboardManager = LocalClipboardManager.current
    var memberToRemove by remember { mutableStateOf<User?>(null) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(circle?.name ?: "") }
    var editDesc by remember { mutableStateOf(circle?.description ?: "") }

    val isOwner = circle?.ownerId == currentUserId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(circle?.name ?: "Circle Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Circle", tint = CoralPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (circle == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Circle not found")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Circle Header & Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Circle Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editDesc,
                            onValueChange = { editDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.updateCircle(circle.id, editName, editDesc, currentUserId)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                        ) {
                            Text("Save Changes")
                        }
                    } else {
                        Text(
                            text = circle.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoralPrimary
                        )
                        if (circle.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = circle.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Invite Code Copy Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Invite Code: ${circle.inviteCode}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(circle.inviteCode))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Invite Code", tint = CoralPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Members Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Members (${state.members.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Leave Circle Button
                TextButton(
                    onClick = { showLeaveDialog = true }
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Leave Circle", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Members List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.members) { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CoralPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = CoralPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.displayName.ifBlank { "Friend" },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (member.id == circle.ownerId) "Owner" else member.email,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            // Owner can remove member (except self)
                            if (isOwner && member.id != currentUserId) {
                                IconButton(onClick = { memberToRemove = member }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Friend", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Confirmation Dialog: Remove Friend
        memberToRemove?.let { member ->
            ConfirmationDialog(
                title = "Remove Friend",
                message = "Are you sure you want to remove ${member.displayName} from this circle?",
                confirmText = "Remove",
                onConfirm = {
                    viewModel.removeMember(circle.id, member.id, currentUserId)
                    memberToRemove = null
                },
                onDismiss = { memberToRemove = null }
            )
        }

        // Confirmation Dialog: Leave Circle
        if (showLeaveDialog) {
            ConfirmationDialog(
                title = "Leave Circle",
                message = "Are you sure you want to leave ${circle.name}? You will need an invite code to join back.",
                confirmText = "Leave",
                onConfirm = {
                    viewModel.leaveCircle(circle.id, currentUserId) {
                        showLeaveDialog = false
                        onBack()
                    }
                },
                onDismiss = { showLeaveDialog = false }
            )
        }
    }
}
