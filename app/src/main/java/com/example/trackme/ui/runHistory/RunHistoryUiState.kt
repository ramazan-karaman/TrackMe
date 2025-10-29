package com.example.trackme.ui.runHistory

import com.example.trackme.data.local.RunEntity

data class RunHistoryUiState(
    val runs: List<RunEntity> = emptyList()
)
