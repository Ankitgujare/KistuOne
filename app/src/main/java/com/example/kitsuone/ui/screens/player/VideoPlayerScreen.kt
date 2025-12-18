package com.example.kitsuone.ui.screens.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.example.kitsuone.data.model.Track

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    episodeId: String,
    viewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    LaunchedEffect(episodeId) {
        android.util.Log.d("VideoPlayerScreen", "Received episodeId: $episodeId")
        viewModel.loadStream(episodeId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val state = uiState) {
            is PlayerUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is PlayerUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error Playing Video",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            is PlayerUiState.Success -> {
                ExoPlayerView(
                    videoUrl = state.videoUrl,
                    subtitleTracks = state.tracks,
                    headers = state.headers
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(
    videoUrl: String,
    subtitleTracks: List<Track>,
    headers: Map<String, String>
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(videoUrl, subtitleTracks) {
        android.util.Log.d("ExoPlayerView", "Loading video URL: $videoUrl")
        android.util.Log.d("ExoPlayerView", "Subtitle tracks: ${subtitleTracks.size}")
        android.util.Log.d("ExoPlayerView", "Headers: $headers")
        
        // Create HTTP data source factory
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(headers)
        
        // Create media item builder
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoUrl)
        
        // Add subtitle tracks
        if (subtitleTracks.isNotEmpty()) {
            val subtitleConfigurations = subtitleTracks.map { track ->
                android.util.Log.d("ExoPlayerView", "Adding subtitle: ${track.label ?: track.kind} - ${track.file}")
                
                MediaItem.SubtitleConfiguration.Builder(android.net.Uri.parse(track.file))
                    .setMimeType(MimeTypes.TEXT_VTT)
                    .setLanguage(when {
                        track.label?.contains("English", ignoreCase = true) == true -> "en"
                        track.label?.contains("Japanese", ignoreCase = true) == true -> "ja"
                        else -> track.kind
                    })
                    .setLabel(track.label ?: track.kind)
                    .setSelectionFlags(if (track.default) androidx.media3.common.C.SELECTION_FLAG_DEFAULT else 0)
                    .build()
            }
            
            mediaItemBuilder.setSubtitleConfigurations(subtitleConfigurations)
        }
        
        val mediaItem = mediaItemBuilder.build()
        
        // Create HLS media source
        val mediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory)
            .createMediaSource(mediaItem)

        exoPlayer.setMediaSource(mediaSource)
        
        // Enable subtitle track by default
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage("en")
            .build()
        
        // Add error listener
        exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                super.onPlayerError(error)
                android.util.Log.e("VideoPlayer", "Player Error: ${error.message}", error)
            }
        })
        
        exoPlayer.prepare()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                // Enable subtitle button in player controls
                setShowSubtitleButton(true)
                setShowNextButton(false)
                setShowPreviousButton(false)
                
                // Handle fullscreen
                setFullscreenButtonClickListener { isFullScreen ->
                    android.util.Log.d("VideoPlayer", "Fullscreen: $isFullScreen")
                    
                    activity?.let { act ->
                        if (isFullScreen) {
                            act.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            act.window.decorView.systemUiVisibility = (
                                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            )
                        } else {
                            act.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            act.window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_VISIBLE
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )



}


