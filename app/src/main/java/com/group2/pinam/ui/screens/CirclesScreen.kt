package com.group2.pinam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.group2.pinam.ui.components.ScreenScaffold

// OWNER: Pair 1 (Auth + Circles). Replace with the real Circles list + create/join.
@Composable
fun CirclesScreen(onOpenCircle: (String) -> Unit) {
    val flow = remember { ServiceLocator.circles.observeMyCircles(FakeData.ME) }
    val circles by flow.collectAsState(initial = emptyList())
    ScreenScaffold("Your circles") { pad ->
        LazyColumn(Modifier.padding(pad)) {
            items(circles, key = { it.id }) { c ->
                ListItem(
                    headlineContent = { Text(c.name) },
                    supportingContent = { Text("${c.memberIds.size} members") },
                    modifier = Modifier.fillMaxWidth().clickable { onOpenCircle(c.id) }
                )
            }
        }
    }
}
