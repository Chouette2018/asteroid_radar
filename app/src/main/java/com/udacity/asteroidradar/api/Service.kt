package com.udacity.asteroidradar.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import kotlinx.coroutines.Deferred
import okhttp3.*
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val NASA_API_KEY = 
private const val API_KEY_PARAM = "api_key"
private const val START_DATE_PARAM = "start_date"

class NasaInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        var newUrl: HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter(API_KEY_PARAM, NASA_API_KEY)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}

interface NasaService {
    /*@GET("devbytes.json")
    fun getAsteroidsList(): Deferred<NetworkVideoContainer>*/

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsList(@Query(START_DATE_PARAM) startDate :String): ResponseBody//JsonElement JsonObject

    @GET("planetary/apod")
    suspend fun getImageOfTheDay(): NasaImageOfTheDayResponse
}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Network {
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(NasaInterceptor())
        .build()

    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(httpClient)
        .build()

    val nasaService = retrofit.create(NasaService::class.java)


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchAsteroids(startDate:String = ""): List<Asteroid> {
        /*dataLoadingState.postValue(
            DataLoadingState(
                Status.LOADING,
                scenario = scenario
            )
        )*/
        try {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatted = current.format(formatter)

            var jsonResponse = nasaService.getAsteroidsList(formatted)

            var asteroidsList = parseAsteroidsJsonResult(JSONObject(jsonResponse.string()))
            /*dataLoadingState.postValue(
                DataLoadingState(
                    Status.SUCCESS,
                    scenario = scenario
                )
            )*/

            return asteroidsList
        } catch (e: HttpException) {
            //dataLoadingState.postValue(DataLoadingState.error(e.message(), e.code(), scenario = scenario))
            e.printStackTrace()
        } catch (e: IOException) {
            //dataLoadingState.postValue(DataLoadingState.error("Please, check your network settings.", scenario = scenario))
            e.printStackTrace()
        } catch (e: Exception) {
            //dataLoadingState.postValue(DataLoadingState.error("Unknown internal error.", scenario = scenario))
            e.printStackTrace()
        }

        return emptyList()
    }
}