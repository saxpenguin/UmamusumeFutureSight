package com.saxpenguin.umamusumefuturesight.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.saxpenguin.umamusumefuturesight.model.BannerType

@Composable
fun Badge(type: BannerType) {
    val (color, text) = when (type) {
        BannerType.CHARACTER -> MaterialTheme.colorScheme.secondary to "馬娘"
        BannerType.SUPPORT_CARD -> MaterialTheme.colorScheme.tertiary to "支援卡"
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}
