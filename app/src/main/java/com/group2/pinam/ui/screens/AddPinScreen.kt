package com.group2.pinam.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group2.pinam.model.Vibe
import com.group2.pinam.ui.components.ScreenScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(
    circleId: String,
    onDone: () -> Unit
) {
    val vm: AddPinViewModel = viewModel()

    val placeName by vm.placeName.collectAsState()
    val caption by vm.caption.collectAsState()
    val selectedVibe by vm.selectedVibe.collectAsState()
    val isSaving by vm.isSaving.collectAsState()
    val errorMessage by vm.errorMessage.collectAsState()

    var selectedPhotoUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Gallery photo picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedPhotoUri = uri
    }

    ScreenScaffold(
        title = "Add Pin",
        onBack = onDone
    ) { pad ->

        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .imePadding()
        ) {

            // Scrollable form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Share a new place",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Add a photo and tell your circle about this spot.",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Photo section
                Text(
                    text = "Place photo",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                ) {
                    Text("Choose from Gallery")
                }

                // Display selected photo using Coil
                selectedPhotoUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected place photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                    )
                }

                // Place name
                OutlinedTextField(
                    value = placeName,
                    onValueChange = vm::onPlaceNameChange,
                    label = { Text("Place name") },
                    placeholder = {
                        Text("e.g. My favourite restaurant")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving
                )

                // Vibe selection
                Text(
                    text = "Choose a vibe",
                    style = MaterialTheme.typography.titleMedium
                )

                Vibe.entries.forEach { vibe ->
                    FilterChip(
                        selected = selectedVibe == vibe,
                        onClick = {
                            vm.onVibeChange(vibe)
                        },
                        label = {
                            Text(vibe.label)
                        },
                        enabled = !isSaving
                    )
                }

                // Caption
                OutlinedTextField(
                    value = caption,
                    onValueChange = vm::onCaptionChange,
                    label = { Text("One-line caption") },
                    placeholder = {
                        Text("Tell your circle about this place...")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !isSaving
                )

                // Error message
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
// Fixed Drop Pin button
    Button(
        onClick = {
            vm.savePin(
                circleId = circleId,
                photoUri = selectedPhotoUri?.toString(),
                onDone = onDone
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            ),
        enabled = !isSaving
    ) {
        Text(
            if (isSaving) "Saving..." else "Drop Pin"
        )
    }
}

