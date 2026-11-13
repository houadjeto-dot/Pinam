package com.group2.pinam.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.data.fake.FakeData
import com.group2.pinam.ui.components.ScreenScaffold
import com.group2.pinam.ui.components.VibeChip
import kotlinx.coroutines.launch

// OWNER: Pair 4 (Pin detail, reactions, edit/delete). Replace with the real detail UI.
@Composable
fun PinDetailScreen(circleId: String, pinId: String, onBack: () -> Unit) {
    val flow = remember(circleId, pinId) { ServiceLocator.pins.observePin(circleId, pinId) }
    val pin by flow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    ScreenScaffold("Pin detail (placeholder)", onBack) { pad ->
        val p = pin
        Column(Modifier.padding(pad).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (p == null) {
                Text("Pin not found")
            } else {
                Text(p.placeName, style = MaterialTheme.typography.headlineSmall)
                VibeChip(p.vibe)
                Text("${p.authorName}: ${p.caption}")
                val been = FakeData.ME in p.beenThere
                val want = FakeData.ME in p.wantToGo
                val toggleBeen = { scope.launch { ServiceLocator.pins.toggleBeenThere(circleId, pinId, FakeData.ME); Unit }; Unit }
                val toggleWant = { scope.launch { ServiceLocator.pins.toggleWantToGo(circleId, pinId, FakeData.ME); Unit }; Unit }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (been) Button(onClick = toggleBeen) { Text("Been there") }
                    else OutlinedButton(onClick = toggleBeen) { Text("Been there") }
                    if (want) Button(onClick = toggleWant) { Text("Want to go") }
                    else OutlinedButton(onClick = toggleWant) { Text("Want to go") }
                }
            }
        }
    }
}
