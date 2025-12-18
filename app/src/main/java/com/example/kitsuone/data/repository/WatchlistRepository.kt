package com.example.kitsuone.data.repository

import com.example.kitsuone.data.local.dao.WatchlistDao
import com.example.kitsuone.data.local.entity.WatchStatus
import com.example.kitsuone.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>
    fun getWatchlistByStatus(status: String): Flow<List<WatchlistEntity>>
    fun getWatchlistItem(animeId: String): Flow<WatchlistEntity?>
    suspend fun addToWatchlist(
        animeId: String,
        title: String,
        posterUrl: String?,
        totalEpisodes: Int?,
        type: String?,
        status: String = WatchStatus.PLAN_TO_WATCH
    )
    suspend fun updateProgress(animeId: String, currentEpisode: Int)
    suspend fun updateStatus(animeId: String, status: String)
    suspend fun removeFromWatchlist(animeId: String)
    suspend fun isInWatchlist(animeId: String): Boolean
    suspend fun getWatchlistCount(): Int
    suspend fun getCountByStatus(status: String): Int
}

class WatchlistRepositoryImpl(
    private val watchlistDao: WatchlistDao
) : WatchlistRepository {
    
    override fun getAllWatchlist(): Flow<List<WatchlistEntity>> {
        return watchlistDao.getAllWatchlist()
    }
    
    override fun getWatchlistByStatus(status: String): Flow<List<WatchlistEntity>> {
        return watchlistDao.getWatchlistByStatus(status)
    }
    
    override fun getWatchlistItem(animeId: String): Flow<WatchlistEntity?> {
        return watchlistDao.getWatchlistItemFlow(animeId)
    }
    
    override suspend fun addToWatchlist(
        animeId: String,
        title: String,
        posterUrl: String?,
        totalEpisodes: Int?,
        type: String?,
        status: String
    ) {
        val timestamp = System.currentTimeMillis()
        val entity = WatchlistEntity(
            animeId = animeId,
            title = title,
            posterUrl = posterUrl,
            currentEpisode = 0,
            totalEpisodes = totalEpisodes,
            status = status,
            addedDate = timestamp,
            lastUpdated = timestamp,
            type = type
        )
        watchlistDao.insertWatchlistItem(entity)
    }
    
    override suspend fun updateProgress(animeId: String, currentEpisode: Int) {
        val timestamp = System.currentTimeMillis()
        watchlistDao.updateProgress(animeId, currentEpisode, timestamp)
    }
    
    override suspend fun updateStatus(animeId: String, status: String) {
        val timestamp = System.currentTimeMillis()
        watchlistDao.updateStatus(animeId, status, timestamp)
    }
    
    override suspend fun removeFromWatchlist(animeId: String) {
        watchlistDao.deleteWatchlistItemById(animeId)
    }
    
    override suspend fun isInWatchlist(animeId: String): Boolean {
        return watchlistDao.getWatchlistItem(animeId) != null
    }
    
    override suspend fun getWatchlistCount(): Int {
        return watchlistDao.getWatchlistCount()
    }
    
    override suspend fun getCountByStatus(status: String): Int {
        return watchlistDao.getCountByStatus(status)
    }
}
