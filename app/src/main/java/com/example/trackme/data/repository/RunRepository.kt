package com.example.trackme.data.repository

import com.example.trackme.data.local.RunDao
import com.example.trackme.data.local.RunEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunRepository @Inject constructor(private val runDao: RunDao) {
    fun getAllRunsSortedByDate(): Flow<List<RunEntity>> = runDao.getAllRunsSortedByDate()

    suspend fun insertRun(run: RunEntity)= runDao.insertRun(run)
    suspend fun deleteRun(run: RunEntity)= runDao.deleteRun(run)
}