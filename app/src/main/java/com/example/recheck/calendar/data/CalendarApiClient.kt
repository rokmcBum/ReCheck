package com.example.recheck.calendar.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CalendarApiClient {
    private const val BASE_URL = "https://www.googleapis.com/calendar/v3/"

    fun create(token: String): CalendarApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val req = chain.request().newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    return chain.proceed(req)
                }
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CalendarApiService::class.java)
    }
}
