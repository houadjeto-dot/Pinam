package com.example.pinam.ui.screens.circles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.ui.components.EmptyStateView
import com.example.pinam.ui.components.LoadingOverlay
import com.example.pinam.ui.theme.CoralPrimary
import com.example.pinam.viewmodel.CircleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CirclesListScreen(
    viewModel: CircleViewModel,
    currentUserId: String,
    onSelectCircleMap: (String) -> Unit,
    onOpenCircleDetails: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) {
            viewModel.loadCircles(currentUserId)
        }
    }

    var newCircleName by remember { mutableStateOf("") }
    var newCircleDesc by remember { mutableStateOf("") }
    var joinInviteCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Circles", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.toggleJoinDialog(true) }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = "Join Circle", tint = CoralPrimary)
                    }
                    IconButton(onClick = { viewModel.toggleCreateDialog(true) }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Circle", tint = CoralPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.circles.isEmpty()) {
                LoadingOverlay(message = "Loading your circles...")
            } else if (state.circles.isEmpty()) {
                EmptyStateView(
                    message = "No circles yet, create your first one or join with an invite code!",
                    buttonText = "Create Circle",
                    onButtonClick = { viewModel.toggleCreateDialog(true) }
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.circles) { circle ->
                        val isActive = state.activeCircle?.id == circle.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCircle(circle)
                                    onSelectCircleMap(circle.id)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = circle.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) CoralPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (circle.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = circle.description,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${circle.memberIds.size} members • Code: ${circle.inviteCode}",
                                        fontSize = 12.sp,
                                        color = CoralPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                IconButton(onClick = { onOpenCircleDetails(circle.id) }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Circle Details",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = CoralPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Create Circle Dialog
            if (state.showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleCreateDialog(false) },
                    title = { Text("Create New Circle", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newCircleName,
                                onValueChange = { newCircleName = it },
                                label = { Text("Circle Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newCircleDesc,
                                onValueChange = { newCircleDesc = it },
                                label = { Text("Short Description") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.createCircle(newCircleName, newCircleDesc, currentUserId)
                                newCircleName = ""
                                newCircleDesc = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.toggleCreateDialog(false) }) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Join Circle Dialog
            if (state.showJoinDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleJoinDialog(false) },
                    title = { Text("Join a Circle", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("Enter the 6-character invite code from your friend:")
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = joinInviteCode,
                                onValueChange = { joinInviteCode = it.uppercase() },
                                label = { Text("Invite Code (e.g. PIN123)") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.joinCircle(joinInviteCode, currentUserId)
                                joinInviteCode = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                        ) {
                            Text("Join")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.toggleJoinDialog(false) }) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}
