package com.example.kitsuone.data.api

import com.example.kitsuone.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface HianimeService {

    @GET("api/v1/home")
    suspend fun getHome(): ApiResponse<HomeData>

    @GET("api/v1/search")
    suspend fun searchAnime(
        @Query("keyword") query: String,
        @Query("page") page: Int = 1,
        @QueryMap filters: Map<String, String> = emptyMap()
    ): ApiResponse<SearchResponse>

    @GET("api/v1/suggestion")
    suspend fun getSearchSuggestions(
        @Query("keyword") query: String
    ): ApiResponse<SuggestionResponse>

    @GET("api/v1/anime/{animeId}")
    suspend fun getAnimeDetails(
        @Path("animeId") animeId: String
    ): ApiResponse<AnimeDetailsDto>

    @GET("api/v1/episodes/{animeId}")
    suspend fun getEpisodes(
        @Path("animeId") animeId: String
    ): ApiResponse<List<Episode>>

    @GET("api/v1/servers")
    suspend fun getServers(
        @Query("id") episodeId: String
    ): ApiResponse<EpisodeServers>

    @GET("api/v1/stream")
    suspend fun getStreamingSources(
        @Query("id") episodeId: String,
        @Query("server") server: String? = "hd-1",
        @Query("type") category: String? = "sub"
    ): ApiResponse<StreamingResponse>

    @GET("api/v1/animes/genre/{name}")
    suspend fun getGenreAnime(
        @Path("name") genreName: String,
        @Query("page") page: Int = 1
    ): ApiResponse<GenreResponse>

    @GET("api/v1/animes/{name}")
    suspend fun getCategoryAnime(
        @Path("name") categoryName: String,
        @Query("page") page: Int = 1
    ): ApiResponse<CategoryResponse>

    @GET("api/v1/animes/az-list/{sortOption}")
    suspend fun getAZList(
        @Path("sortOption") sortOption: String = "all",
        @Query("page") page: Int = 1
    ): ApiResponse<AZListResponse>

    @GET("api/v1/schadule")
    suspend fun getEstimatedSchedule(
        @Query("date") date: String
    ): ApiResponse<ScheduleResponse>

    @GET("api/v1/characters/{animeId}")
    suspend fun getCharacters(
        @Path("animeId") animeId: String
    ): ApiResponse<CharacterResponseWrapper>

    @GET("api/v1/schadule/next/{animeId}")
    suspend fun getNextEpisodeSchedule(
        @Path("animeId") animeId: String
    ): ApiResponse<NextEpisodeResponse>
}
