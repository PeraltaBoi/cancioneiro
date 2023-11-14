package com.peraltaboi.cancioneiro

import Song
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface ApiService {
    @GET("api/songs")
    suspend fun getAllSongs(): List<String>

    @GET("api/songs/{song_name}")
    suspend fun getSong(@Path("song_name") name: String): Song

    // Other potential API calls can be added here.

    companion object {
        private const val BASE_URL = "https://cancioneiro-0ef9303ee093.herokuapp.com"

        fun create(): ApiService {
            // Now we can simply create an OkHttpClient without the unsafe trust manager.
            val okHttpClient = OkHttpClient.Builder()
                // You can add other client configurations such as logging interceptors here if needed.
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}