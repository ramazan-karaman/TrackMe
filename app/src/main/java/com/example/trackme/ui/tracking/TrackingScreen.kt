package com.example.trackme.ui.tracking

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrackingViewModel= hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    when {
        locationPermissionState.status.isGranted -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrackingScreenContent(
                    uiState = uiState,
                    onToggleTracking = { viewModel.toggleTracking() },
                    onSaveRun = {
                        viewModel.saveRun()
                        onNavigateBack()
                    }
                )
            }
        }

        locationPermissionState.status.shouldShowRationale -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Konum takibi için bu izne ihtiyacımız var.")
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("İzin İste")
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bu özellik için konum izni gerekiyor.")
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("İzin Ver")
                }
            }
        }
    }
}