package com.example.kitsuone.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitsuone.ui.theme.*


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ProfileViewModel.Factory),
    themeViewModel: ThemeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = ThemeViewModel.Factory),
    onLogout: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }
    
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                 com.example.kitsuone.util.NotificationUtils.showAnimeSuggestion(context, "One Piece")
            }
        }
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // ... (Header items remain)


        // Profile header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(DarkBrownLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user?.displayName?.take(1)?.uppercase() ?: "A",
                        style = MaterialTheme.typography.headlineLarge,
                        color = AccentRed
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.medium))
                
                Text(
                    text = user?.displayName ?: "Anime Fan",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextWhite
                )
                
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(Spacing.medium))

                Button(
                    onClick = { 
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Logout")
                }
            }
        }
        
        // Stats cards
        item {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextWhite
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                StatCard(
                    title = "Watching",
                    value = "0",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = "0",
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Settings section
        item {
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextWhite
            )
        }
        
        item {
            SettingsItem(
                icon = if (isDarkTheme) Icons.Default.Settings else Icons.Default.Person,
                title = if (isDarkTheme) "Dark Mode" else "Light Mode",
                onClick = { themeViewModel.toggleTheme() }
            )
        }

        item {
             SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Get Anime Suggestion",
                onClick = {
                    if (hasNotificationPermission) {
                        com.example.kitsuone.util.NotificationUtils.showAnimeSuggestion(context, "Demon Slayer")
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            )
        }
        
        item {
            SettingsItem(
                icon = if (isDarkTheme) Icons.Default.Settings else Icons.Default.Person,
                title = if (isDarkTheme) "Dark Mode" else "Light Mode",
                onClick = { themeViewModel.toggleTheme() }
            )
        }

        item {
            SettingsItem(
                icon = Icons.Default.Clear,
                title = "Clear Cache",
                onClick = { }
            )
        }
        
        item {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About",
                onClick = { }
            )
        }
        
        // Version info
        item {
            Spacer(modifier = Modifier.height(Spacing.medium))
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = AccentRed
            )
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AccentRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.medium))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhite
            )
        }
    }
}
