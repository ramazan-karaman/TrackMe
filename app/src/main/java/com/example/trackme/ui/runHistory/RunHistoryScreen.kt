package com.example.trackme.ui.runHistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunHistoryScreen(onNavigateToTracking: () -> Unit){
    val viewModel: RunHistoryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Koşu Geçmişi")},
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTracking) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yeni Koşu Başlat"
                )
            }
        }
    ) {paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if (uiState.runs.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text("Henüz koşu kaydedilmemiş.")
                }
            }else{
                LazyColumn(Modifier.fillMaxSize().padding(paddingValues)) {
                    items(uiState.runs, key= { it.id }){run->
                        RunItem(run= run)
                    }
                }
            }
        }
    }
}