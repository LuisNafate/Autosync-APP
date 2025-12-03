package com.autosync.main.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.data.local.model.Notification
import com.autosync.main.data.local.model.NotificationType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNotificationClick: (Notification) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val backgroundColor = Color(0xFF101C22)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notificaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (state.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text(
                                "Marcar todas",
                                color = Color(0xFF3B82F6),
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                state.notifications.isEmpty() -> {
                    EmptyNotificationsView()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val today = state.notifications.filter { isToday(it.date) }
                        if (today.isNotEmpty()) {
                            item {
                                Text(
                                    "HOY",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(today) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        onNotificationClick(notification)
                                    }
                                )
                            }
                        }
                        val yesterday = state.notifications.filter { isYesterday(it.date) }
                        if (yesterday.isNotEmpty()) {
                            item {
                                Text(
                                    "AYER",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(yesterday) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        onNotificationClick(notification)
                                    }
                                )
                            }
                        }
                        val lastWeek = state.notifications.filter {
                            !isToday(it.date) && !isYesterday(it.date) && isLastWeek(it.date)
                        }
                        if (lastWeek.isNotEmpty()) {
                            item {
                                Text(
                                    "SEMANA PASADA",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF9CA3AF),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(lastWeek) { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onClick = {
                                        viewModel.markAsRead(notification.id)
                                        onNotificationClick(notification)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    val (icon, iconColor) = getNotificationIcon(notification.type)
    val timeText = getRelativeTime(notification.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                Color(0xFF1F2937)
            } else {
                Color(0xFF1F2937).copy(alpha = 0.95f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3B82F6))
                        )
                    }
                }

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF),
                    lineHeight = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = timeText,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF4B5563)
            )
            Text(
                "No tienes notificaciones",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9CA3AF)
            )
            Text(
                "Te avisaremos cuando haya algo nuevo",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

fun getNotificationIcon(type: NotificationType): Pair<ImageVector, Color> {
    return when (type) {
        NotificationType.SERVICE_UPCOMING -> Icons.Default.Build to Color(0xFF3B82F6)
        NotificationType.SERVICE_REGISTERED -> Icons.Default.CheckCircle to Color(0xFF10B981)
        NotificationType.VEHICLE_REGISTERED -> Icons.Default.DirectionsCar to Color(0xFF8B5CF6)
        NotificationType.INVOICE_GENERATED -> Icons.Default.Receipt to Color(0xFFF59E0B)
        NotificationType.BATTERY_WARNING -> Icons.Default.Warning to Color(0xFFEF4444)
        NotificationType.MAINTENANCE_DUE -> Icons.Default.Settings to Color(0xFF06B6D4)
    }
}

fun getRelativeTime(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "Ahora"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
    }
}

fun isToday(date: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = Date() }
    val cal2 = Calendar.getInstance().apply { time = date }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isYesterday(date: Date): Boolean {
    val cal1 = Calendar.getInstance().apply {
        time = Date()
        add(Calendar.DAY_OF_YEAR, -1)
    }
    val cal2 = Calendar.getInstance().apply { time = date }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isLastWeek(date: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = Date() }
    val cal2 = Calendar.getInstance().apply { time = date }
    val diffDays = (cal1.timeInMillis - cal2.timeInMillis) / (1000 * 60 * 60 * 24)
    return diffDays in 2..7
}