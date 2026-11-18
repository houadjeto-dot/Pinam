package com.example.pinam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.data.PinItem
import com.example.pinam.data.SampleData
import com.example.pinam.ui.components.AvatarStack
import com.example.pinam.ui.components.PinChip
import com.example.pinam.ui.components.PinamButton
import com.example.pinam.ui.theme.*

@Composable
fun PinDetailScreen(
    pin: PinItem = SampleData.rooftopCafePin,
    onBack: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    var hasBeenThere by remember { mutableStateOf(true) }
    var hasWantToGo by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image Placeholder Box (Teal Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(GradientTealBrush)
            ) {
                // Centered Image Icon Placeholder
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )

                // Circular Back Button
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .size(36.dp)
                        .align(Alignment.TopStart)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextNearBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Circular Options Button
                Surface(
                    onClick = { },
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .size(36.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = TextNearBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // White Detail Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Place Title & Vibe Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pin.placeName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextNearBlack,
                            modifier = Modifier.weight(1f)
                        )
                        PinChip(tag = pin.vibeTag)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Author Avatar & Time
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarStack(
                            avatars = listOf("A" to AvatarOrange),
                            avatarSize = 28.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pin.authorAndDate,
                            fontSize = 13.sp,
                            color = TextSecondaryGray,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Caption
                    Text(
                        text = pin.caption,
                        fontSize = 15.sp,
                        color = TextNearBlack
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Reaction Chips (♡ 12, ☆ 5, 😊 3)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReactionPill(text = "♡ ${pin.heartCount}")
                        ReactionPill(text = "☆ ${pin.starCount}")
                        ReactionPill(text = "😊 ${pin.smileyCount}")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons: "Been there" & "Want to go"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PinamButton(
                            text = "Been there",
                            onClick = { hasBeenThere = !hasBeenThere },
                            isOutlined = !hasBeenThere,
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                        PinamButton(
                            text = "Want to go",
                            onClick = { hasWantToGo = !hasWantToGo },
                            isOutlined = !hasWantToGo,
                            leadingIcon = { Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Edit pin & Delete buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PinamButton(
                            text = "Edit pin",
                            onClick = onEditClick,
                            isOutlined = true,
                            modifier = Modifier.weight(1f)
                        )
                        PinamButton(
                            text = "Delete",
                            onClick = onDeleteClick,
                            isOutlined = true,
                            isDestructive = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionPill(text: String) {
    Surface(
        shape = CircleShape,
        color = SurfaceTintLavender
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextNearBlack,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PinDetailScreenPreview() {
    PinamTheme {
        PinDetailScreen()
    }
}
