package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HazardCrimson
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MetricCompareRow(
    label: String,
    optimizedValue: String,
    baselineValue: String,
    isOptimizedFavorable: Boolean,
    deltaText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optimized Value
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = optimizedValue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isOptimizedFavorable) SafeEmerald else TextPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Optimized",
                    style = MaterialTheme.typography.labelSmall,
                    color = SafeEmerald.copy(alpha = 0.8f),
                    fontSize = 9.sp
                )
            }

            // Delta Indicator
            if (deltaText != null) {
                Text(
                    text = deltaText,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    color = if (isOptimizedFavorable) SafeEmerald else HazardCrimson,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Baseline Value
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = baselineValue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isOptimizedFavorable) HazardCrimson else TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Shortest Baseline",
                    style = MaterialTheme.typography.labelSmall,
                    color = HazardCrimson.copy(alpha = 0.8f),
                    fontSize = 9.sp
                )
            }
        }
    }
}
