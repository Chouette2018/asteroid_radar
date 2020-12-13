package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.room.getDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    val databaseAsteroid = getDatabase(application.applicationContext)
    val repository = AsteroidsRepository(databaseAsteroid)
    val asteroids = repository.asteroids


    init{
        viewModelScope.launch{
            repository.refreshAsteroids()
        }
    }
}