package com.udacity.asteroidradar.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Filter
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.room.AsteroidsDatabase
import com.udacity.asteroidradar.room.asDomainModel
import com.udacity.asteroidradar.room.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AsteroidsRepository(private val database :AsteroidsDatabase) {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids : LiveData<List<Asteroid>>
        get() = _asteroids

    private val _imageOfTheDay:MutableLiveData<String> = MutableLiveData<String>()
    val imageOfDay : LiveData<String>
        get() = _imageOfTheDay

    suspend fun refreshAsteroids():List<Asteroid>{
        return withContext(Dispatchers.IO){
            val asteroidsList = Network.fetchAsteroids()
            database.asteroidDao.insertAll(asteroidsList.asDatabaseModel())
            database.asteroidDao.getSavedAsteroids().asDomainModel()
        }
    }

    suspend fun getImageOfTheDay(){
        withContext(Dispatchers.IO){
            _imageOfTheDay.postValue(Network.getImageOfTheDay())
        }
    }

    suspend fun removeObsolete(date:String) {
        withContext(Dispatchers.IO){
            database.asteroidDao.removeObsolete(date)
        }
    }

    suspend fun getAsteroids(showSaved: Filter){
        withContext(Dispatchers.IO) {
            _asteroids.postValue(
                when (showSaved) {
                    Filter.SHOW_ALL -> refreshAsteroids()
                    Filter.SHOW_SAVED -> database.asteroidDao.getSavedAsteroids().asDomainModel()
                    else -> {
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)
                        val date = current.format(formatter)
                        database.asteroidDao.getTodayAsteroids(date).asDomainModel()
                    }
                }
            )
        }
    }

    companion object{
        fun getNewRepository(context: Context):AsteroidsRepository{
            val databaseAsteroid = getDatabase(context)
            return AsteroidsRepository(databaseAsteroid)
        }
    }
}