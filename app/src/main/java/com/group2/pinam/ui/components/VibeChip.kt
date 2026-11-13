package com.group2.pinam.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Coloured tag chip from the mockups: Cozy, Cheap, Date spot, Study spot. */
@Composable
fun VibeChip(vibe: String, modifier: Modifier = Modifier) {
    val (bg, fg) = when (vibe) {
        "Cheap" -> Color(0xFFD9F2DF) to Color(0xFF1E6B34)
        "Date spot" -> Color(0xFFFFDDE8) to Color(0xFF9A1F4B)
        "Study spot" -> Color(0xFFDCE8FF) to Color(0xFF1D4A9E)
        else -> Color(0xFFFFE6D1) to Color(0xFF8A3B00)
    }
    Surface(shape = RoundedCornerShape(12.dp), color = bg, modifier = modifier) {
        Text(
            vibe, color = fg, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
