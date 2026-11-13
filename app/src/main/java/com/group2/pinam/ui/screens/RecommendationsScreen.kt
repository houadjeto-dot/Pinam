package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.data.fake.FakeData
import com.group2.pinam.domain.buildRecommendations
import com.group2.pinam.ui.components.ScreenScaffold
import com.group2.pinam.ui.components.VibeChip

// OWNER: Pair 5 (Feed + Recommendations). Replace with tabs (Friends' picks / My wishlist) and cards.
@Composable
fun RecommendationsScreen(circleId: String, onBack: () -> Unit) {
    val flow = remember(circleId) { ServiceLocator.pins.observePins(circleId) }
    val pins by flow.collectAsState(initial = emptyList())
    val recs = remember(pins) { buildRecommendations(pins, FakeData.ME) }
    ScreenScaffold("Discover (placeholder)", onBack) { pad ->
        LazyColumn(Modifier.padding(pad)) {
            items(recs, key = { it.placeName }) { r ->
                ListItem(
                    headlineContent = { Text(r.placeName) },
                    supportingContent = { Text("${r.friendCount} of your friends loved this") },
                    trailingContent = { r.vibes.firstOrNull()?.let { VibeChip(it) } }
                )
            }
        }
    }
}
