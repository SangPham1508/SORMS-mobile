package com.example.sorms_app.presentation.screens.user.bookings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sorms_app.domain.model.Booking
import com.example.sorms_app.presentation.components.BadgeTone
import com.example.sorms_app.presentation.components.SormsBadge
import com.example.sorms_app.presentation.components.SormsCard
import com.example.sorms_app.presentation.components.SormsEmptyState
import com.example.sorms_app.presentation.components.SormsLoading
import com.example.sorms_app.presentation.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(viewModel: BookingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lịch sử đặt phòng") })
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SormsLoading(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                SormsEmptyState(
                    title = "Lỗi",
                    subtitle = uiState.errorMessage ?: "Không thể tải lịch sử đặt phòng.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            uiState.bookings.isEmpty() -> {
                SormsEmptyState(
                    title = "Chưa có đặt phòng",
                    subtitle = "Lịch sử đặt phòng của bạn sẽ được hiển thị ở đây.",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            else -> {
                BookingList(bookings = uiState.bookings, modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun BookingList(bookings: List<Booking>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(bookings) { booking ->
            BookingCard(booking = booking)
        }
    }
}

@Composable
private fun BookingCard(booking: Booking) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    SormsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${booking.roomName} - ${booking.buildingName}",
                    style = MaterialTheme.typography.titleMedium
                )
                SormsBadge(
                    text = booking.status.replace("_", " "),
                    tone = when (booking.status.uppercase()) {
                        "CHECKED_IN" -> BadgeTone.Success
                        "CONFIRMED" -> BadgeTone.Default
                        "COMPLETED" -> BadgeTone.Default
                        else -> BadgeTone.Warning
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Từ ${dateFormatter.format(booking.checkInDate)} đến ${dateFormatter.format(booking.checkOutDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
