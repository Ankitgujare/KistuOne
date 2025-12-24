
package com.example.kitsuone.worker

import android.content.Context
import android.util.Log

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kitsuone.data.local.entity.WatchStatus
import com.example.kitsuone.data.repository.WatchlistRepository
import com.example.kitsuone.util.NotificationUtils
import com.example.kitsuone.KitsuOneApplication
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.first

class RecommendationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        return try {
            // TODO: IMPORTANT! Replace "YOUR_API_KEY_HERE" with your actual Gemini API Key from Google AI Studio.
            // You can also move this to BuildConfig.GEMINI_API_KEY for better security.
            val validApiKey = "AIzaSyDdyiRb3wkQJrhtrYiOmmVTPMnWdaRWMQ8"
            
            if (validApiKey == "YOUR_API_KEY_HERE" || validApiKey.isEmpty()) {
                Log.w("RecWorker", "Gemini API Key is missing. Please set it in RecommendationWorker.kt to enable AI recommendations.")
                // Return success to avoid unnecessary retries restricted by WorkManager backoff
                return Result.success()
            }

            val application = applicationContext as KitsuOneApplication
            val repository = application.container.watchlistRepository

            // Fetch user's watched list (Completed or Watching)
            val allAnime = repository.getAllWatchlist().first()
            val watchedAnime = allAnime.filter { 
                it.status == WatchStatus.COMPLETED || it.status == WatchStatus.WATCHING 
            }.takeLast(5) // Take last 5 for recent context

            if (watchedAnime.isEmpty()) {
                Log.d("RecWorker", "No anime watched yet. Skipping.")
                return Result.success()
            }

            val animeTitles = watchedAnime.joinToString(", ") { it.title }

            // AI Logic
            val generativeModel = GenerativeModel(
                modelName = "gemini-pro", // or gemini-1.5-flash
                apiKey = validApiKey
            )

            val prompt = """
                Based on these anime: $animeTitles.
                Recommend ONE single anime that the user hasn't watched from that list.
                Format the output exactly as: "Title | Short Reason"
                Example: "Attack on Titan | Because you like intense action."
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val responseText = response.text

            if (!responseText.isNullOrBlank() && responseText.contains("|")) {
                val parts = responseText.split("|")
                if (parts.size == 2) {
                    val title = parts[0].trim()
                    val reason = parts[1].trim()

                    NotificationUtils.showNotification(
                        applicationContext,
                        "Recommended: $title",
                        reason
                    )
                } else {
                     NotificationUtils.showNotification(
                        applicationContext,
                        "Anime Recommendation",
                        responseText
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("RecWorker", "Error generating recommendation", e)
            Result.retry()
        }
    }
}
