package com.example.pinam.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.data.PinItem
import com.example.pinam.data.SampleData
import com.example.pinam.ui.components.AvatarStack
import com.example.pinam.ui.components.GradientThumb
import com.example.pinam.ui.components.PinChip
import com.example.pinam.ui.theme.*

@Composable
fun MapScreen(
    circleName: String = "Osu Crew",
    friendCountText: String = "4 friends",
    avatars: List<Pair<String, Color>> = SampleData.sampleCircles[0].memberAvatars,
    recentPins: List<PinItem> = SampleData.sampleRecentPins,
    onAddPinClick: () -> Unit = {},
    onPinDetailClick: (PinItem) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Fake Map Canvas Base
                FakeMapCanvas(modifier = Modifier.fillMaxSize())

                // Top Floating Header Pill ("Osu Crew", "4 friends", Avatars)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = circleName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextNearBlack
                            )
                            Text(
                                text = friendCountText,
                                fontSize = 11.sp,
                                color = TextSecondaryGray
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        AvatarStack(avatars = avatars, avatarSize = 24.dp)
                    }
                }

                // Floating "+ Add pin" button
                Button(
                    onClick = onAddPinClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple, contentColor = Color.White),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp, top = 80.dp)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add pin", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Small Preview Card floating near bottom of map ("Rooftop café")
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp)
                        .width(200.dp)
                        .clickable { onPinDetailClick(SampleData.rooftopCafePin) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GradientThumb(
                            brush = GradientTealBrush,
                            modifier = Modifier.size(44.dp),
                            cornerRadius = 10.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Rooftop café",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextNearBlack
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            PinChip(tag = "Cozy")
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Kofi · 2h ago",
                                fontSize = 10.sp,
                                color = TextSecondaryGray
                            )
                        }
                    }
                }
            }

            // Bottom Section: "Recent pins"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Drag Handle Line
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(BorderLightGray)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Recent pins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextNearBlack
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentPins) { pin ->
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable { onPinDetailClick(pin) },
                                horizontalAlignment = Alignment.Start
                            ) {
                                GradientThumb(
                                    brush = pin.gradientBrush,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    cornerRadius = 14.dp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = pin.placeName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextNearBlack,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Canvas Fake Map
@Composable
fun FakeMapCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background Base: Light Gray-Blue (#E6ECF5)
        drawRect(color = Color(0xFFE6ECF5), size = size)

        // Green Park Blocks
        drawRoundRect(
            color = Color(0xFFD8EED8),
            topLeft = Offset(w * 0.05f, h * 0.12f),
            size = Size(w * 0.32f, h * 0.18f),
            cornerRadius = CornerRadius(20f, 20f)
        )
        drawRoundRect(
            color = Color(0xFFD8EED8),
            topLeft = Offset(w * 0.58f, h * 0.55f),
            size = Size(w * 0.35f, h * 0.22f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )

        // Light Blue Water Patch
        drawRoundRect(
            color = Color(0xFFD0E4F7),
            topLeft = Offset(w * 0.72f, h * 0.08f),
            size = Size(w * 0.25f, h * 0.25f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )

        // White Road Lines
        val strokeWidth = 14f
        val roadColor = Color.White

        // Vertical roads
        drawLine(color = roadColor, start = Offset(w * 0.25f, 0f), end = Offset(w * 0.25f, h), strokeWidth = strokeWidth)
        drawLine(color = roadColor, start = Offset(w * 0.52f, 0f), end = Offset(w * 0.52f, h), strokeWidth = strokeWidth)
        drawLine(color = roadColor, start = Offset(w * 0.80f, 0f), end = Offset(w * 0.80f, h), strokeWidth = strokeWidth)

        // Horizontal & Diagonal roads
        drawLine(color = roadColor, start = Offset(0f, h * 0.30f), end = Offset(w, h * 0.30f), strokeWidth = strokeWidth)
        drawLine(color = roadColor, start = Offset(0f, h * 0.62f), end = Offset(w, h * 0.62f), strokeWidth = strokeWidth)
        drawLine(color = roadColor, start = Offset(0f, h * 0.15f), end = Offset(w * 0.52f, h * 0.45f), strokeWidth = strokeWidth)

        // Map Teardrop Pins (Teal, Orange, Pink, Purple)
        drawCircle(color = GradientTealStart, radius = 22f, center = Offset(w * 0.65f, h * 0.18f))
        drawCircle(color = GradientOrangeStart, radius = 22f, center = Offset(w * 0.52f, h * 0.28f))
        drawCircle(color = GradientPinkStart, radius = 22f, center = Offset(w * 0.80f, h * 0.35f))
        drawCircle(color = GradientPurpleStart, radius = 22f, center = Offset(w * 0.52f, h * 0.48f))
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    PinamTheme {
        MapScreen()
    }
}
