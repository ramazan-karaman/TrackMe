package com.example.trackme.ui.runHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackme.data.local.RunEntity
import com.example.trackme.data.repository.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunHistoryViewModel  @Inject constructor(private val runRepository: RunRepository): ViewModel() {
    val uiState: StateFlow<RunHistoryUiState> = runRepository.getAllRunsSortedByDate()
        .map { runList ->
            RunHistoryUiState(runs = runList)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = RunHistoryUiState()
        )

    fun deleteRun(run: RunEntity){
        viewModelScope.launch {
            runRepository.deleteRun(run)
        }
    }
}