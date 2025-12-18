package com.example.kitsuone.ui.screens.player

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.kitsuone.ui.theme.AccentRed
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    episodeId: String,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var bufferedPosition by remember { mutableLongStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFullscreen by remember { mutableStateOf(false) }
    var playerTracks by remember { mutableStateOf<androidx.media3.common.Tracks?>(null) }
    
    // Seek Feedback State
    var seekFeedbackVisible by remember { mutableStateOf(false) }
    var seekFeedbackForward by remember { mutableStateOf(true) } // true = forward, false = rewind (backward)
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Toggle controls visibility
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }
    
    // Hide seek feedback after a short delay
    LaunchedEffect(seekFeedbackVisible) {
        if (seekFeedbackVisible) {
            delay(600)
            seekFeedbackVisible = false
        }
    }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_IDLE -> { /* Do nothing */ }
                        Player.STATE_READY -> {
                            isLoading = false
                            totalDuration = duration.coerceAtLeast(0)
                        }
                        Player.STATE_BUFFERING -> isLoading = true
                        Player.STATE_ENDED -> {
                            isPlaying = false
                            viewModel.playNextEpisode()
                        }
                    }
                }
                
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
                
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    super.onPlayerError(error)
                    // Construct detailed error message
                    val cause = error.cause
                    val errorDetails = if (cause != null) "${cause.javaClass.simpleName}: ${cause.message}" else error.message
                    errorMessage = "${error.errorCodeName}\n$errorDetails"
                    
                    isLoading = false
                    Log.e("PlayerScreen", "Player Error: $errorMessage", error)
                }

                override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                    playerTracks = tracks
                    Log.d("PlayerScreen", "onTracksChanged: ${tracks.groups.size} groups")
                }
            })
        }
    }

    // Load Stream
    LaunchedEffect(episodeId) {
        viewModel.loadStream(episodeId)
    }

    // Observe UI State and update player
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PlayerUiState.Success -> {
                Log.d("PlayerScreen", "Loading video: ${state.videoUrl}")

                val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .setAllowCrossProtocolRedirects(true)
                    .setDefaultRequestProperties(state.headers)

                val subtitleTracks = state.tracks.filter { it.kind == "subtitles" || it.kind == "captions" }
                Log.d("PlayerScreen", "Configuring ${subtitleTracks.size} subtitle tracks")

                val mediaItemBuilder = MediaItem.Builder()
                    .setUri(state.videoUrl.toUri())
                    // Add subtitles
                    .setSubtitleConfigurations(
                        subtitleTracks.map { track ->
                            val mimeType = when {
                                track.file.lowercase().endsWith(".srt") -> MimeTypes.APPLICATION_SUBRIP
                                track.file.lowercase().endsWith(".vtt") -> MimeTypes.TEXT_VTT
                                track.file.lowercase().endsWith(".ttml") -> MimeTypes.APPLICATION_TTML
                                track.file.lowercase().endsWith(".dfxp") -> MimeTypes.APPLICATION_TTML
                                track.file.lowercase().endsWith(".xml") -> MimeTypes.APPLICATION_TTML
                                else -> MimeTypes.TEXT_VTT // Default/Fallback
                            }
                            Log.d("PlayerScreen", "  Adding Subtitle: ${track.label} (${track.file}) -> $mimeType")
                            MediaItem.SubtitleConfiguration.Builder(track.file.toUri())
                                .setMimeType(mimeType)
                                .setLanguage(track.label) // Usually "English", "Japanese", etc.
                                .setLabel(track.label)
                                .setSelectionFlags(if (track.default) C.SELECTION_FLAG_DEFAULT else 0)
                                .build()
                        }
                    )
                val mediaItem = mediaItemBuilder.build()

                val mediaSource = if (state.type.equals("hls", ignoreCase = true) || state.videoUrl.contains(".m3u8")) {
                    HlsMediaSource.Factory(defaultHttpDataSourceFactory)
                        .createMediaSource(mediaItem)
                } else {
                    ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory)
                        .createMediaSource(mediaItem)
                }
                
                exoPlayer.setMediaSource(mediaSource)
                
                // Prioritize English Subtitles
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setPreferredTextLanguage("en")
                    .setPreferredTextRoleFlags(C.ROLE_FLAG_SUBTITLE)
                    .build()

                exoPlayer.prepare()
                isLoading = false
                errorMessage = null
            }
            is PlayerUiState.Error -> {
                isLoading = false
                errorMessage = state.message
            }
            is PlayerUiState.Loading -> {
                isLoading = true
                errorMessage = null
            }
        }
    }

    // Update current position
    LaunchedEffect(isPlaying) {
        while (true) {
            if (isPlaying) {
                currentPosition = exoPlayer.currentPosition
                bufferedPosition = exoPlayer.bufferedPosition
            }
            delay(1000)
        }
    }

    // Release player when the screen is destroyed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Main player UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        val screenWidth = size.width
                        val tapX = offset.x
                        
                        if (tapX < screenWidth * 0.35) {
                            // Left 35% -> Rewind 10s
                            val newPosition = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                            exoPlayer.seekTo(newPosition)
                            seekFeedbackForward = false
                            seekFeedbackVisible = true
                        } else if (tapX > screenWidth * 0.65) {
                            // Right 35% -> Forward 10s
                            val newPosition = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPosition)
                            seekFeedbackForward = true
                            seekFeedbackVisible = true
                        }
                        // Center 30% -> Ignore double tap (or could handle same as single tap)
                    },
                    onTap = {
                        showControls = !showControls
                    }
                )
            }
    ) {
        // Video Player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Seek Feedback Overlay
        if (seekFeedbackVisible) {
            Box(
                modifier = Modifier
                    .align(if (seekFeedbackForward) Alignment.CenterEnd else Alignment.CenterStart)
                    .padding(horizontal = 48.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (seekFeedbackForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                        contentDescription = if (seekFeedbackForward) "+10s" else "-10s",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = if (seekFeedbackForward) "+10s" else "-10s",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Loading Indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentRed
            )
        }

        // Error Message
        errorMessage?.let { message ->
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error loading video",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        viewModel.loadStream(episodeId) // Retry loading
                        exoPlayer.prepare() 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Retry")
                }
            }
        }

        // Player Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            if (isFullscreen) {
                                isFullscreen = false
                                // TODO: Handle window orientation change if needed
                            } else {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isFullscreen) "Exit Fullscreen" else "Back",
                            tint = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Episode Info
                    Text(
                        text = viewModel.currentEpisodeTitle,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Fullscreen Toggle
                    IconButton(
                        onClick = { isFullscreen = !isFullscreen },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) "Exit Fullscreen" else "Fullscreen",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Center Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Previous Episode
                    IconButton(
                        onClick = { viewModel.playPreviousEpisode() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Episode",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Play/Pause
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Next Episode
                    IconButton(
                        onClick = { viewModel.playNextEpisode() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Episode",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Bottom Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDuration(currentPosition),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.width(48.dp)
                        )
                        
                        LinearProgressIndicator(
                            progress = { if (totalDuration > 0) (currentPosition.toFloat() / totalDuration) else 0f },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AccentRed,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        
                        Text(
                            text = formatDuration(totalDuration),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.width(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Additional Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Audio / Language (Sub/Dub)
                        var showAudioOptions by remember { mutableStateOf(false) }
                        
                        Box {
                            IconButton(
                                onClick = { showAudioOptions = !showAudioOptions }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = "Audio",
                                    tint = Color.White
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showAudioOptions,
                                onDismissRequest = { showAudioOptions = false }
                            ) {
                                val categories = listOf("sub", "dub")
                                categories.forEach { category ->
                                    val isSelected = viewModel.currentCategory == category
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = category.uppercase(),
                                                color = if (isSelected) AccentRed else Color.Black
                                            ) 
                                        },
                                        onClick = { 
                                            viewModel.switchCategory(category)
                                            showAudioOptions = false 
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Subtitles
                        var showSubtitleOptions by remember { mutableStateOf(false) }
                        
                        Box {
                            IconButton(
                                onClick = { showSubtitleOptions = !showSubtitleOptions }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ClosedCaption,
                                    contentDescription = "Subtitles",
                                    tint = if (viewModel.uiState.collectAsState().value is PlayerUiState.Success) Color.White else Color.Gray
                                )
                            }
                            
                            val state = viewModel.uiState.collectAsState().value
                            if (state is PlayerUiState.Success) {
                                DropdownMenu(
                                    expanded = showSubtitleOptions,
                                    onDismissRequest = { showSubtitleOptions = false }
                                ) {
                                    // Off option
                                    DropdownMenuItem(
                                        text = { Text("Off") },
                                        onClick = {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                                                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                                                .build()
                                            showSubtitleOptions = false
                                        }
                                    )
                                    
                                    // Available Text Tracks
                                    playerTracks?.groups?.forEach { group ->
                                        if (group.type == C.TRACK_TYPE_TEXT) {
                                            for (i in 0 until group.length) {
                                                if (group.isTrackSupported(i)) {
                                                    val trackFormat = group.getTrackFormat(i)
                                                    val label = trackFormat.label ?: trackFormat.language ?: "Unknown"
                                                    val isSelected = group.isTrackSelected(i)
                                                    
                                                    DropdownMenuItem(
                                                        text = { 
                                                            Text(
                                                                text = label,
                                                                color = if (isSelected) AccentRed else Color.Black
                                                            ) 
                                                        },
                                                        onClick = { 
                                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                                .buildUpon()
                                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                                                                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                                                                .addOverride(androidx.media3.common.TrackSelectionOverride(group.mediaTrackGroup, i))
                                                                .build()
                                                            showSubtitleOptions = false 
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Quality / Server
                        var showQualityOptions by remember { mutableStateOf(false) }
                        
                        Box {
                            IconButton(
                                onClick = { showQualityOptions = !showQualityOptions }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HighQuality,
                                    contentDescription = "Quality",
                                    tint = Color.White
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showQualityOptions,
                                onDismissRequest = { showQualityOptions = false }
                            ) {
                                val servers = listOf("hd-1", "hd-2")
                                servers.forEach { server ->
                                    val isSelected = viewModel.currentServer == server
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = server.uppercase(),
                                                color = if (isSelected) AccentRed else Color.Black
                                            ) 
                                        },
                                        onClick = { 
                                            viewModel.switchServer(server)
                                            showQualityOptions = false 
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to format duration in milliseconds to MM:SS format
private fun formatDuration(durationMs: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
