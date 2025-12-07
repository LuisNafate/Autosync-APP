package com.autosync.main.ui.screens.vehiclehistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.data.local.model.Service
import com.autosync.main.data.local.model.Vehicle
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddService: (Int) -> Unit,
    onNavigateToInvoiceDetail: (Int) -> Unit = {},
    viewModel: VehicleHistoryViewModel = hiltViewModel()
) {
    val vehicle by viewModel.vehicle.collectAsState()
    val services by viewModel.services.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val backgroundColor = Color(0xFF101C22)
    val accentColor = Color(0xFF10374A)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        vehicle?.let { "${it.make}  (${it.licensePlate})" } ?: "Historial",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            "Cargando historial...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
                vehicle == null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "No se encontró información del vehículo",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Volver")
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Card de información del vehículo mejorada
                        VehicleInfoCardImproved(vehicle!!)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "Historial de servicios",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (services.isEmpty()) {
                                item {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No hay servicios registrados",
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                            items(services) { service ->
                                ServiceCardImproved(
                                    service = service,
                                    onViewInvoice = { onNavigateToInvoiceDetail(service.id) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Botón mejorado para registrar nuevo servicio
                        Button(
                            onClick = { vehicle?.let { onNavigateToAddService(it.id) } },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 14.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Registrar Nuevo Servicio",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleInfoCardImproved(vehicle: Vehicle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = "Vehículo",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Marca: ${vehicle.make}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 19.2.sp
                    )
                    Text(
                        "Modelo: ${vehicle.model}",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF),
                        lineHeight = 16.8.sp
                    )
                    Text(
                        "Año: ${vehicle.year}",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF),
                        lineHeight = 16.8.sp
                    )
                }
            }
            
            TextButton(
                onClick = { /* TODO: Navigate to vehicle details */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Ver Detalles del Vehículo",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6),
                    lineHeight = 16.8.sp
                )
            }
        }
    }
}

@Composable
fun ServiceCardImproved(
    service: Service,
    onViewInvoice: () -> Unit
) {
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(service.date)
    val serviceIcon = if (service.serviceType.contains("aceite", ignoreCase = true)) {
        Icons.Default.Build
    } else {
        Icons.Default.Settings
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(23.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF10374A), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        serviceIcon,
                        contentDescription = "Servicio",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        service.serviceType,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 19.2.sp
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            "Taller: ${service.workshop}",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF),
                            lineHeight = 16.8.sp
                        )
                        Text(
                            "Fecha: $formattedDate",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF),
                            lineHeight = 16.8.sp
                        )
                        Text(
                            service.details ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFB0B0B0),
                            lineHeight = 15.6.sp
                        )
                        service.cost?.let {
                            Text(
                                "Costo: $$it MXN",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF),
                                lineHeight = 16.8.sp
                            )
                        }
                    }
                }
            }
            
            TextButton(
                onClick = onViewInvoice,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Ver Factura",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6),
                    lineHeight = 16.8.sp
                )
            }
        }
    }
}

@Composable
fun VehicleInfoCard(vehicle: Vehicle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DirectionsCar, contentDescription = "Vehículo", modifier = Modifier.size(40.dp), tint = Color.White)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text("Marca: ${vehicle.make}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text("Modelo: ${vehicle.model}", fontSize = 16.sp, color = Color.Gray)
                Text("Año: ${vehicle.year}", fontSize = 16.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { /* TODO: Navigate to edit vehicle */ }) {
                Text("Ver Detalles", color = Color(0xFF3B82F6))
            }
        }
    }
}

@Composable
fun ServiceListItem(service: Service) {
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(service.date)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Build, contentDescription = "Servicio", modifier = Modifier.size(40.dp), tint = Color.White)
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text(service.serviceType, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text("Taller: ${service.workshop}", fontSize = 16.sp, color = Color.Gray)
                Text("Fecha: $formattedDate", fontSize = 16.sp, color = Color.Gray)
                Text(service.details ?: "", fontSize = 16.sp, color = Color.Gray)
                service.cost?.let {
                    Text("Costo: $${it} MXN", fontSize = 16.sp, color = Color.Gray)
                }
                TextButton(onClick = { /* TODO: View invoice */ }) {
                    Text("Ver Factura", color = Color(0xFF3B82F6))
                }
            }
        }
    }
}
