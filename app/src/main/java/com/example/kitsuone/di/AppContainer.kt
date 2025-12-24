package com.example.kitsuone.di

import android.content.Context
import com.example.kitsuone.data.api.HianimeService
import com.example.kitsuone.data.local.KitsuDatabase
import com.example.kitsuone.data.repository.AnimeRepository
import com.example.kitsuone.data.repository.ExploreRepository
import com.example.kitsuone.data.repository.NetworkAnimeRepository
import com.example.kitsuone.data.repository.NetworkExploreRepository
import com.example.kitsuone.data.repository.WatchlistRepository
import com.example.kitsuone.data.repository.WatchlistRepositoryImpl
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

interface AppContainer {
    val animeRepository: AnimeRepository
    val watchlistRepository: WatchlistRepository
    val exploreRepository: ExploreRepository
    val firebaseAuth: FirebaseAuth
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    // Local network IP - Make sure the aniwatch-api server is running!
    // Start server: Run `npm start` in aniwatch-api folder
    // NOTE: Server runs on port 4000. This only works on WiFi. For mobile data, use ngrok or deploy to cloud
    private val baseUrl = "http://10.96.215.36:3030/"

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = okhttp3.OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    private val retrofitService: HianimeService by lazy {
        retrofit.create(HianimeService::class.java)
    }

    private val database: KitsuDatabase by lazy {
        KitsuDatabase.getDatabase(context)
    }

    override val animeRepository: AnimeRepository by lazy {
        NetworkAnimeRepository(retrofitService)
    }

    override val watchlistRepository: WatchlistRepository by lazy {
        WatchlistRepositoryImpl(database.watchlistDao())
    }

    override val exploreRepository: ExploreRepository by lazy {
        NetworkExploreRepository(retrofitService)
    }

    override val firebaseAuth: FirebaseAuth by lazy {
        Firebase.auth
    }
}
