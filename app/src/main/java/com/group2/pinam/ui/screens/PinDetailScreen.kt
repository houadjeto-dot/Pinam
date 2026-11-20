package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.ui.components.ScreenScaffold
import com.group2.pinam.ui.components.VibeChip
import kotlinx.coroutines.launch

@Composable
fun PinDetailScreen(circleId: String, pinId: String, onBack: () -> Unit) {
    val flow = remember(circleId, pinId) { ServiceLocator.pins.observePin(circleId, pinId) }
    val pin by flow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "u1"

    ScreenScaffold("Pin Detail", onBack) { pad ->
        val p = pin
        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (p == null) {
                Text("Pin not found")
            } else {
                Text(p.placeName, style = MaterialTheme.typography.headlineSmall)
                VibeChip(p.vibe)
                Text("Added by ${p.authorName}", style = MaterialTheme.typography.bodyMedium)

                if (p.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = p.photoUrl,
                        contentDescription = p.placeName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }

                if (p.caption.isNotEmpty()) {
                    Text(p.caption, style = MaterialTheme.typography.bodyLarge)
                }

                val been = currentUserId in p.beenThere
                val want = currentUserId in p.wantToGo

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (been) {
                        Button(onClick = { scope.launch { ServiceLocator.pins.toggleBeenThere(circleId, pinId, currentUserId) } }) {
                            Text("Been there ✓")
                        }
                    } else {
                        OutlinedButton(onClick = { scope.launch { ServiceLocator.pins.toggleBeenThere(circleId, pinId, currentUserId) } }) {
                            Text("Been there")
                        }
                    }

                    if (want) {
                        Button(onClick = { scope.launch { ServiceLocator.pins.toggleWantToGo(circleId, pinId, currentUserId) } }) {
                            Text("Want to go ★")
                        }
                    } else {
                        OutlinedButton(onClick = { scope.launch { ServiceLocator.pins.toggleWantToGo(circleId, pinId, currentUserId) } }) {
                            Text("Want to go")
                        }
                    }
                }

                Text("Reactions: ${p.reactions.size}", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {
                        val currentReaction = p.reactions[currentUserId]
                        val newReaction = if (currentReaction == "heart") null else "heart"
                        scope.launch { ServiceLocator.pins.setReaction(circleId, pinId, currentUserId, newReaction) }
                    }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Heart",
                            tint = if (p.reactions[currentUserId] == "heart") Color.Red else Color.Gray
                        )
                    }
                    IconButton(onClick = {
                        val currentReaction = p.reactions[currentUserId]
                        val newReaction = if (currentReaction == "star") null else "star"
                        scope.launch { ServiceLocator.pins.setReaction(circleId, pinId, currentUserId, newReaction) }
                    }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Star",
                            tint = if (p.reactions[currentUserId] == "star") Color.Yellow else Color.Gray
                        )
                    }
                }

                if (p.authorId == currentUserId) {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                ServiceLocator.pins.deletePin(circleId, pinId)
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Pin")
                        Spacer(Modifier.width(8.dp))
                        Text("Delete Pin")
                    }
                }
            }
        }
    }
}
