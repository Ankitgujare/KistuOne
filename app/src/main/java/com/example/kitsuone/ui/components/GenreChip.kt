package com.example.kitsuone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitsuone.ui.theme.AccentRed
import com.example.kitsuone.ui.theme.CornerRadius
import com.example.kitsuone.ui.theme.DarkBrownLight
import com.example.kitsuone.ui.theme.TextWhite

@Composable
fun GenreChip(
    genre: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) AccentRed else DarkBrownLight
    val textColor = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.7f)
    val borderColor = if (isSelected) AccentRed else DarkBrownLight.copy(alpha = 0.5f)
    
    Text(
        text = genre,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        ),
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.extraLarge))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(CornerRadius.extraLarge)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
