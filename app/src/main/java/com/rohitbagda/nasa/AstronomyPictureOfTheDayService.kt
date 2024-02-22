package com.rohitbagda.nasa

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AstronomyPictureOfTheDayService {
    companion object {
        private val contentType = MediaType.get("application/json")
        private val nasaApi = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(NasaApi::class.java)
    }

    fun getLast5(): List<AstronomyPictureOfTheDay> {
        val astronomyPictureOfTheDayList = mutableListOf<AstronomyPictureOfTheDay>()
        var currentDate = LocalDate.now()
        Log.i("AstronomyPictureOfTheDayService", "Getting Pictures")
        while (astronomyPictureOfTheDayList.size < 5) {
            val response = getAstronomyPictureOfTheDay(getFormattedDate(currentDate))
            if (response.isSuccessful) {
                val body = response.body()
                if (body!= null && body.mediaType == "image") {
                    Log.i("AstronomyPictureOfTheDayService","Success: $body")
                    astronomyPictureOfTheDayList.add(body)
                }
                currentDate = currentDate.minusDays(1)
            }
            if (response.code() == 429) {
                Thread.sleep(1000)
            }
        }
        Log.i("AstronomyPictureOfTheDayService", "Finished Fetching Pictures")
        return astronomyPictureOfTheDayList
    }

    private fun getAstronomyPictureOfTheDay(date: String): Response<AstronomyPictureOfTheDay> {
        val request = nasaApi.getAstronomyPictureOfTheDay(BuildConfig.NASA_API_KEY, date)
        return request.execute()
    }

    private fun getFormattedDate(currentDate: LocalDate) = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
}

interface NasaApi {
    @GET("/planetary/apod")
    fun getAstronomyPictureOfTheDay(@Query("api_key") apiKey: String, @Query("date") date: String): Call<AstronomyPictureOfTheDay>
}