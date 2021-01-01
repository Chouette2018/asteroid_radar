package com.udacity.asteroidradar.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
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


const val NASA_API_KEY = "kJ0NjlUIisbZEOldE7rDRznqq0ZncITSIBZMS0z2"
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
        try {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(API_QUERY_DATE_FORMAT)
            val formatted = current.format(formatter)

            var jsonResponse = nasaService.getAsteroidsList(formatted)

            var asteroidsList = parseAsteroidsJsonResult(JSONObject(jsonResponse.string()))

            return asteroidsList
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emptyList()
    }

    suspend fun getImageOfTheDay(): String {
        try {
            val imageOfTheDay = nasaService.getImageOfTheDay()

            return imageOfTheDay.url
        } catch (e: HttpException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }
}