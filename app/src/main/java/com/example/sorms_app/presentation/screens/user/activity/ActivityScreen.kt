package com.example.sorms_app.presentation.screens.user.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Notification
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(viewModel: ActivityViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lịch sử hoạt động") })
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải lịch sử hoạt động.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.activities.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có hoạt động",
                    subtitle = "Lịch sử hoạt động của bạn sẽ được hiển thị ở đây.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                ActivityList(activities = uiState.activities, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun ActivityList(activities: List<Notification>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(activities) { activity ->
            ActivityCard(activity = activity)
        }
    }
}

@Composable
private fun ActivityCard(activity: Notification) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    SormsCard {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle, // Using a default icon for now
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = activity.message, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormatter.format(activity.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
