package com.example.pinam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.ui.components.PinChip
import com.example.pinam.ui.components.PinamButton
import com.example.pinam.ui.components.PinamTextField
import com.example.pinam.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPinScreen(
    onBack: () -> Unit = {},
    onDropPinSuccess: () -> Unit = {}
) {
    var placeName by remember { mutableStateOf("Rooftop café") }
    var selectedVibeTag by remember { mutableStateOf("Cozy") }
    var caption by remember { mutableStateOf("Best iced coffee in Osu") }

    val vibeTags = listOf("Cozy", "Cheap", "Date spot", "Study spot")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Pin", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.White
            ) {
                PinamButton(
                    text = "Drop Pin",
                    onClick = onDropPinSuccess,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Dashed Border Photo Upload Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryPurpleContainer.copy(alpha = 0.2f))
                    .border(
                        width = 1.5.dp,
                        color = PrimaryPurple,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Take or choose a photo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auto-detected Location Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Auto-detected: Osu, Accra",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextNearBlack
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Change",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPurple,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Place name field
            PinamTextField(
                value = placeName,
                onValueChange = { placeName = it },
                label = "Place name",
                placeholder = "e.g. Rooftop café"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Vibe tag chips
            Text(
                text = "Vibe tag",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextNearBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vibeTags.forEach { tag ->
                    PinChip(
                        tag = tag,
                        isSelected = selectedVibeTag == tag,
                        onClick = { selectedVibeTag = tag }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // One-line caption field
            PinamTextField(
                value = caption,
                onValueChange = { caption = it },
                label = "One-line caption",
                placeholder = "Best iced coffee in Osu"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPinScreenPreview() {
    PinamTheme {
        AddPinScreen()
    }
}
