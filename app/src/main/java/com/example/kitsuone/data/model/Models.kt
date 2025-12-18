package com.example.kitsuone.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============================================================================
// Common Response Wrapper
// ============================================================================
@Serializable
data class ApiResponse<T>(
    val success: Boolean = false,
    val status: Int = 200,
    val data: T? = null
)

// ============================================================================
// Common Models
// ============================================================================
@Serializable
data class Episodes(
    val sub: Int? = null,
    val dub: Int? = null
)

@Serializable
data class AnimeCommon(
    val id: String,
    @SerialName("title")
    val name: String? = null,
    val poster: String,
    val jname: String? = null,
    val type: String? = null,
    val duration: String? = null,
    val rating: String? = null,
    val episodes: Episodes? = null
)

// ============================================================================
// Home Page Models
// ============================================================================
// ============================================================================
// Home Page Models
// ============================================================================
@Serializable
data class HomeData(
    val genres: List<String> = emptyList(),
    @SerialName("latestEpisode")
    val latestEpisodeAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("spotlight")
    val spotlightAnimes: List<SpotlightAnime> = emptyList(),
    @SerialName("top10")
    val top10Animes: Top10Data? = null,
    @SerialName("topAiring")
    val topAiringAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("topUpcoming")
    val topUpcomingAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("trending")
    val trendingAnimes: List<TrendingAnime> = emptyList(),
    @SerialName("mostPopular")
    val mostPopularAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("mostFavorite")
    val mostFavoriteAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("latestCompleted")
    val latestCompletedAnimes: List<AnimeCommon> = emptyList()
)

@Serializable
data class SpotlightAnime(
    val id: String,
    @SerialName("title")
    val name: String? = null, // Made optional/nullable just in case
    val jname: String? = null,
    val poster: String,
    val description: String? = null,
    val rank: Int,
    val otherInfo: List<String> = emptyList(),
    val episodes: Episodes? = null
)

@Serializable
data class TrendingAnime(
    val id: String,
    @SerialName("title")
    val name: String? = null,
    val poster: String,
    val rank: Int
)

@Serializable
data class Top10Data(
    val today: List<Top10Anime> = emptyList(),
    val week: List<Top10Anime> = emptyList(),
    val month: List<Top10Anime> = emptyList()
)

@Serializable
data class Top10Anime(
    val id: String,
    @SerialName("title")
    val name: String? = null,
    val poster: String,
    val rank: Int,
    val episodes: Episodes? = null
)

// ============================================================================
// Anime Details Models
// ============================================================================
@Serializable
data class AnimeDetailsDto(
    val id: String,
    @SerialName("title")
    val name: String,
    val poster: String,
    @SerialName("synopsis")
    val description: String? = null,
    @SerialName("alternativeTitle")
    val jname: String? = null,
    @SerialName("japanese")
    val japanese: String? = null,
    
    val rating: String? = null,
    val type: String? = null,
    val duration: String? = null,
    
    val episodes: Episodes? = null,
    
    val genres: List<String> = emptyList(),
    val studios: List<String> = emptyList(),
    val producers: List<String> = emptyList(),
    val status: String? = null,
    @SerialName("MAL_score") 
    val malScore: String? = null,
    
    @SerialName("mostPopular") 
    val mostPopularAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("recommended") 
    val recommendedAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("related") 
    val relatedAnimes: List<AnimeCommon> = emptyList(),
    @SerialName("moreSeasons") 
    val seasons: List<Season> = emptyList()
)

@Serializable
data class AnimeDetailsResponse(
    val anime: AnimeDetailsWrapper,
    val mostPopularAnimes: List<AnimeCommon> = emptyList(),
    val recommendedAnimes: List<AnimeCommon> = emptyList(),
    val relatedAnimes: List<AnimeCommon> = emptyList(),
    val seasons: List<Season> = emptyList()
)

@Serializable
data class AnimeDetailsWrapper(
    val info: AnimeInfo,
    val moreInfo: AnimeMoreInfo
)

@Serializable
data class AnimeInfo(
    val id: String,
    @SerialName("title")
    val name: String,
    val poster: String,
    val description: String,
    val stats: AnimeStats,
    val promotionalVideos: List<PromoVideo> = emptyList(),
    val characterVoiceActor: List<CharacterVoiceActor> = emptyList()
)

@Serializable
data class AnimeStats(
    val rating: String? = null,
    val quality: String? = null,
    val episodes: Episodes,
    val type: String,
    val duration: String
)

