package com.example.pinam.ui.screens.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
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
import com.example.pinam.data.model.Pin
import com.example.pinam.ui.components.EmptyStateView
import com.example.pinam.ui.components.LoadingOverlay
import com.example.pinam.ui.theme.CoralPrimary
import com.example.pinam.viewmodel.CircleViewModel
import com.example.pinam.viewmodel.PinViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleMapScreen(
    circleId: String,
    pinViewModel: PinViewModel,
    circleViewModel: CircleViewModel,
    onPinClick: (String) -> Unit,
    onDropPinClick: (String) -> Unit
) {
    val pinsState by pinViewModel.pinsState.collectAsState()
    val circleState by circleViewModel.uiState.collectAsState()
    val activeCircle = circleState.circles.find { it.id == circleId } ?: circleState.activeCircle

    var isMapView by remember { mutableStateOf(true) }

    LaunchedEffect(circleId) {
        if (circleId.isNotBlank()) {
            pinViewModel.loadPinsForCircle(circleId)
        }
    }

    // Default camera center
    val defaultLatLng = if (pinsState.pins.isNotEmpty()) {
        LatLng(pinsState.pins.first().lat, pinsState.pins.first().lng)
    } else {
        LatLng(37.7749, -122.4194)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 12f)
    }

    LaunchedEffect(pinsState.pins) {
        if (pinsState.pins.isNotEmpty()) {
            val first = pinsState.pins.first()
            if (first.lat != 0.0 || first.lng != 0.0) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(first.lat, first.lng), 13f)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(activeCircle?.name ?: "Circle Map", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = "${pinsState.pins.size} pins dropped",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    // Toggle View Mode (Map / Feed)
                    IconButton(onClick = { isMapView = !isMapView }) {
                        Icon(
                            if (isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                            contentDescription = "Toggle View",
                            tint = CoralPrimary
                        )
                    }
                    IconButton(onClick = { onDropPinClick(circleId) }) {
                        Icon(Icons.Default.Add, contentDescription = "Drop Pin", tint = CoralPrimary)
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
            if (pinsState.isLoading && pinsState.pins.isEmpty()) {
                LoadingOverlay(message = "Loading circle map...")
            } else if (pinsState.pins.isEmpty()) {
                EmptyStateView(
                    message = "No pins dropped in ${activeCircle?.name ?: "this circle"} yet! Be the first to drop a pin.",
                    buttonText = "Drop First Pin 📍",
                    onButtonClick = { onDropPinClick(circleId) }
                )
            } else if (isMapView) {
                // Map View
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    pinsState.pins.forEach { pin ->
                        if (pin.lat != 0.0 || pin.lng != 0.0) {
                            val markerState = remember(pin.id, pin.lat, pin.lng) {
                                MarkerState(position = LatLng(pin.lat, pin.lng))
                            }
                            Marker(
                                state = markerState,
                                title = pin.placeName.ifBlank { "Pin" },
                                snippet = "${pin.authorName}: ${pin.vibeTag}",
                                onClick = {
                                    onPinClick(pin.id)
                                    true
                                }
                            )
                        }
                    }
                }
            } else {
                // Feed List View
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pinsState.pins) { pin ->
                        PinFeedCard(
                            pin = pin,
                            onClick = { onPinClick(pin.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinFeedCard(
    pin: Pin,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (pin.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = pin.photoUrl,
                    contentDescription = pin.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = CoralPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pin.placeName.ifBlank { "Location" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CoralPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "#${pin.vibeTag}",
                            color = CoralPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                if (pin.caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pin.caption,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dropped by ${pin.authorName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
