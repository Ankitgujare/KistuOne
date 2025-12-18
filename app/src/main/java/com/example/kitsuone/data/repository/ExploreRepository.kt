package com.example.kitsuone.data.repository

import com.example.kitsuone.data.api.HianimeService
import com.example.kitsuone.data.model.AZListResponse
import com.example.kitsuone.data.model.ApiResponse
import com.example.kitsuone.data.model.CategoryResponse
import com.example.kitsuone.data.model.GenreResponse
import com.example.kitsuone.data.model.ScheduleResponse

interface ExploreRepository {
    suspend fun getGenreAnime(genreName: String, page: Int): ApiResponse<GenreResponse>
    suspend fun getCategoryAnime(categoryName: String, page: Int): ApiResponse<CategoryResponse>
    suspend fun getAZList(sortOption: String, page: Int): ApiResponse<AZListResponse>
    suspend fun getEstimatedSchedule(date: String): ApiResponse<ScheduleResponse>
}

class NetworkExploreRepository(
    private val hianimeService: HianimeService
) : ExploreRepository {

    override suspend fun getGenreAnime(genreName: String, page: Int): ApiResponse<GenreResponse> {
        return hianimeService.getGenreAnime(genreName, page)
    }

    override suspend fun getCategoryAnime(categoryName: String, page: Int): ApiResponse<CategoryResponse> {
        return hianimeService.getCategoryAnime(categoryName, page)
    }

    override suspend fun getAZList(sortOption: String, page: Int): ApiResponse<AZListResponse> {
        return hianimeService.getAZList(sortOption, page)
    }

    override suspend fun getEstimatedSchedule(date: String): ApiResponse<ScheduleResponse> {
        return hianimeService.getEstimatedSchedule(date)
    }
}
