package com.example.kitsuone.data.local.dao

import androidx.room.*
import com.example.kitsuone.data.local.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    
    @Query("SELECT * FROM watchlist ORDER BY lastUpdated DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>
    
    @Query("SELECT * FROM watchlist WHERE status = :status ORDER BY lastUpdated DESC")
    fun getWatchlistByStatus(status: String): Flow<List<WatchlistEntity>>
    
    @Query("SELECT * FROM watchlist WHERE animeId = :animeId")
    suspend fun getWatchlistItem(animeId: String): WatchlistEntity?
    
    @Query("SELECT * FROM watchlist WHERE animeId = :animeId")
    fun getWatchlistItemFlow(animeId: String): Flow<WatchlistEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistEntity)
    
    @Update
    suspend fun updateWatchlistItem(item: WatchlistEntity)
    
    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistEntity)
    
    @Query("DELETE FROM watchlist WHERE animeId = :animeId")
    suspend fun deleteWatchlistItemById(animeId: String)
    
    @Query("UPDATE watchlist SET currentEpisode = :episode, lastUpdated = :timestamp WHERE animeId = :animeId")
    suspend fun updateProgress(animeId: String, episode: Int, timestamp: Long)
    
    @Query("UPDATE watchlist SET status = :status, lastUpdated = :timestamp WHERE animeId = :animeId")
    suspend fun updateStatus(animeId: String, status: String, timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM watchlist")
    suspend fun getWatchlistCount(): Int
    
    @Query("SELECT COUNT(*) FROM watchlist WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    @Query("SELECT title FROM watchlist WHERE status IN (:statuses) ORDER BY lastUpdated DESC LIMIT 20")
    suspend fun getWatchedTitlesSync(statuses: List<String>): List<String>
}
