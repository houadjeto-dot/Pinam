package com.group2.pinam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group2.pinam.ui.components.ScreenScaffold

@Composable
fun CirclesScreen(
    onOpenCircle: (String) -> Unit,
    viewModel: CirclesViewModel = viewModel()
) {

    val circles by viewModel.circles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val createdInviteCode by viewModel.createdInviteCode.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    var circleName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }

    ScreenScaffold("Your circles") { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Circle")
                }

                OutlinedButton(
                    onClick = { showJoinDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Join Circle")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            if (circles.isEmpty() && !isLoading) {
                Text("You haven't joined any circles yet.")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(circles, key = { it.circle.id }) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenCircle(item.circle.id)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = item.circle.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${item.circle.memberIds.size} members"
                            )

                            Text(
                                text = "${item.pinCount} pins"
                            )
                        }
                    }
                }
            }
        }
    }

    // Create Circle Dialog

    if (showCreateDialog) {

        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
            },
            title = {
                Text("Create a Circle")
            },
            text = {
                OutlinedTextField(
                    value = circleName,
                    onValueChange = { circleName = it },
                    label = { Text("Circle Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createCircle(circleName)
                        circleName = ""
                        showCreateDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Join Circle Dialog

    if (showJoinDialog) {

        AlertDialog(
            onDismissRequest = {
                showJoinDialog = false
            },
            title = {
                Text("Join a Circle")
            },
            text = {
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it },
                    label = { Text("Invitation Code") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.joinCircle(inviteCode)
                        inviteCode = ""
                        showJoinDialog = false
                    }
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJoinDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Display Invitation Code

    if (createdInviteCode != null) {

        AlertDialog(
            onDismissRequest = {
                viewModel.clearInviteCode()
            },
            title = {
                Text("Circle Created!")
            },
            text = {
                Text("Your invitation code is: $createdInviteCode")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearInviteCode()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }

    // Display Errors

    if (errorMessage != null) {

        AlertDialog(
            onDismissRequest = {
                viewModel.clearError()
            },
            title = {
                Text("Error")
            },
            text = {
                Text(errorMessage ?: "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearError()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}