package com.example.pinam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pinam.ui.theme.*

// Pill-shaped primary button or outlined button
@Composable
fun PinamButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false,
    isDestructive: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = CircleShape
    val primaryColor = if (isDestructive) DestructiveRed else PrimaryPurple

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = shape,
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(primaryColor, primaryColor))),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            )
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// Vibe tag chip with exact preset colors
@Composable
fun PinChip(
    tag: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (bg, textColor) = when (tag.lowercase()) {
        "cozy" -> CozyBg to CozyText
        "cheap" -> CheapBg to CheapText
        "study spot" -> StudySpotBg to StudySpotText
        "date spot" -> DateSpotBg to DateSpotText
        else -> CozyBg to CozyText
    }

    val finalBg = if (isSelected) PrimaryPurple else bg
    val finalTextColor = if (isSelected) Color.White else textColor

    Surface(
        shape = CircleShape,
        color = finalBg,
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Text(
            text = tag,
            color = finalTextColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// Overlapping colored member avatars
@Composable
fun AvatarStack(
    avatars: List<Pair<String, Color>>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 26.dp,
    overlapOffset: Dp = 10.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        avatars.forEachIndexed { index, (initial, color) ->
            Box(
                modifier = Modifier
                    .offset(x = if (index == 0) 0.dp else (-overlapOffset * index))
                    .size(avatarSize)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (avatarSize.value * 0.45f).sp
                )
            }
        }
    }
}

// Gradient Thumbnail Box
@Composable
fun GradientThumb(
    brush: Brush,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

// Input text field with 12dp rounded corners
@Composable
fun PinamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextNearBlack,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder != null) {
                    Text(text = placeholder, color = TextSecondaryGray, fontSize = 14.sp)
                }
            },
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPurple,
                unfocusedBorderColor = BorderLightGray
            )
        )
    }
}
