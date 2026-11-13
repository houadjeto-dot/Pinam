package com.group2.pinam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.ui.components.ScreenScaffold
import com.group2.pinam.ui.components.VibeChip

// OWNER: Pair 2 (Map). Replace the list with a GoogleMap + markers (maps-compose is already a dependency).
@Composable
fun CircleMapScreen(
    circleId: String,
    onBack: () -> Unit,
    onAddPin: () -> Unit,
    onOpenPin: (String) -> Unit,
    onRecommendations: () -> Unit
) {
    val flow = remember(circleId) { ServiceLocator.pins.observePins(circleId) }
    val pins by flow.collectAsState(initial = emptyList())
    ScreenScaffold("Circle map (placeholder)", onBack) { pad ->
        Column(Modifier.padding(pad)) {
            Row(Modifier.padding(16.dp)) {
                Button(onClick = onAddPin) { Text("Add pin") }
                Button(onClick = onRecommendations, modifier = Modifier.padding(start = 8.dp)) { Text("Recommendations") }
            }
            LazyColumn {
                items(pins, key = { it.id }) { p ->
                    ListItem(
                        headlineContent = { Text(p.placeName) },
                        supportingContent = { VibeChip(p.vibe) },
                        trailingContent = { Text(p.authorName) },
                        modifier = Modifier.fillMaxWidth().clickable { onOpenPin(p.id) }
                    )
                }
            }
        }
    }
}
