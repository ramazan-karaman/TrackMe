package com.example.trackme.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trackme.util.formatDistance
import com.example.trackme.util.formatDuration
import com.example.trackme.util.formatSpeed



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreenContent(
    uiState: TrackingUiState,
    onToggleTracking: () -> Unit,
    onSaveRun: () -> Unit
){
    Scaffold(
        topBar = { TopAppBar(title = { Text("Yeni Koşu") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatDuration(uiState.currentTimeMillis),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoText("Mesafe", formatDistance(uiState.distanceInMeters))
                InfoText("Hız", formatSpeed(uiState.avgSpeedKMH))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onToggleTracking) {
                    Text(text = if (uiState.isTracking) "Durdur" else "Başlat")
                }

                if (!uiState.isTracking && uiState.distanceInMeters > 0) {
                    Button(onClick = onSaveRun) {
                        Text(text = "Koşuyu Kaydet")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoText(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.titleLarge)
    }
}