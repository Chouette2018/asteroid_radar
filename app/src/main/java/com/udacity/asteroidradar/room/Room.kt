package com.udacity.asteroidradar.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao{
    @Query("SELECT * FROM databaseasteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid")
    suspend fun getSavedAsteroids(): List<DatabaseAsteroid>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :date")
    suspend fun getTodayAsteroids(date:String): List<DatabaseAsteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids:List<DatabaseAsteroid>)

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate <= :date")
    fun removeObsolete(date:String)
}


@Database(entities = [DatabaseAsteroid::class], version = 3)
abstract class AsteroidsDatabase : RoomDatabase(){
    abstract val asteroidDao:AsteroidDao
}

private lateinit var INSTANCE : AsteroidsDatabase

fun getDatabase(context: Context) : AsteroidsDatabase{
    synchronized(AsteroidsDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}