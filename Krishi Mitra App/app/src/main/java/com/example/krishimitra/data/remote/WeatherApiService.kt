package com.example.krishimitra.data.remote

import com.example.krishimitra.Constants
import com.example.krishimitra.domain.weather_models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = Constants.WEATHER_API_KEY
    ): Response<WeatherResponse>

}