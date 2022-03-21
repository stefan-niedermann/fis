package it.niedermann.fis.weather.provider.org.openweathermap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface OpenWeatherMapService {
    @GET("2.5/weather")
    Call<OpenWeatherMapResponseDto> fetchWeather(
            @Query("lang") String lang,
            @Query("q") String location,
            @Query("units") String units,
            @Query("appId") String key
    );

    @GET("2.5/weather")
    Call<OpenWeatherMapResponseDto> fetchWeather(
            @Query("lang") String lang,
            @Query("id") long location,
            @Query("units") String units,
            @Query("appId") String key
    );
}
