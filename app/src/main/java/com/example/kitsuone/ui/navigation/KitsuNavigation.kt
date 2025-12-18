package com.example.kitsuone.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
// Imports needed
import com.example.kitsuone.ui.screens.auth.LoginScreen
import com.example.kitsuone.ui.screens.auth.SignupScreen
import com.example.kitsuone.KitsuOneApplication
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kitsuone.ui.screens.details.DetailsScreen
import com.example.kitsuone.ui.screens.home.HomeScreen
import com.example.kitsuone.ui.screens.player.PlayerScreen
import com.example.kitsuone.ui.screens.profile.ProfileScreen
import com.example.kitsuone.ui.screens.search.SearchScreen
import com.example.kitsuone.ui.screens.watchlist.WatchlistScreen

object KitsuDestinations {
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val HOME_ROUTE = "home"
    const val SEARCH_ROUTE = "search"
    const val WATCHLIST_ROUTE = "watchlist"
    const val PROFILE_ROUTE = "profile"
    const val SCHEDULE_ROUTE = "schedule"
    const val ANIME_LIST_ROUTE = "anime_list/{type}/{query}"
    const val DETAILS_ROUTE = "details/{animeId}"
    const val PLAYER_ROUTE = "player/{episodeId}"
}

// Routes that should show bottom nav
val bottomNavRoutes = listOf(
    KitsuDestinations.HOME_ROUTE,
    KitsuDestinations.SEARCH_ROUTE,
    KitsuDestinations.SCHEDULE_ROUTE,
    KitsuDestinations.WATCHLIST_ROUTE,
    KitsuDestinations.PROFILE_ROUTE
)

@Composable
fun KitsuApp(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute in bottomNavRoutes
    
    // Get FirebaseAuth instance from AppContainer
    val context = LocalContext.current
    val application = context.applicationContext as KitsuOneApplication
    val firebaseAuth = application.container.firebaseAuth
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(KitsuDestinations.HOME_ROUTE) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = KitsuDestinations.SPLASH_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Splash screen
            composable(KitsuDestinations.SPLASH_ROUTE) {
                com.example.kitsuone.ui.screens.splash.SplashScreen(
                    onNavigateToHome = {
                        // Check auth status
                        if (firebaseAuth.currentUser != null) {
                            navController.navigate(KitsuDestinations.HOME_ROUTE) {
                                popUpTo(KitsuDestinations.SPLASH_ROUTE) { inclusive = true }
                            }
                        } else {
                            navController.navigate(KitsuDestinations.LOGIN_ROUTE) {
                                popUpTo(KitsuDestinations.SPLASH_ROUTE) { inclusive = true }
                            }
                        }
                    }
                )
            }
            
            // Login Screen
            composable(KitsuDestinations.LOGIN_ROUTE) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(KitsuDestinations.HOME_ROUTE) {
                            popUpTo(KitsuDestinations.LOGIN_ROUTE) { inclusive = true }
                        }
                    },
                    onNavigateToSignup = {
                        navController.navigate(KitsuDestinations.SIGNUP_ROUTE)
                    },
                    firebaseAuth = firebaseAuth
                )
            }
            
            // Signup Screen
            composable(KitsuDestinations.SIGNUP_ROUTE) {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate(KitsuDestinations.HOME_ROUTE) {
                            popUpTo(KitsuDestinations.SIGNUP_ROUTE) { inclusive = true }
                            // Also pop login if it was in stack
                            popUpTo(KitsuDestinations.LOGIN_ROUTE) { inclusive = true } 
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(KitsuDestinations.LOGIN_ROUTE) {
                            popUpTo(KitsuDestinations.SIGNUP_ROUTE) { inclusive = true }
                        }
                    },
                    firebaseAuth = firebaseAuth
                )
            }
            
            // Bottom nav destinations
            composable(KitsuDestinations.HOME_ROUTE) {
                HomeScreen(
                    onAnimeClick = { animeId ->
                        navController.navigate("details/$animeId")
                    },
                    onSearchClick = {
                        navController.navigate(KitsuDestinations.SEARCH_ROUTE)
                    },
                    onBrowseClick = { type, query ->
                        navController.navigate("anime_list/$type/$query")
                    }
                )
            }
            
            composable(KitsuDestinations.SEARCH_ROUTE) {
                SearchScreen(
                    onBackClick = { }, // No back on bottom nav screen
                    onAnimeClick = { animeId ->
                        navController.navigate("details/$animeId")
                    }
                )
            }
            
            composable(KitsuDestinations.WATCHLIST_ROUTE) {
                WatchlistScreen(
                    onAnimeClick = { animeId ->
                        navController.navigate("details/$animeId")
                    }
                )
            }
            
            composable(KitsuDestinations.SCHEDULE_ROUTE) {
                com.example.kitsuone.ui.screens.schedule.ScheduleScreen(
                    onAnimeClick = { animeId ->
                        navController.navigate("details/$animeId")
                    }
                )
            }

            composable(KitsuDestinations.PROFILE_ROUTE) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(KitsuDestinations.LOGIN_ROUTE) {
                            popUpTo(KitsuDestinations.HOME_ROUTE) { inclusive = true }
                        }
                    }
                )
            }
            
            // Detail screens (no bottom nav)
            composable(
                route = KitsuDestinations.DETAILS_ROUTE,
                arguments = listOf(navArgument("animeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val animeId = backStackEntry.arguments?.getString("animeId") ?: return@composable
                DetailsScreen(
                    animeId = animeId,
                    onBackClick = { navController.popBackStack() },
                    onEpisodeClick = { episodeId ->
                        android.util.Log.d("Navigation", "Episode click received, episodeId: $episodeId")
                        try {
                            val encodedId = java.net.URLEncoder.encode(episodeId, "UTF-8").replace("+", "%20")
                            android.util.Log.d("Navigation", "Encoded episodeId: $encodedId")
                            navController.navigate("player/$encodedId")
                        } catch (e: Exception) {
                            android.util.Log.e("Navigation", "Error encoding episodeId: $episodeId", e)
                            // Fallback: try without encoding
                            navController.navigate("player/$episodeId")
                        }
                    },
                    onAnimeClick = { id ->
                        navController.navigate("details/$id")
                    }
                )
            }
            
            composable(
                route = KitsuDestinations.PLAYER_ROUTE,
                arguments = listOf(navArgument("episodeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val encodedEpisodeId = backStackEntry.arguments?.getString("episodeId") ?: return@composable
                android.util.Log.d("Navigation", "Received encoded episodeId: $encodedEpisodeId")
                
                // Try to decode, but fall back to original if it fails
                val episodeId = try {
                    val decoded = java.net.URLDecoder.decode(encodedEpisodeId, "UTF-8")
                    android.util.Log.d("Navigation", "Decoded episodeId: $decoded")
                    decoded
                } catch (e: Exception) {
                    android.util.Log.e("Navigation", "Error decoding episodeId: $encodedEpisodeId", e)
                    // Fallback to original
                    encodedEpisodeId
                }
                
                PlayerScreen(
                    episodeId = episodeId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            
            composable(
                route = KitsuDestinations.ANIME_LIST_ROUTE,
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("query") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: return@composable
                val query = backStackEntry.arguments?.getString("query") ?: return@composable
                
                com.example.kitsuone.ui.screens.explore.AnimeListScreen(
                    type = type,
                    query = query,
                    onBackClick = { navController.popBackStack() },
                    onAnimeClick = { animeId ->
                        navController.navigate("details/$animeId")
                    }
                )
            }
        }
    }
}
