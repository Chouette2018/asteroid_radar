package com.udacity.asteroidradar.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FetchWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val repository = AsteroidsRepository.getNewRepository(applicationContext)

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
           val dateFormat: SimpleDateFormat = SimpleDateFormat(API_QUERY_DATE_FORMAT)
            val date = dateFormat.format(yesterday())
            repository.removeObsolete(date)
            repository.refreshAsteroids()
            Result.success()
        }

    private fun yesterday(): Date? {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return cal.getTime()
    }
}