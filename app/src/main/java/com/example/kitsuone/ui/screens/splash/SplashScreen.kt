package com.example.kitsuone.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitsuone.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToHome()
    }

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animations")
    
    // Shimmer/glow effect
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )
    
    // Particle floating animation
    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    // Title entrance animation
    val titleScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "title_scale"
    )
    
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a0a2e), // Deep purple
                        Color(0xFF2d1b4e), // Purple
                        Color(0xFF4a1f6f), // Medium purple
                        Color(0xFF6b2d8a), // Lighter purple
                        Color(0xFF8b3a9c)  // Pink-purple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles background
        FloatingParticles(particleOffset)
        
        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // App logo/icon with glow effect
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(titleScale)
                    .alpha(titleAlpha)
                    .drawBehind {
                        // Glow effect
                        val glowColor = Color(0xFFFF6EC7).copy(alpha = shimmerAlpha * 0.5f)
                        drawCircle(
                            color = glowColor,
                            radius = size.minDimension / 1.5f
                        )
                    }
            ) {
                // Logo placeholder with gradient circle
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name with shimmer effect
            Text(
                text = "KitsuOne",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color(0xFFFF6EC7).copy(alpha = shimmerAlpha),
                        offset = Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier
                    .scale(titleScale)
                    .alpha(titleAlpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = "Your Anime Journey Begins",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f * titleAlpha),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )
            
            Spacer(modifier = Modifier.weight(1.5f))
            
            // Loading indicator
            LoadingDots(shimmerAlpha)
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun FloatingParticles(animationProgress: Float) {
    val particles = remember {
        List(20) {
            Particle(
                x = (0..100).random().toFloat(),
                y = (0..100).random().toFloat(),
                size = (2..8).random().toFloat(),
                speed = (30..80).random() / 100f
            )
        }
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val progress = (animationProgress * particle.speed) % 1f
            val x = size.width * (particle.x / 100f)
            val y = size.height * (1f - progress)
            
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun LoadingDots(shimmerAlpha: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(shimmerAlpha)
                    .background(
                        color = Color(0xFFFF6EC7),
                        shape = CircleShape
                    )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)
