package com.autosync.main.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onAnimationEnd: () -> Unit
) {
    val scale = remember { Animatable(1.1f) }
    
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 2000, easing = androidx.compose.animation.core.LinearEasing)
            )
        }
        
        launch {
            for (i in 0..5) {
                shakeOffset.animateTo(2.5f, animationSpec = tween(50))
                shakeOffset.animateTo(-2.5f, animationSpec = tween(50))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(100))
        }

        delay(2000)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.autosync.main.R.drawable.splash_background),
            contentDescription = "Cinematic Drift Scene",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale.value)
                .offset(x = shakeOffset.value.dp, y = shakeOffset.value.dp)
        )
        

        val textAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(500)
            textAlpha.animateTo(1f, animationSpec = tween(1000))
        }

        Text(
            text = "AutoSync",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .alpha(textAlpha.value)
        )
    }
}
