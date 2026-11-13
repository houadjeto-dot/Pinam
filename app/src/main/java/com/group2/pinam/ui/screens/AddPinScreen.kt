package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.data.fake.FakeData
import com.group2.pinam.model.Pin
import com.group2.pinam.ui.components.ScreenScaffold
import kotlinx.coroutines.launch

// OWNER: Pair 3 (Add Pin). Replace with photo picker, GPS location, place name, vibe chips, caption.
@Composable
fun AddPinScreen(circleId: String, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    ScreenScaffold("Add Pin (placeholder)", onDone) { pad ->
        Column(Modifier.padding(pad).padding(24.dp)) {
            Button(onClick = {
                scope.launch {
                    ServiceLocator.pins.addPin(
                        Pin(circleId = circleId, authorId = FakeData.ME, authorName = "Ama",
                            lat = 5.556, lng = -0.182, placeName = "Test spot", vibe = "Cozy", caption = "Fake pin"),
                        photoUri = null
                    )
                    onDone()
                }
            }) { Text("Drop fake pin") }
        }
    }
}