@Serializable
data class AnimeMoreInfo(
    val aired: String? = null,
    val genres: List<String> = emptyList(),
    val status: String? = null,
    val studios: List<String> = emptyList(), // Changed from String? to List<String>
    val producers: List<String> = emptyList(), // Changed from String? to List<String>
    val duration: String? = null,
    val japanese: String? = null,
    val synonyms: String? = null,
    @SerialName("malScore")
    val malScore: String? = null
)

@Serializable
data class PromoVideo(
    val title: String? = null,
    val source: String? = null,
    val thumbnail: String? = null
)

@Serializable
data class CharacterVoiceActor(
    val character: CharacterInfo,
    val voiceActor: VoiceActorInfo
)

@Serializable
data class CharacterInfo(
    val id: String,
    val poster: String,
    val name: String,
    val cast: String
)

@Serializable
data class VoiceActorInfo(
    val id: String,
    val poster: String,
    val name: String,
    val cast: String
)

@Serializable
data class Season(
    val id: String,
    @SerialName("title")
    val name: String? = null,
    @SerialName("alternativeTitle")
    val title: String? = null,
    val poster: String? = null,
    @SerialName("isActive")
    val isCurrent: Boolean = false
)

// ============================================================================
// Episode Models
// ============================================================================
@Serializable
data class EpisodeListResponse(
    val totalEpisodes: Int,
    val episodes: List<Episode>
)

@Serializable
data class Episode(
    @SerialName("episodeNumber")
    val number: Int = 0,
    val title: String? = null,
    @SerialName("id")
    val episodeId: String = "",
    val isFiller: Boolean = false
)

// ============================================================================
// Server Models
// ============================================================================
@Serializable
data class EpisodeServers(
    val episodeId: String,
    val episodeNo: Int,
    val sub: List<Server> = emptyList(),
    val dub: List<Server> = emptyList(),
    val raw: List<Server> = emptyList()
)

@Serializable
data class Server(
    val serverId: Int,
    val serverName: String
)

// ============================================================================
// Streaming Models
// ============================================================================
@Serializable
data class StreamingResponse(
    val headers: Map<String, String>? = null,
    val link: StreamLink? = null,
    val tracks: List<Track> = emptyList(),
    val anilistID: Int? = null,
    val malID: Int? = null
)

@Serializable
data class StreamLink(
    val file: String,
    val type: String? = null
)

@Serializable
data class Track(
    val file: String,
    val kind: String,
    val label: String? = null,
    val default: Boolean = false
)



// ============================================================================
// Search Models
// ============================================================================
@Serializable
data class SearchResponse(
    val pageInfo: PageInfo? = null,
    @SerialName("response")
    val animes: List<AnimeCommon> = emptyList()
)

@Serializable
data class SuggestionResponse(
    @SerialName("data") // Sometimes suggestions are a direct list in some APIs, but docs say data: [...]
    val suggestions: List<SearchSuggestion> = emptyList()
)

@Serializable
data class SearchSuggestion(
    val id: String,
    @SerialName("title")
    val name: String? = null, // Make nullable
    val poster: String,
    val jname: String? = null,
    val moreInfo: List<String> = emptyList()
)

// ============================================================================
// Page Info Models  
// ============================================================================
@Serializable
data class PageInfo(
    val totalPages: Int = 1,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false
)

// ============================================================================
// Explore Models
// ============================================================================
@Serializable
data class GenreResponse(
    val pageInfo: PageInfo? = null,
    @SerialName("response")
    val animes: List<AnimeCommon> = emptyList()
)

@Serializable
data class CategoryResponse(
    val pageInfo: PageInfo? = null,
    @SerialName("response")
    val animes: List<AnimeCommon> = emptyList()
)

@Serializable
data class AZListResponse(
    val pageInfo: PageInfo? = null,
    @SerialName("response")
    val animes: List<AnimeCommon> = emptyList()
)

// ============================================================================
// Schedule Models
// ============================================================================
@Serializable
data class ScheduleResponse(
    val date: String? = null,
    val timeZoneOffset: Int? = null,
    val scheduledAnimes: List<ScheduledAnime> = emptyList()
)

@Serializable
data class ScheduledAnime(
    val id: String,
    val time: String,
    @SerialName("title")
    val name: String,
    val jname: String? = null,
    val airingTimestamp: Long = 0,
    val secondsUntilAiring: Long = 0
)

// ============================================================================
// Characters Models
// ============================================================================
@Serializable
data class CharacterResponseWrapper(
    val pageInfo: PageInfo? = null,
    val response: List<CharacterItem> = emptyList()
)

@Serializable
data class CharacterItem(
    val id: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val role: String? = null,
    val voiceActors: List<VoiceActorItem> = emptyList()
)

@Serializable
data class VoiceActorItem(
    val id: String? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val cast: String? = null
)

@Serializable
data class NextEpisodeResponse(
    val time: String? = null
)
