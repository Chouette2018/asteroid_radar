package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.room.DatabaseAsteroid

@JsonClass(generateAdapter = true)
data class NasaImageOfTheDayResponse(

    val url:String,
    @Json(name="media_type")
    val mediaType:String,

    val title:String
)

fun List<Asteroid>.asDatabaseModel(): List<DatabaseAsteroid> {
        return map{
            DatabaseAsteroid(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous
            )
        }
    }