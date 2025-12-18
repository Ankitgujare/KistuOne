package com.example.kitsuone.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val animeId: String,
    val title: String,
    val posterUrl: String?,
    val currentEpisode: Int,
    val totalEpisodes: Int?,
    val status: String, // WATCHING, COMPLETED, PLAN_TO_WATCH
    val addedDate: Long,
    val lastUpdated: Long,
    val type: String? = null // TV, Movie, OVA, etc.
)

// Watch status constants
object WatchStatus {
    const val WATCHING = "WATCHING"
    const val COMPLETED = "COMPLETED"
    const val PLAN_TO_WATCH = "PLAN_TO_WATCH"
}
