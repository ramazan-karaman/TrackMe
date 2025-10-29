package com.example.trackme.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.example.trackme.MainActivity
import com.example.trackme.R
import com.example.trackme.util.formatDuration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TrackingService: LifecycleService() {

    private var timerJob: Job? = null
    private var lapStartTime = 0L
    private var timeRun = 0L

    companion object{
        const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
        const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Konum Takibi"
        const val NOTIFICATION_ID = 1

        val isTracking= MutableStateFlow(false)
        val pathPoints= MutableStateFlow<List<Location>> (emptyList())
        val timeRunInMillis= MutableStateFlow(0L)

    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        Log.d("TrackingService", "onCreate called")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("TrackingService", "onStartCommand called")

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (!isTracking.value){
                        Log.d("TrackingService", "Started or Resumed service")
                        isTracking.value= true
                        pathPoints.value= emptyList()
                        timeRunInMillis.value= 0L
                        startForegroundService()
                        startLocationUpdates()
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("TrackingService", "Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("TrackingService", "Stopped service")
                    stopService()
                }
            }
        }
        return START_STICKY
    }

    private fun stopService() {
        Log.d("TrackingService", "Stopping service...")
        isTracking.value = false
        timerJob?.cancel()
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } catch (unlikely: SecurityException) {
            Log.e("TrackingService", "Konum izni hatası (durdurulamadı): ${unlikely.message}")
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundService() {
        Log.d("TrackingService", "Starting foreground service...")

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Konum Takibi Aktif")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        lapStartTime = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value) {
                val timeElapsed = System.currentTimeMillis() - lapStartTime
                timeRunInMillis.value = timeRun + timeElapsed

                val formattedTime = formatDuration(timeRunInMillis.value)

                updateNotification(formattedTime)
                delay(1000L)
            }
        }
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = Intent.ACTION_MAIN
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    override fun onDestroy() {
        timerJob?.cancel()
        isTracking.value= false
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            Log.d("TrackingService", "Konum güncellemeleri durduruldu.")
        } catch (unlikely: SecurityException) {
            Log.e("TrackingService", "Konum izni hatası (güncellemeler durdurulamadı): ${unlikely.message}")
        }
        super.onDestroy()
        Log.d("TrackingService", "onDestroy called")
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value){
                result.locations.let { locations ->
                    for (location in locations) {
                        Log.d("TrackingService", "Yeni Konum: ${location.latitude}, ${location.longitude}")
                        addPathPoint(location)
                    }
                }
            }

        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            pathPoints.value = pathPoints.value + listOf(it)
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("TrackingService", "Konum izni verilmemiş!")
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d("TrackingService", "Konum güncellemeleri başlatıldı.")
        } catch (unlikely: SecurityException) {
            Log.e("TrackingService", "Konum izni hatası (güncellemeler başlatılamadı): ${unlikely.message}")
        }
    }

    private fun updateNotification(formattedTime: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Konum Takibi Aktif")
            .setContentText(formattedTime)
            .setContentIntent(getMainActivityPendingIntent())

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}