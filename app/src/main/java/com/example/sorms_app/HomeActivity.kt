package com.example.sorms_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sorms_app.data.model.RoomData
import com.example.sorms_app.viewmodel.RoomViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sorms_app.ui.theme.SORMS_appTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SORMS_appTheme {
                val context = LocalContext.current
                var selectedTab by remember { mutableStateOf(HomeTab.HOME) }
                val userName = intent.getStringExtra("userName") ?: "Người dùng"
                val userEmail = intent.getStringExtra("userEmail") ?: ""
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        HomeBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { tab ->
                                selectedTab = tab
                            }
                        )
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        HomeTab.HOME -> {
                            HomeScreen(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .background(Color(0xFFF4F5F8)),
                                userName = userName,
                                onNotificationClick = {
                                    selectedTab = HomeTab.NOTIFICATIONS
                                },
                                onCheckAvailability = {
                                    selectedTab = HomeTab.ROOM_LIST
                                },
                                onRuleClick = {
                                    selectedTab = HomeTab.RULES
                                },
                                onContactClick = {
                                    selectedTab = HomeTab.CONTACT_MANAGER
                                },
                                onTripCardClick = {
                                    selectedTab = HomeTab.BOOKING_DETAIL
                                },
                                onViewAllHistoryClick = {
                                    Toast.makeText(context, "Xem tất cả lịch sử", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        HomeTab.ROOM_LIST -> {
                            RoomListScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { selectedTab = HomeTab.HOME },
                                onBookRoom = { selectedTab = HomeTab.BOOKING }
                            )
                        }
                        HomeTab.BOOKING -> {
                            BookingScreen(
                                modifier = Modifier.padding(innerPadding),
                                userName = userName,
                                userEmail = userEmail,
                                onBackClick = { selectedTab = HomeTab.HOME },
                                onBookRoom = { location, checkIn, checkOut, roomType ->
                                    Toast.makeText(
                                        context,
                                        "Đặt phòng $roomType tại $location từ $checkIn đến $checkOut",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                        HomeTab.SERVICES -> {
                            ServiceOrderScreen(
                                modifier = Modifier.padding(innerPadding),
                                userName = userName,
                                userEmail = userEmail,
                                onBackClick = { selectedTab = HomeTab.HOME }
                            )
                        }
                        HomeTab.NOTIFICATIONS -> {
                            NotificationScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { selectedTab = HomeTab.HOME }
                            )
                        }
                        HomeTab.ACCOUNT -> {
                            AccountScreen(
                                modifier = Modifier.padding(innerPadding),
                                userName = userName,
                                userEmail = userEmail,
                                onLogout = {
                                    // Đăng xuất Google Sign-In
                                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestEmail()
                                        .build()
                                    val googleSignInClient = GoogleSignIn.getClient(this@HomeActivity, gso)
                                    googleSignInClient.signOut().addOnCompleteListener {
                                        Toast.makeText(this@HomeActivity, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            )
                        }
                        HomeTab.RULES -> {
                            RulesScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { selectedTab = HomeTab.HOME }
                            )
                        }
                        HomeTab.CONTACT_MANAGER -> {
                            ContactManagerScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { selectedTab = HomeTab.HOME }
                            )
                        }
                        HomeTab.BOOKING_DETAIL -> {
                            BookingDetailScreen(
                                modifier = Modifier.padding(innerPadding),
                                onBackClick = { selectedTab = HomeTab.HOME }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    onNotificationClick: () -> Unit = {},
    onCheckAvailability: () -> Unit = {},
    onRuleClick: () -> Unit = {},
    onContactClick: () -> Unit = {},
    onTripCardClick: () -> Unit = {},
    onViewAllHistoryClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HomeHeader(
            userName = userName,
            onNotificationClick = onNotificationClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        HeroCard(onCheckAvailability = onCheckAvailability)
        Spacer(modifier = Modifier.height(24.dp))
        UsefulInfoSection(
            onRuleClick = onRuleClick,
            onContactClick = onContactClick
        )
        Spacer(modifier = Modifier.height(24.dp))
        TripHistorySection(
            onTripCardClick = onTripCardClick,
            onViewAllHistoryClick = onViewAllHistoryClick
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercase() ?: "U",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Xin chào, $userName",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Thông báo",
            tint = Color(0xFF111827),
            modifier = Modifier
                .size(22.dp)
                .clickable { onNotificationClick() }
        )
    }
}

@Composable
private fun HeroCard(
    onCheckAvailability: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1D4ED8),
                                Color(0xFF7C3AED)
                            )
                        )
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sorm_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Nhà công vụ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9FAFB)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ĐỊA ĐIỂM CÔNG TÁC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Đại học FPT Quy Nhơn",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFFF7A1A))
                                .clickable { onCheckAvailability() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Kiểm tra phòng trống",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsefulInfoSection(
    onRuleClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Text(
        text = "Thông tin hữu ích",
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onRuleClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Quy định ở",
                    tint = Color(0xFF0284C7)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Quy định ở",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onContactClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Liên hệ quản lý",
                    tint = Color(0xFF16A34A)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Liên hệ quản lý",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun TripHistorySection(
    onTripCardClick: () -> Unit,
    onViewAllHistoryClick: () -> Unit
) {
    Text(
        text = "Lịch sử công tác",
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row {
        Text(
            text = "Đang ở",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF111827)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Đã hoàn thành",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTripCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sorm_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Phòng đơn C2-305",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "15/11 - 18/11 • 3 đêm • Công tác giảng dạy",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE0F2FE))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Đã xác nhận",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF0284C7)
                        )
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = "Xem tất cả lịch sử",
        style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF2563EB),
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewAllHistoryClick() }
    )
}

@Composable
private fun HomeBottomBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomBarItem(
                text = HomeTab.HOME.label,
                selected = selectedTab == HomeTab.HOME,
                onClick = { onTabSelected(HomeTab.HOME) }
            )
            BottomBarItem(
                text = HomeTab.BOOKING.label,
                selected = selectedTab == HomeTab.BOOKING,
                onClick = { onTabSelected(HomeTab.BOOKING) }
            )
            BottomBarItem(
                text = HomeTab.SERVICES.label,
                selected = selectedTab == HomeTab.SERVICES,
                onClick = { onTabSelected(HomeTab.SERVICES) }
            )
            BottomBarItem(
                text = HomeTab.ACCOUNT.label,
                selected = selectedTab == HomeTab.ACCOUNT,
                onClick = { onTabSelected(HomeTab.ACCOUNT) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (selected) Color(0xFF111827) else Color.Transparent
                )
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (selected) Color(0xFF111827) else Color.Gray,
                fontSize = 11.sp
            ),
            modifier = Modifier.clickable { onClick() }
        )
    }
}

