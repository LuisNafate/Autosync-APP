package com.autosync.main.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.autosync.main.data.local.model.Notification
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.data.local.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddVehicle: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showNotificationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel principal", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                actions = {
                    val hasNotifications = state.unreadCount > 0
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { showNotificationDialog = true }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.DarkGray.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                            }
                        }
                        if (hasNotifications) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .offset(x = (-8).dp, y = (8).dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    state.user?.let {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.nombre.firstOrNull()?.uppercase() ?: "U",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text("Hola, ${it.nombre}", style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Text("Mis vehículos", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                if (state.vehicles.isEmpty()) {
                    Text("No tienes vehículos registrados", color = Color.Gray)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.vehicles) { vehicle ->
                            VehicleCard(vehicle = vehicle)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToAddVehicle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10374A).copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar vehículo", tint = Color.White)
                    Text("Agregar nuevo vehículo", color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Servicios recientes", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No tienes servicios recientes", color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Últimas facturas", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No tienes facturas recientes", color = Color.Gray)
            }
        }
    }


    if (showNotificationDialog) {
        Dialog(onDismissRequest = { showNotificationDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        IconButton(onClick = { showNotificationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (state.notifications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No tienes notificaciones", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.notifications) { notification ->
                                NotificationItem(
                                    notification = notification,
                                    onMarkAsRead = { viewModel.markAsRead(notification.id) }
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
fun NotificationItem(
    notification: Notification,
    onMarkAsRead: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(notification.date)
    val bgColor = if (notification.read) Color(0xFF2D3748) else Color(0xFF10374A).copy(alpha = 0.3f)
    
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarkAsRead() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
             Icon(
                if (notification.type == "VEHICLE") Icons.Default.DirectionsCar else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.message, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(dateStr, color = Color.Gray, fontSize = 12.sp)
            }
            if (!notification.read) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.DirectionsCar, contentDescription = "Vehículo", tint = Color.White, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("${vehicle.make} ${vehicle.year}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Text("Placas: ${vehicle.licensePlate}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
