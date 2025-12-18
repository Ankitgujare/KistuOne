package com.example.kitsuone.data.repository

import com.example.kitsuone.data.api.HianimeService
import com.example.kitsuone.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AnimeRepository {
    fun getHomeData(): Flow<ApiResponse<HomeData>>
    fun getAnimeDetails(id: String): Flow<ApiResponse<AnimeDetailsResponse>>
    fun getEpisodes(id: String): Flow<ApiResponse<EpisodeListResponse>>
    fun getServers(episodeId: String): Flow<ApiResponse<EpisodeServers>>
    fun getStreamingSources(id: String, server: String?, category: String?): Flow<ApiResponse<StreamingResponse>>
    fun searchAnime(query: String, page: Int, filters: Map<String, String> = emptyMap()): Flow<ApiResponse<SearchResponse>>
    fun getSearchSuggestions(query: String): Flow<ApiResponse<SuggestionResponse>>
    fun getCharacters(id: String): Flow<ApiResponse<CharacterResponseWrapper>>
    fun getNextEpisodeSchedule(id: String): Flow<ApiResponse<NextEpisodeResponse>>
}

class NetworkAnimeRepository(
    private val apiService: HianimeService
) : AnimeRepository {

    override fun getHomeData(): Flow<ApiResponse<HomeData>> = flow {
        emit(apiService.getHome())
    }

    override fun getAnimeDetails(id: String): Flow<ApiResponse<AnimeDetailsResponse>> = flow {
        val result = apiService.getAnimeDetails(id)
        if (result.success && result.data != null) {
            val dto = result.data
            
            // Transform DTO to UI Model
            val uiModel = AnimeDetailsResponse(
                anime = AnimeDetailsWrapper(
                    info = AnimeInfo(
                        id = dto.id,
                        name = dto.name,
                        poster = dto.poster,
                        description = dto.description ?: "",
                        stats = AnimeStats(
                            rating = dto.rating,
                            episodes = dto.episodes ?: Episodes(null, null),
                            type = dto.type ?: "Unknown",
                            duration = dto.duration ?: "Unknown"
                        )
                    ),
                    moreInfo = AnimeMoreInfo(
                        genres = dto.genres,
                        status = dto.status,
                        studios = dto.studios,
                        producers = dto.producers,
                        duration = dto.duration,
                        japanese = dto.japanese,
                        malScore = dto.malScore
                    )
                ),
                mostPopularAnimes = dto.mostPopularAnimes,
                recommendedAnimes = dto.recommendedAnimes,
                relatedAnimes = dto.relatedAnimes,
                seasons = dto.seasons
            )
            emit(ApiResponse(success = true, status = 200, data = uiModel))
        } else {
            // Forward error
            emit(ApiResponse(success = false, status = result.status, data = null))
        }
    }

    override fun getEpisodes(id: String): Flow<ApiResponse<EpisodeListResponse>> = flow {
        val result = apiService.getEpisodes(id)
        if (result.success && result.data != null) {
            val episodesList = result.data
            val responseObj = EpisodeListResponse(
                totalEpisodes = episodesList.size,
                episodes = episodesList
            )
            emit(ApiResponse(success = true, status = 200, data = responseObj))
        } else {
            emit(ApiResponse(success = false, status = result.status, data = null))
        }
    }

    override fun getServers(episodeId: String): Flow<ApiResponse<EpisodeServers>> = flow {
        emit(apiService.getServers(episodeId))
    }

    override fun getStreamingSources(
        id: String,
        server: String?,
        category: String?
    ): Flow<ApiResponse<StreamingResponse>> = flow {
        emit(apiService.getStreamingSources(id, server, category))
    }

    override fun searchAnime(query: String, page: Int, filters: Map<String, String>): Flow<ApiResponse<SearchResponse>> = flow {
        emit(apiService.searchAnime(query, page, filters))
    }

    override fun getSearchSuggestions(query: String): Flow<ApiResponse<SuggestionResponse>> = flow {
        emit(apiService.getSearchSuggestions(query))
    }

    override fun getCharacters(id: String): Flow<ApiResponse<CharacterResponseWrapper>> = flow {
        emit(apiService.getCharacters(id))
    }

    override fun getNextEpisodeSchedule(id: String): Flow<ApiResponse<NextEpisodeResponse>> = flow {
        emit(apiService.getNextEpisodeSchedule(id))
    }
}