private enum class HomeTab(val label: String) {
    HOME("Trang chủ"),
    ROOM_LIST("Danh sách phòng"),
    BOOKING("Đặt phòng"),
    SERVICES("Order dịch vụ"),
    NOTIFICATIONS("Thông báo"),
    ACCOUNT("Tài khoản"),
    RULES("Quy định ở"),
    CONTACT_MANAGER("Liên hệ quản lý"),
    BOOKING_DETAIL("Chi tiết booking")
}

@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    userName: String = "",
    userEmail: String = "",
    onBackClick: () -> Unit = {},
    onBookRoom: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    var customerName by remember { mutableStateOf(userName) }
    var customerEmail by remember { mutableStateOf(userEmail) }
    var customerPhone by remember { mutableStateOf("") }
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }
    var numberOfGuests by remember { mutableStateOf("1") }
    var purpose by remember { mutableStateOf("") }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF111827),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Đặt phòng mới",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Thông tin khách hàng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Đã tự động điền",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF10B981)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Tên khách hàng",
                value = customerName,
                onValueChange = { customerName = it },
                placeholder = "Nhập tên khách hàng",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Email",
                value = customerEmail,
                onValueChange = { customerEmail = it },
                placeholder = "Nhập email",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Số điện thoại",
                value = customerPhone,
                onValueChange = { customerPhone = it },
                placeholder = "Nhập số điện thoại",
                isRequired = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Thông tin đặt phòng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Check-in",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF374151)
                        )
                    )
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Red
                        ),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        calendar.set(year, month, day)
                                        checkInDate = dateFormat.format(calendar.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = checkInDate.ifEmpty { "mm/dd/yyyy" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (checkInDate.isEmpty()) Color.Gray else Color.Black
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Check-out",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF374151)
                        )
                    )
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Red
                        ),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        calendar.set(year, month, day)
                                        checkOutDate = dateFormat.format(calendar.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = checkOutDate.ifEmpty { "mm/dd/yyyy" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (checkOutDate.isEmpty()) Color.Gray else Color.Black
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Số khách",
                value = numberOfGuests,
                onValueChange = { numberOfGuests = it },
                placeholder = "1",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Mục đích sử dụng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = purpose,
                    onValueChange = { purpose = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxSize()
                )
                if (purpose.isEmpty()) {
                    Text(
                        text = "Nhập mục đích sử dụng",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hủy",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF374151),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1D4ED8))
                        .clickable {
                            if (customerName.isNotEmpty() && customerEmail.isNotEmpty() &&
                                customerPhone.isNotEmpty() && checkInDate.isNotEmpty() &&
                                checkOutDate.isNotEmpty() && numberOfGuests.isNotEmpty()
                            ) {
                                Toast
                                    .makeText(
                                        context,
                                        "Gửi yêu cầu đặt phòng thành công!\nCheck-in: $checkInDate\nCheck-out: $checkOutDate",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                onBackClick()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Vui lòng điền đầy đủ thông tin bắt buộc",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gửi yêu cầu đặt phòng",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class Room(
    val number: String,
    val type: String,
    val isAvailable: Boolean,
    val floor: String,
    val capacity: String
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    SUCCESS, INFO, WARNING
}

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val price: String
)

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val notifications = remember {
        listOf(
            Notification(
                "1",
                "Đặt phòng thành công",
                "Phòng C2-301 đã được đặt thành công. Check-in: 15/12/2025, Check-out: 18/12/2025",
                "2 giờ trước",
                NotificationType.SUCCESS,
                false
            ),
            Notification(
                "2",
                "Order dịch vụ thành công",
                "Yêu cầu dịch vụ Giặt ủi và Ăn sáng đã được tiếp nhận. Nhân viên sẽ liên hệ bạn sớm.",
                "5 giờ trước",
                NotificationType.SUCCESS,
                false
            ),
            Notification(
                "3",
                "Thông báo thanh toán",
                "Vui lòng thanh toán tiền phòng tháng 12/2025 trước ngày 05/12/2025",
                "1 ngày trước",
                NotificationType.WARNING,
                true
            ),
            Notification(
                "4",
                "Thông báo bảo trì",
                "Hệ thống điện tầng 3 sẽ bảo trì vào 08:00 - 12:00 ngày 06/12/2025",
                "2 ngày trước",
                NotificationType.INFO,
                true
            ),
            Notification(
                "5",
                "Đặt phòng thành công",
                "Phòng C2-404 đã được đặt thành công. Check-in: 10/12/2025, Check-out: 12/12/2025",
                "3 ngày trước",
                NotificationType.SUCCESS,
                true
            ),
            Notification(
                "6",
                "Quy định mới",
                "Cập nhật quy định về giờ giấc ra vào. Vui lòng xem chi tiết trong mục Quy định ở.",
                "1 tuần trước",
                NotificationType.INFO,
                true
            )
        )
    }
    
    val unreadCount = notifications.count { !it.isRead }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF111827),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )
                    if (unreadCount > 0) {
                        Text(
                            text = "$unreadCount thông báo chưa đọc",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFEF4444)
                            )
                        )
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
        )
        
        // Notifications list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            notifications.forEach { notification ->
                NotificationItem(notification = notification)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF0F9FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Icon theo loại thông báo
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            NotificationType.SUCCESS -> Color(0xFFDCFCE7)
                            NotificationType.INFO -> Color(0xFFDEEDFF)
                            NotificationType.WARNING -> Color(0xFFFEF3C7)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (notification.type) {
                        NotificationType.SUCCESS -> "✓"
                        NotificationType.INFO -> "ℹ"
                        NotificationType.WARNING -> "⚠"
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = when (notification.type) {
                            NotificationType.SUCCESS -> Color(0xFF16A34A)
                            NotificationType.INFO -> Color(0xFF2563EB)
                            NotificationType.WARNING -> Color(0xFFEAB308)
                        },
                        fontSize = 24.sp
                    )
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF6B7280),
                        lineHeight = 18.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = notification.time,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun RoomListScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBookRoom: () -> Unit = {},
    viewModel: RoomViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    val availableRooms = uiState.availableRooms
    val occupiedRooms = uiState.occupiedRooms
    val selectedRoomNumber = uiState.selectedRoomNumber
    val isLoading = uiState.isLoading
    val errorMessage = uiState.errorMessage

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF111827),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Danh sách phòng",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )
                    Text(
                        text = "${availableRooms.size} phòng còn trống",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF10B981)
                        )
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
        )
        
        // Content
        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> {
                    // Loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = Color(0xFFFF7A1A)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang tải danh sách phòng...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF6B7280)
                                )
                            )
                        }
                    }
                }
                errorMessage != null -> {
                    // Error message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFDC2626),
                                    textAlign = TextAlign.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFF7A1A))
                                    .clickable { viewModel.refresh() }
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Thử lại",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Phòng còn trống
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phòng còn trống (${availableRooms.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF111827)
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        availableRooms.forEach { room ->
                            RoomCardData(
                                room = room,
                                isSelected = selectedRoomNumber == room.number,
                                onClick = { viewModel.selectRoom(room.number) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Phòng đã đầy
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Phòng đã đầy (${occupiedRooms.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF111827)
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        occupiedRooms.forEach { room ->
                            RoomCardData(
                                room = room,
                                isSelected = false,
                                onClick = {
                                    Toast.makeText(context, "Phòng ${room.number} đã đầy", Toast.LENGTH_SHORT).show()
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        
        // Bottom button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selectedRoomNumber != null) Color(0xFFFF7A1A) else Color(0xFFD1D5DB)
                    )
                    .clickable(enabled = selectedRoomNumber != null) {
                        if (selectedRoomNumber != null) {
                            onBookRoom()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedRoomNumber != null) {
                        "Đặt phòng $selectedRoomNumber"
                    } else {
                        "Chọn phòng để đặt"
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun RoomCard(
    room: Room,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = room.isAvailable) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color(0xFFEEF2FF)
                !room.isAvailable -> Color(0xFFF9FAFB)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1D4ED8))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon phòng
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏠",
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Thông tin phòng
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.number,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF111827)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.type,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF6B7280)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = room.floor,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                    Text(
                        text = room.capacity,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Badge trạng thái
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (room.isAvailable) "Còn trống" else "Đã đầy",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (room.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * RoomCard cho RoomData (từ API)
 */
@Composable
fun RoomCardData(
    room: RoomData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = room.isAvailable) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color(0xFFEEF2FF)
                !room.isAvailable -> Color(0xFFF9FAFB)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1D4ED8))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon phòng
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏠",
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Thông tin phòng
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.number,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF111827)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.type,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF6B7280)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = room.floor,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                    Text(
                        text = room.capacity,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Badge trạng thái
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (room.isAvailable) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (room.isAvailable) "Còn trống" else "Đã đầy",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (room.isAvailable) Color(0xFF16A34A) else Color(0xFFDC2626),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun ServiceOrderScreen(
    modifier: Modifier = Modifier,
    userName: String = "",
    userEmail: String = "",
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    
    var customerName by remember { mutableStateOf(userName) }
    var customerEmail by remember { mutableStateOf(userEmail) }
    var customerPhone by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // Danh sách dịch vụ
    val services = remember {
        listOf(
            Service("1", "Dọn phòng", "Dịch vụ dọn dẹp phòng hàng ngày", "Miễn phí"),
            Service("2", "Giặt ủi", "Giặt ủi quần áo, trả trong 24h", "50.000đ/kg"),
            Service("3", "Ăn sáng", "Buffet sáng tại nhà ăn chung", "30.000đ/suất"),
            Service("4", "Đưa đón sân bay", "Xe đưa đón sân bay Nội Bài", "200.000đ/chuyến"),
            Service("5", "In ấn tài liệu", "In, photocopy tài liệu", "2.000đ/trang"),
            Service("6", "Sửa chữa điện nước", "Sửa chữa các thiết bị trong phòng", "Miễn phí")
        )
    }
    
    var selectedServices by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF111827),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Order dịch vụ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Thông tin khách hàng
            Text(
                text = "Thông tin khách hàng",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Đã tự động điền",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF10B981)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Tên khách hàng",
                value = customerName,
                onValueChange = { customerName = it },
                placeholder = "Nhập tên khách hàng",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Email",
                value = customerEmail,
                onValueChange = { customerEmail = it },
                placeholder = "Nhập email",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Số điện thoại",
                value = customerPhone,
                onValueChange = { customerPhone = it },
                placeholder = "Nhập số điện thoại",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BookingTextField(
                label = "Số phòng",
                value = roomNumber,
                onValueChange = { roomNumber = it },
                placeholder = "Nhập số phòng (VD: C2-301)",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Danh sách dịch vụ
            Text(
                text = "Chọn dịch vụ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            services.forEach { service ->
                ServiceItem(
                    service = service,
                    isSelected = selectedServices.contains(service.id),
                    onToggle = {
                        selectedServices = if (selectedServices.contains(service.id)) {
                            selectedServices - service.id
                        } else {
                            selectedServices + service.id
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Ghi chú
            Text(
                text = "Ghi chú thêm",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF374151)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxSize()
                )
                if (notes.isEmpty()) {
                    Text(
                        text = "Nhập ghi chú (VD: thời gian cần dịch vụ, yêu cầu đặc biệt...)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hủy",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF374151),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1D4ED8))
                        .clickable {
                            if (customerName.isNotEmpty() && customerEmail.isNotEmpty() &&
                                customerPhone.isNotEmpty() && roomNumber.isNotEmpty() &&
                                selectedServices.isNotEmpty()
                            ) {
                                val serviceNames = services
                                    .filter { selectedServices.contains(it.id) }
                                    .joinToString(", ") { it.name }
                                Toast
                                    .makeText(
                                        context,
                                        "Đặt dịch vụ thành công!\nPhòng: $roomNumber\nDịch vụ: $serviceNames",
                                        Toast.LENGTH_LONG
                                    )
                                    .show()
                                onBackClick()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "Vui lòng điền đầy đủ thông tin và chọn ít nhất 1 dịch vụ",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gửi yêu cầu",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ServiceItem(
    service: Service,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF9FAFB))
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFFD1D5DB),
                    shape = RoundedCornerShape(4.dp)
                )
                .background(if (isSelected) Color(0xFF1D4ED8) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color(0xFF1D4ED8) else Color.Black
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = service.price,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF374151)
            )
        )
    }
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    userName: String = "",
    userEmail: String = "",
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
            .verticalScroll(rememberScrollState())
    ) {
        // Header với gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1D4ED8),
                            Color(0xFF7C3AED)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D4ED8),
                            fontSize = 36.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Thông tin tài khoản
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Thông tin tài khoản",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AccountInfoRow(
                        icon = Icons.Default.Person,
                        label = "Họ và tên",
                        value = userName
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    AccountInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = userEmail
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    AccountInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Số điện thoại",
                        value = "Chưa cập nhật"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Cài đặt
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem(
                        icon = Icons.Default.Info,
                        title = "Giới thiệu ứng dụng",
                        onClick = { /* TODO */ }
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E7EB))
                            .padding(vertical = 8.dp)
                    )
                    
                    SettingItem(
                        icon = Icons.Default.Call,
                        title = "Liên hệ hỗ trợ",
                        onClick = { /* TODO */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Nút đăng xuất
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDC2626))
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Đăng xuất",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "SORMS App v1.0",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AccountInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF111827)
            ),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "›",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray
            )
        )
    }
}

