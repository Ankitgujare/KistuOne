package com.example.kitsuone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kitsuone.ui.navigation.KitsuApp
import com.example.kitsuone.ui.theme.KitsuOneTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: com.example.kitsuone.ui.theme.ThemeViewModel = 
                androidx.lifecycle.viewmodel.compose.viewModel(factory = com.example.kitsuone.ui.theme.ThemeViewModel.Factory)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            
            KitsuOneTheme(darkTheme = isDarkTheme) {
                KitsuApp()
            }
        }
    }
}
