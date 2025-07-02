package de.thws.challengeaccepted.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.31.39.188:5000/api/"

    // Standard-Client (ohne Auth)
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Client mit Auth-Token
    fun getRetrofit(context: Context): Retrofit {
        val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = prefs.getString("token", null)
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}