@Composable
fun BookingTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isRequired: Boolean = false
) {
    Column {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF374151)
                )
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Red
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(8.dp))
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
            }
        }
        
        if (isRequired && value.isEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Bạn có thể chỉnh sửa nếu thông tin không chính xác",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Màn hình $title\n(Đang phát triển)",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SORMS_appTheme {
        Scaffold(
            bottomBar = {
                HomeBottomBar(
                    selectedTab = HomeTab.HOME,
                    onTabSelected = {}
                )
            }
        ) { inner ->
            HomeScreen(
                modifier = Modifier
                    .padding(inner)
                    .background(Color(0xFFF4F5F8)),
                userName = "Người dùng"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceOrderScreenPreview() {
    SORMS_appTheme {
        ServiceOrderScreen(
            userName = "Người dùng",
            userEmail = "minhuong@example.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountScreenPreview() {
    SORMS_appTheme {
        AccountScreen(
            userName = "Người dùng",
            userEmail = "minhuong@example.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomListScreenPreview() {
    SORMS_appTheme {
        RoomListScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    SORMS_appTheme {
        NotificationScreen()
    }
}

// Màn hình Nội quy
@Composable
fun RulesScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Quy định nhà công vụ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Giới thiệu
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📋 Nội quy chung",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Để đảm bảo môi trường sinh hoạt an toàn, văn minh, chuyên nghiệp, kính mong quý giảng viên tuân thủ các quy định sau:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7280),
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách nội quy
            val rules = listOf(
                RuleItem("🏠 Sử dụng phòng", "Chỉ sử dụng phòng đã được cấp phát. Không chuyển nhượng, cho thuê lại hoặc để người khác ở cùng."),
                RuleItem("👥 Khách thăm", "Khách đến thăm cần đăng ký tại bảo vệ. Giữ gìn trật tự, không gây ồn ảo sau 22:00."),
                RuleItem("🔥 An toàn PCCC", "Được phép sử dụng các thiết bị nấu nướng an toàn (bếp từ, nồi cơm điện). Nghiêm cấm bếp gas, bếp dầu. Không sử dụng nến, hương trong phòng."),
                RuleItem("🚭 Hút thuốc", "Chỉ được hút thuốc tại khu vực quy định (ban công, sân sau). Tuyệt đối không hút thuốc trong phòng."),
                RuleItem("🐕 Vật nuôi", "Không được nuôi động vật trong phòng để đảm bảo vệ sinh chung."),
                RuleItem("🔌 Điện nước", "Sử dụng tiết kiệm, hiệu quả. Tắt các thiết bị điện khi không sử dụng. Kiểm tra định kỳ thiết bị điện để tránh sự cố."),
                RuleItem("🧹 Vệ sinh", "Giữ gìn vệ sinh phòng ở và khu vực chung. Phân loại rác theo quy định. Tự dọn dẹp hoặc thuê dịch vụ vệ sinh."),
                RuleItem("🔑 Bảo mật", "Không làm chìa khóa phòng trái phép. Mất chìa khóa phải báo ngay ban quản lý trong vòng 24h."),
                RuleItem("💰 Thanh toán", "Thanh toán tiền phòng, điện nước đúng hạn trước ngày 05 hàng tháng. Quá hạn sẽ bị tính phí phạt."),
                RuleItem("📢 Thông báo", "Thường xuyên kiểm tra thông báo từ ban quản lý. Báo cáo kịp thời các sự cố về cơ sở vật chất.")
            )

            rules.forEach { rule ->
                RuleItemCard(rule = rule)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Xử lý vi phạm
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚠️ Xử lý vi phạm",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Vi phạm nhẹ: Nhắc nhở bằng văn bản\n• Vi phạm nghiêm trọng: Phạt tiền 500.000đ - 2.000.000đ tùy mức độ\n• Vi phạm nhiều lần hoặc rất nghiêm trọng: Thu hồi quyền sử dụng nhà công vụ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF991B1B),
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class RuleItem(
    val title: String,
    val description: String
)

@Composable
private fun RuleItemCard(rule: RuleItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = rule.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp
                )
            )
        }
    }
}

// Màn hình Liên hệ quản lý
@Composable
fun ContactManagerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Thông tin quản lý",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Ảnh và thông tin cơ bản
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0284C7),
                                        Color(0xFF0EA5E9)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Quản lý",
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ông Nguyễn Văn Quản",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Trưởng phòng Quản lý Nhà công vụ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7280)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin liên hệ
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📞 Thông tin liên hệ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContactInfoItem(
                        icon = Icons.Default.Phone,
                        label = "Điện thoại",
                        value = "024 3869 4242",
                        iconColor = Color(0xFF16A34A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ContactInfoItem(
                        icon = Icons.Default.Call,
                        label = "Di động",
                        value = "0912 345 678",
                        iconColor = Color(0xFF0284C7)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ContactInfoItem(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = "quanly.ktx@fpt.edu.vn",
                        iconColor = Color(0xFFDC2626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Giờ làm việc
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🕐 Giờ làm việc",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thứ 2 - Thứ 6:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                        )
                        Text(
                            text = "08:00 - 17:00",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Thứ 7 - Chủ nhật:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF374151)
                            )
                        )
                        Text(
                            text = "Nghỉ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Địa chỉ văn phòng
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📍 Địa chỉ văn phòng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Phòng 101, Tòa nhà C2\nKhu Nhà công vụ giảng viên\nFPT University, Khu Công nghệ cao Hòa Lạc\nHuyện Thạch Thất, Hà Nội",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF6B7280),
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lưu ý
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Lưu ý",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Trường hợp khẩn cấp ngoài giờ làm việc, vui lòng liên hệ bảo vệ tại số: 024 3869 5555",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF78350F),
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6B7280)
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
            )
        }
    }
}

// Màn hình Chi tiết Booking
@Composable
fun BookingDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F8))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3F4F6))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = Color(0xFF111827)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Chi tiết booking",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Ảnh và thông tin phòng
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // Ảnh phòng
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sorm_logo),
                            contentDescription = "Ảnh phòng",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                        
                        // Trạng thái
                        Box(
                            modifier = Modifier
                                .padding(12.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFE0F2FE))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Text(
                                text = "Đã xác nhận",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Thông tin cơ bản
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Phòng đơn C2-305",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "Tầng 3, Tòa nhà C2",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin booking
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📅 Thông tin đặt phòng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BookingInfoRow(
                        label = "Mã booking",
                        value = "BK20251115001"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookingInfoRow(
                        label = "Check-in",
                        value = "15/11/2025 - 14:00"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookingInfoRow(
                        label = "Check-out",
                        value = "18/11/2025 - 12:00"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookingInfoRow(
                        label = "Số đêm",
                        value = "3 đêm"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookingInfoRow(
                        label = "Loại phòng",
                        value = "Phòng đơn"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BookingInfoRow(
                        label = "Sức chứa",
                        value = "1 người"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mục đích
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💼 Mục đích",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Công tác giảng dạy",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF374151)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Giá
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💰 Chi phí",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "200.000đ x 3 đêm",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6B7280)
                            )
                        )
                        Text(
                            text = "600.000đ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF374151)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E7EB))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng cộng",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                        )
                        Text(
                            text = "600.000đ",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tiện nghi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "✨ Tiện nghi phòng",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val amenities = listOf(
                        "Máy lạnh",
                        "Wi-Fi miễn phí",
                        "Giường đơn",
                        "Tủ quần áo",
                        "Bàn làm việc",
                        "Tủ lạnh nhỏ",
                        "Toilet riêng",
                        "Nước nóng"
                    )

                    amenities.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { amenity ->
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF16A34A),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = amenity,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF374151)
                                        )
                                    )
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút liên hệ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0284C7))
                    .clickable { /* TODO: Liên hệ */ },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Liên hệ quản lý",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BookingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF6B7280)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF111827),
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookingDetailScreenPreview() {
    SORMS_appTheme {
        BookingDetailScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun RulesScreenPreview() {
    SORMS_appTheme {
        RulesScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactManagerScreenPreview() {
    SORMS_appTheme {
        ContactManagerScreen()
    }
}


