package com.example.sorms_app.presentation.screens.user.services

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Service
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: ServiceViewModel = hiltViewModel(),
    onServiceClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dịch vụ") })
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải danh sách dịch vụ.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.services.isEmpty() -> {
                SormsEmptyState(
                    title = "Không có dịch vụ",
                    subtitle = "Hiện tại không có dịch vụ nào.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                ServiceGrid(
                    services = uiState.services,
                    onServiceClick = onServiceClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ServiceGrid(
    services: List<Service>,
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(services) { service ->
            ServiceItem(service = service, onClick = { onServiceClick(service.id) })
        }
    }
}

@Composable
private fun ServiceItem(service: Service, onClick: () -> Unit) {
    SormsCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.name,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
