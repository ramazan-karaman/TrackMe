package com.example.trackme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trackme.ui.runHistory.RunHistoryScreen
import com.example.trackme.ui.theme.TrackMeTheme
import com.example.trackme.ui.tracking.Screen
import com.example.trackme.ui.tracking.TrackingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrackMeTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    val navController= rememberNavController()

                    NavHost(
                        navController= navController,
                        startDestination= Screen.RunHistory
                    ){
                        composable(route= Screen.RunHistory){
                            RunHistoryScreen(onNavigateToTracking = {
                                navController.navigate(Screen.Tracking)
                            }
                            )
                        }

                        composable(route= Screen.Tracking){
                            TrackingScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}