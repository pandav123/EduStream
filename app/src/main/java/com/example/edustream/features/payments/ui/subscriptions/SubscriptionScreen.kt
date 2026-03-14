package com.example.edustream.features.payments.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edustream.features.payments.data.local.entities.PlanType
import com.example.edustream.features.payments.data.local.entities.SubStatus
import com.example.edustream.ui.common.UiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subState by viewModel.subscription.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = subState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Success -> {
                    val sub = state.data
                    if (sub != null && sub.status == SubStatus.ACTIVE) {
                        ActiveSubscriptionCard(sub = sub)
                    } else {
                        SubscriptionPlans(onSelectPlan = viewModel::subscribe)
                    }
                }
                is UiState.Error -> Text("Error: ${state.message}")
            }
        }
    }
}

@Composable
fun ActiveSubscriptionCard(sub: com.example.edustream.features.payments.data.local.entities.SubscriptionEntity) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Active ${sub.planType} Plan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Valid until: ${sdf.format(Date(sub.endDate))}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = { /* TODO: Cancel */ }) {
                Text("Manage Subscription")
            }
        }
    }
}

@Composable
fun SubscriptionPlans(onSelectPlan: (PlanType) -> Unit) {
    Text("Choose your learning path", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(24.dp))
    
    PlanCard(
        title = "Monthly Plan",
        price = "₹499",
        features = listOf("Access to all courses", "HD Streaming"),
        onClick = { onSelectPlan(PlanType.MONTHLY) }
    )
    Spacer(modifier = Modifier.height(16.dp))
    PlanCard(
        title = "Annual Plan",
        price = "₹3,999",
        features = listOf("Access to all courses", "Offline Downloads", "Priority Support"),
        onClick = { onSelectPlan(PlanType.ANNUAL) }
    )
}

@Composable
fun PlanCard(title: String, price: String, features: List<String>, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(price, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            features.forEach { feature ->
                Text("• $feature", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text("Subscribe Now")
            }
        }
    }
}
