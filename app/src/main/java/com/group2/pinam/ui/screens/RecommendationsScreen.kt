package com.group2.pinam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.group2.pinam.domain.Recommendation
import com.group2.pinam.model.Pin
import com.group2.pinam.ui.components.ScreenScaffold
import com.group2.pinam.ui.components.VibeChip

private val PrimaryPurple = Color(0xFF6C3CD9)

@Composable
fun RecommendationsScreen(
    circleId: String,
    onBack: () -> Unit,
    vm: RecommendationsViewModel = viewModel(factory = RecommendationsViewModel.Factory(circleId))
) {
    val currentUser by vm.currentUser.collectAsState()
    val friendsPicks by vm.friendsPicks.collectAsState()
    val wishlist by vm.wishlist.collectAsState()
    val pins by vm.pins.collectAsState()

    ScreenScaffold("Discover", onBack) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .background(Color.White)
        ) {
            TabRow(
                selectedTabIndex = vm.selectedTabIndex,
                contentColor = PrimaryPurple
            ) {
                Tab(
                    selected = vm.selectedTabIndex == 0,
                    onClick = { vm.selectTab(0) },
                    text = { Text("Friends' picks", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = vm.selectedTabIndex == 1,
                    onClick = { vm.selectTab(1) },
                    text = { Text("My wishlist", fontWeight = FontWeight.SemiBold) }
                )
            }

            if (vm.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            } else if (vm.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = vm.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                when (vm.selectedTabIndex) {
                    0 -> FriendsPicksTab(
                        recommendations = friendsPicks,
                        pins = pins,
                        currentUserId = currentUser?.id ?: "",
                        onToggleWantToGoForRec = { rec -> vm.onToggleWantToGoForRecommendation(rec) }
                    )
                    1 -> WishlistTab(
                        wishlistPins = wishlist,
                        onToggleWantToGo = { pin -> vm.onToggleWantToGo(pin) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FriendsPicksTab(
    recommendations: List<Recommendation>,
    pins: List<Pin>,
    currentUserId: String,
    onToggleWantToGoForRec: (Recommendation) -> Unit
) {
    if (recommendations.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No recommendations yet. Explore pins from your friends!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recommendations, key = { it.placeName }) { rec ->
                val matchingPins = pins.filter { it.placeName.trim().equals(rec.placeName.trim(), ignoreCase = true) }
                val isBookmarked = matchingPins.any { currentUserId in it.wantToGo }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            if (rec.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = rec.photoUrl,
                                    contentDescription = rec.placeName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PrimaryPurple.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rec.placeName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    rec.vibes.forEach { vibe ->
                                        VibeChip(vibe = vibe)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "${rec.friendCount} of your friends loved this",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { onToggleWantToGoForRec(rec) }) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = PrimaryPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WishlistTab(
    wishlistPins: List<Pin>,
    onToggleWantToGo: (Pin) -> Unit
) {
    if (wishlistPins.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your wishlist is empty. Bookmark places you want to visit!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(wishlistPins, key = { it.id }) { pin ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            if (pin.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = pin.photoUrl,
                                    contentDescription = pin.placeName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PrimaryPurple.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = PrimaryPurple,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pin.placeName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (pin.vibe.isNotBlank()) {
                                    VibeChip(vibe = pin.vibe)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val friendCount = (pin.beenThere + pin.reactions.keys + listOf(pin.authorId)).distinct().filter { it.isNotBlank() }.size
                                Text(
                                    text = if (friendCount > 0) "$friendCount of your friends loved this" else "Saved to your wishlist",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { onToggleWantToGo(pin) }) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Remove from wishlist",
                                    tint = PrimaryPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
