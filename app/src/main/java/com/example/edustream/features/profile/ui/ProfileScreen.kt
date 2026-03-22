package com.example.edustream.features.profile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToMyCourses: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    when (isAdmin) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        true -> {
            AdminProfileScreen(
                name = user?.displayName ?: "Admin",
                email = user?.email ?: "",
                onBackClick = onBackClick,
                onNavigateToAdmin = onNavigateToAdmin,
                onNavigateToSettings = onNavigateToSettings,
                onLogout = {
                    viewModel.logout()
                    onLogout()
                }
            )
        }
        false -> {
            UserProfileScreen(
                name = user?.displayName ?: "User",
                email = user?.email ?: "",
                onBackClick = onBackClick,
                onNavigateToMyCourses = onNavigateToMyCourses,
                onNavigateToSubscriptions = onNavigateToSubscriptions,
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToCertificates = onNavigateToCertificates,
                onNavigateToSettings = onNavigateToSettings,
                onLogout = {
                    viewModel.logout()
                    onLogout()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    name: String,
    email: String,
    onBackClick: () -> Unit,
    onNavigateToMyCourses: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ProfileHeader(name = name, email = email, isAdmin = false)
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.LibraryBooks,
                    title = "My Courses",
                    onClick = onNavigateToMyCourses
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Default.CardMembership,
                    title = "Subscriptions",
                    onClick = onNavigateToSubscriptions
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Default.History,
                    title = "Payment History",
                    onClick = onNavigateToHistory
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Default.WorkspacePremium,
                    title = "Certificates",
                    onClick = onNavigateToCertificates
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    name: String,
    email: String,
    onBackClick: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                ProfileHeader(name = name, email = email, isAdmin = true)
            }

            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Admin Dashboard",
                    onClick = onNavigateToAdmin
                )
            }
            item {
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "General Settings",
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String, isAdmin: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = if (isAdmin) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (isAdmin) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (isAdmin) {
                Spacer(modifier = Modifier.width(8.dp))
                SuggestionChip(
                    onClick = { },
                    label = { Text("Admin") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, contentDescription = null) },
        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        modifier = Modifier.clickable { onClick() }
    )
}
