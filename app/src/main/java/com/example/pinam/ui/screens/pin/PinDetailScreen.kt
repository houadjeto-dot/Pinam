package com.example.pinam.ui.screens.pin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pinam.data.repository.ReactionRepository
import com.example.pinam.ui.components.ConfirmationDialog
import com.example.pinam.ui.theme.CoralPrimary
import com.example.pinam.viewmodel.PinViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailScreen(
    pinId: String,
    pinViewModel: PinViewModel,
    currentUserId: String,
    onBack: () -> Unit
) {
    val pinsState by pinViewModel.pinsState.collectAsState()
    val pin = pinsState.pins.find { it.id == pinId } ?: pinsState.selectedPin

    val reactionRepo = remember { ReactionRepository() }
    val scope = rememberCoroutineScope()

    var beenThereCount by remember { mutableIntStateOf(0) }
    var wantToGoCount by remember { mutableIntStateOf(0) }
    var userHasBeenThere by remember { mutableStateOf(false) }
    var userHasWantToGo by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }
    var editCaption by remember { mutableStateOf(pin?.caption ?: "") }
    var editVibeTag by remember { mutableStateOf(pin?.vibeTag ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    fun loadReactions() {
        if (pinId.isNotBlank()) {
            scope.launch {
                reactionRepo.getReactionsForPin(pinId).onSuccess { list ->
                    beenThereCount = list.count { it.type == "been_there" }
                    wantToGoCount = list.count { it.type == "want_to_go" }
                    userHasBeenThere = list.any { it.type == "been_there" && it.userId == currentUserId }
                    userHasWantToGo = list.any { it.type == "want_to_go" && it.userId == currentUserId }
                }
            }
        }
    }

    LaunchedEffect(pinId) {
        loadReactions()
    }

    val isAuthor = pin?.authorId == currentUserId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pin?.placeName ?: "Pin Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAuthor) {
                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Pin", tint = CoralPrimary)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Pin", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (pin == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pin details not available")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Photo
            if (pin.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = pin.photoUrl,
                    contentDescription = pin.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Edit fields if author clicked Edit
            if (isEditing) {
                OutlinedTextField(
                    value = editCaption,
                    onValueChange = { editCaption = it },
                    label = { Text("Caption") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editVibeTag,
                    onValueChange = { editVibeTag = it },
                    label = { Text("Vibe Tag") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        pinViewModel.updatePin(pin.id, editCaption, editVibeTag, pin.circleId)
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralPrimary)
                ) {
                    Text("Save Changes")
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // Place Name & Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = CoralPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = pin.placeName.ifBlank { "Location" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CoralPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#${pin.vibeTag}",
                            color = CoralPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                if (pin.caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = pin.caption,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Author & Date Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dropped by ${pin.authorName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    val dateFormatted = remember(pin.createdAt) {
                        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(pin.createdAt))
                    }
                    Text(
                        text = dateFormatted,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Reactions Section ("been_there" | "want_to_go")
            Text(
                text = "Reactions",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Been There Reaction
                FilterChip(
                    selected = userHasBeenThere,
                    onClick = {
                        scope.launch {
                            reactionRepo.toggleReaction(pin.id, currentUserId, "been_there")
                            loadReactions()
                        }
                    },
                    label = {
                        Text("Been There ($beenThereCount)", fontWeight = FontWeight.SemiBold)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Want to Go Reaction
                FilterChip(
                    selected = userHasWantToGo,
                    onClick = {
                        scope.launch {
                            reactionRepo.toggleReaction(pin.id, currentUserId, "want_to_go")
                            loadReactions()
                        }
                    },
                    label = {
                        Text("Want to Go ($wantToGoCount)", fontWeight = FontWeight.SemiBold)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PushPin, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CoralPrimary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Confirmation Dialog for Delete Pin
        if (showDeleteDialog) {
            ConfirmationDialog(
                title = "Delete Pin",
                message = "Are you sure you want to delete this pin? This action cannot be undone.",
                confirmText = "Delete",
                onConfirm = {
                    pinViewModel.deletePin(pin.id, pin.circleId) {
                        showDeleteDialog = false
                        onBack()
                    }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}
