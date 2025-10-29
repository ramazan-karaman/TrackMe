package com.example.trackme.ui.runHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trackme.data.local.RunEntity
import com.example.trackme.util.formatDistance
import com.example.trackme.util.formatDuration
import com.example.trackme.util.formatSpeed
import com.example.trackme.util.formatTimestamp

@Composable
fun RunItem(run: RunEntity){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formatTimestamp(run.startTimeMillis),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Süre", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = formatDuration(run.timeInMillis),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Mesafe", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = formatDistance(run.distanceInMeters),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Hız", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = formatSpeed(run.avgSpeedKMH),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}