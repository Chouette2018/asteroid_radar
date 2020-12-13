package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.room.getDatabase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    val databaseAsteroid = getDatabase(application.applicationContext)
    val repository = AsteroidsRepository(databaseAsteroid)
    val asteroids = repository.asteroids
    val imageOfTheDay = repository.imageOfDay


    init{
        viewModelScope.launch{
            repository.refreshAsteroids()
            repository.getImageOfTheDay()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    /* navigate to  asteroid details*/
    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetails :LiveData<Asteroid>
        get() = _navigateToAsteroidDetails

    fun onAsteroidClicked(asteroid:Asteroid){
        _navigateToAsteroidDetails.value = asteroid
    }

    fun navigateToAsteroidDetailsDone(){
        _navigateToAsteroidDetails.value = null
    }
}