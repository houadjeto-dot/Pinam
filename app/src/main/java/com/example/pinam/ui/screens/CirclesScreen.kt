package com.example.pinam.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.pinam.data.CircleItem
import com.example.pinam.data.SampleData
import com.example.pinam.ui.components.AvatarStack
import com.example.pinam.ui.components.GradientThumb
import com.example.pinam.ui.components.PinamButton
import com.example.pinam.ui.theme.PinamTheme
import com.example.pinam.ui.theme.SurfaceTintLavender
import com.example.pinam.ui.theme.TextNearBlack
import com.example.pinam.ui.theme.TextSecondaryGray

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CirclesScreen(
    circles: List<CircleItem> = SampleData.sampleCircles,
    onCircleClick: (CircleItem) -> Unit = {},
    onCreateClick: () -> Unit = {},
    onJoinClick: () -> Unit = {},
    onLeaveCircle: ((CircleItem) -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "Your circles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextNearBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(circles) { circle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onCircleClick(circle) },
                                onLongClick = { onLeaveCircle?.invoke(circle) }
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceTintLavender)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GradientThumb(
                                brush = circle.gradientBrush,
                                modifier = Modifier.size(52.dp),
                                cornerRadius = 12.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = circle.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextNearBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = circle.pinCountText,
                                    fontSize = 13.sp,
                                    color = TextSecondaryGray
                                )
                            }
                            AvatarStack(avatars = circle.memberAvatars)
                            if (onLeaveCircle != null) {
                                IconButton(
                                    onClick = { onLeaveCircle(circle) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Circle Options",
                                        tint = TextSecondaryGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PinamButton(
                    text = "Join with code",
                    onClick = onJoinClick,
                    isOutlined = true,
                    modifier = Modifier.weight(1f)
                )
                PinamButton(
                    text = "+ Create",
                    onClick = onCreateClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CirclesScreenPreview() {
    PinamTheme {
        CirclesScreen()
    }
}
