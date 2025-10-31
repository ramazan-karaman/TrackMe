package com.example.trackme.ui.tracking

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackme.data.local.RunEntity
import com.example.trackme.data.repository.RunRepository
import com.example.trackme.services.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(private val runRepository: RunRepository, @ApplicationContext private val context: Context) : ViewModel() {
    private val isTrackingFlow= TrackingService.isTracking
    private val pathPointsFlow= TrackingService.pathPoints
    private val timeRunInMillisFlow= TrackingService.timeRunInMillis

    val uiState: StateFlow<TrackingUiState> = combine(
        isTrackingFlow,
        pathPointsFlow,
        timeRunInMillisFlow
    ) { isTracking, path, time ->
        val distance = calculateDistance(path)
        val avgSpeed = calculateAvgSpeed(distance, time)

        TrackingUiState(
            isTracking = isTracking,
            pathPoints = path,
            currentTimeMillis = time,
            distanceInMeters = distance,
            avgSpeedKMH = avgSpeed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TrackingUiState()
    )

    fun toggleTracking(){
        if (uiState.value.isTracking){
            sendCommandToService(TrackingService.ACTION_STOP_SERVICE)
        }else{
            sendCommandToService(TrackingService.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    fun saveRun(){
        viewModelScope.launch {
            val currentState= uiState.value

            if (currentState.distanceInMeters>0){
                val runToSave= RunEntity(
                    id= 0,
                    startTimeMillis = System.currentTimeMillis() - currentState.currentTimeMillis,
                    timeInMillis = currentState.currentTimeMillis,
                    avgSpeedKMH = currentState.avgSpeedKMH,
                    distanceInMeters = currentState.distanceInMeters
                )

                runRepository.insertRun(runToSave)
                sendCommandToService(TrackingService.ACTION_STOP_SERVICE)
            }
        }
    }

    fun sendCommandToService(action: String){
        Intent(context, TrackingService::class.java).also {
            it.action= action
            context.startService(it)}
    }

    private fun calculateDistance(path: List<Location>): Int{
        var totalDistance= 0
        if (path.size>1){
            for (i in 0 until path.size-1){
                val pos1= path[i]
                val pos2= path[i+1]

                val distanceBetween= FloatArray(1)
                Location.distanceBetween(
                    pos1.latitude, pos1.longitude,
                    pos2.latitude, pos2.longitude,
                    distanceBetween
                )
                totalDistance+= distanceBetween[0].toInt()
            }
        }
        return totalDistance
    }

    private fun calculateAvgSpeed(distanceInMeters: Int, timeInMillis: Long): Float {
        if (timeInMillis == 0L || distanceInMeters == 0) return 0f

        val distanceInKm = distanceInMeters / 1000f
        val timeInHours = timeInMillis / 1000f / 60f / 60f

        return distanceInKm / timeInHours
    }
}