package com.group2.pinam.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.model.Pin
import com.group2.pinam.model.Vibe
import com.group2.pinam.ui.components.ScreenScaffold
import kotlinx.coroutines.launch

@Composable
fun AddPinScreen(circleId: String, onDone: () -> Unit) {
    var placeName by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf("") }
    var selectedVibe by remember { mutableStateOf(Vibe.COZY.label) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        photoUri = uri
    }

    val scope = rememberCoroutineScope()
    val authUser = FirebaseAuth.getInstance().currentUser

    ScreenScaffold("Add Pin", onDone) { pad ->
        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = { Text("Place Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Caption / Review") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Vibe", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Vibe.entries.forEach { vibe ->
                    FilterChip(
                        selected = selectedVibe == vibe.label,
                        onClick = { selectedVibe = vibe.label },
                        label = { Text(vibe.label) }
                    )
                }
            }

            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Selected photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Button(onClick = { launcher.launch("image/*") }) {
                Text(if (photoUri == null) "Pick Photo" else "Change Photo")
            }

            Button(
                onClick = {
                    if (placeName.isNotBlank()) {
                        isUploading = true
                        scope.launch {
                            val userId = authUser?.uid ?: "u1"
                            val userName = authUser?.displayName ?: "User"
                            ServiceLocator.pins.addPin(
                                Pin(
                                    circleId = circleId,
                                    authorId = userId,
                                    authorName = userName,
                                    lat = 5.5560,
                                    lng = -0.1820,
                                    placeName = placeName,
                                    vibe = selectedVibe,
                                    caption = caption
                                ),
                                photoUri = photoUri?.toString()
                            )
                            isUploading = false
                            onDone()
                        }
                    }
                },
                enabled = placeName.isNotBlank() && !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isUploading) "Saving Pin & Uploading Photo..." else "Save Pin")
            }
        }
    }
}
