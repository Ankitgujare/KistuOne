package com.example.kitsuone.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitsuone.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // Auto-navigate after 2.5 seconds (slightly faster for minimal feel)
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToHome()
    }

    // Single unified animation controller
    val transitionState = remember { MutableTransitionState(false) }
    transitionState.targetState = true

    val transition = updateTransition(transitionState, label = "splashTransition")

    // Logo Scale Animation
    val logoScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        },
        label = "logoScale"
    ) { state ->
        if (state) 1f else 0.8f
    }

    // Logo Alpha Animation
    val logoAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 800)
        },
        label = "logoAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    // Text Slide/Fade Animation
    val textAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 800, delayMillis = 300)
        },
        label = "textAlpha"
    ) { state ->
        if (state) 1f else 0f
    }
    
    val textOffset by transition.animateDp(
        transitionSpec = {
             tween(durationMillis = 1000, delayMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "textOffset"
    ) { state ->
        if (state) 0.dp else 20.dp
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)), // Deep minimal dark
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(140.dp) // Slightly larger
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "KitsuOne Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = "KitsuOne",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset(y = textOffset)
            )
        }
    }
}
