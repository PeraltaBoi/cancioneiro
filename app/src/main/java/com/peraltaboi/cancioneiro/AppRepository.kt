package com.peraltaboi.cancioneiro

import Song

class AppRepository {
    private val apiService = ApiService.create()

    suspend fun getAllSongs(): List<String> {
        return apiService.getAllSongs()
    }

    suspend fun getSong(name: String): Song {
        return apiService.getSong(name)
    }

    // Other repository methods corresponding to your API calls can be added here.
}