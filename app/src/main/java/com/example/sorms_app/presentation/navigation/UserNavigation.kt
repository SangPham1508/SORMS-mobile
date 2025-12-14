package com.example.sorms_app.presentation.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

enum class UserTab(val label: String) {
    ROOMS("Rooms"),
    BOOKINGS("Bookings"),
    SERVICES("Services"),
    PAYMENTS("Payments"),
    PROFILE("Profile")
}

@Composable
fun UserBottomBar(
    current: UserTab,
    onSelect: (UserTab) -> Unit
) {
    NavigationBar {
        UserTab.values().forEach { tab ->
            NavigationBarItem(
                selected = current == tab,
                onClick = { onSelect(tab) },
                icon = { Text(tab.label.first().toString()) },
                label = { Text(tab.label) }
            )
        }
    }
}


