package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group2.pinam.model.Pin
import com.group2.pinam.ui.components.ScreenScaffold
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// This screen displays all the pins on a Google Map.
@Composable
fun CircleMapScreen(
    circleId: String,
    onBack: () -> Unit,
    onAddPin: () -> Unit,
    onOpenPin: (String) -> Unit,
    onRecommendations: () -> Unit,
    mapViewModel: CircleMapViewModel = viewModel()
) {

    // Gets the ViewModel that manages our map pins.
    val pins by mapViewModel.pins.collectAsState()

    // Loads pins whenever the selected circle changes.
    LaunchedEffect(circleId) {
        mapViewModel.loadPins(circleId)
    }

    // Stores the ID of the pin selected by the user.
    var selectedPinId by remember {
        mutableStateOf<String?>(null)
    }

    // Finds the pin that the user selected.
    val selectedPin = pins.firstOrNull {
        it.id == selectedPinId
    }

    // Sets the starting position of the map.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(5.5560, -0.1820),
            14f
        )
    }

    // Creates the main screen layout.
    ScreenScaffold("Circle Map", onBack) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Shows the screen buttons.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Button(onClick = onAddPin) {
                    Text("Add Pin")
                }

                Button(
                    onClick = onRecommendations,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Recommendations")
                }
            }

            // Displays the Google Map.
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),

                cameraPositionState = cameraPositionState
            ) {

                // Creates a marker for every pin.
                pins.forEach { pin ->

                    Marker(
                        state = MarkerState(
                            position = LatLng(pin.lat, pin.lng)
                        ),

                        title = pin.placeName,
                        snippet = pin.vibe,

                        // Selects the pin when its marker is tapped.
                        onClick = {
                            selectedPinId = pin.id
                            true
                        }
                    )
                }
            }

            // Shows the selected pin below the map.
            if (selectedPin != null) {
                PinPreview(
                    pin = selectedPin,
                    onOpenPin = onOpenPin
                )
            }
        }
    }
}

// This displays a small preview of the selected pin.
@Composable
fun PinPreview(
    pin: Pin,
    onOpenPin: (String) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Shows the location name.
            Text(text = pin.placeName)

            // Shows the type of place.
            Text(text = "Vibe: ${pin.vibe}")

            // Shows who added the location.
            Text(text = "Added by: ${pin.authorName}")

            // Shows the location description.
            Text(text = pin.caption)

            // Opens the full pin details.
            Button(
                onClick = {
                    onOpenPin(pin.id)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("View Pin Details")
            }
        }
    }
}