package com.example.trackme.ui.tracking

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel= hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    )
    when {
        permissionsState.allPermissionsGranted -> {
            TrackingScreenContent(
                uiState = uiState,
                onToggleTracking = { viewModel.toggleTracking() },
                onSaveRun = {
                    viewModel.saveRun()
                    onNavigateBack()
                }
            )
        }

        permissionsState.shouldShowRationale -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bu özelliğin çalışması için hem Konum hem de Bildirim izinleri gereklidir.",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Konum, rotanızı kaydetmek içindir. Bildirim, servis arka planda çalışırken sizi bilgilendirmek içindir.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("İzinleri İste")
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Lütfen konum ve bildirim izinlerini verin.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("İzinleri Ver")
                }
            }
        }
    }
}