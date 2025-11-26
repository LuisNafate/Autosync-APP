package com.autosync.main.ui.screens.facturas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleFacturaScreen(
    serviceId: Int,
    onNavigateBack: () -> Unit,
    viewModel: FacturasViewModel = hiltViewModel()
) {
    val service by viewModel.getServiceById(serviceId).collectAsState(initial = null)
    val vehicle by viewModel.getVehicleForService(serviceId).collectAsState(initial = null)
    
    val backgroundColor = Color(0xFF101C22)
    val cardColor = Color(0xFF1F2937)
    val accentColor = Color(0xFF10374A)
    
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Detalle factura",
                        fontWeight = FontWeight.SemiBold,
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Datos receptor
            Text(
                "Datos receptor",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Nombre: GuzGuz",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            "Email: guz@gmail.com",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
            
            // Información del vehículo
            vehicle?.let { vehicleData ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(23.dp)
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = "Vehículo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Marca: ${vehicleData.make}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                "Modelo: ${vehicleData.model}",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                "Año: ${vehicleData.year}",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                "Placas: ${vehicleData.licensePlate}",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
            }
            
            // Detalle servicio
            Text(
                "Detalle servicio",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
            
            service?.let { serviceData ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(accentColor, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Build,
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
                                serviceData.serviceType,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.5.sp,
                                color = Color.White,
                                lineHeight = 18.6.sp
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "Taller: ${serviceData.workshop}",
                                    fontSize = 13.6.sp,
                                    color = Color(0xFF9CA3AF),
                                    lineHeight = 16.3.sp
                                )
                                Text(
                                    "Kilometraje: ${SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(serviceData.date)}",
                                    fontSize = 13.6.sp,
                                    color = Color(0xFF9CA3AF),
                                    lineHeight = 16.3.sp
                                )
                                Text(
                                    serviceData.description,
                                    fontSize = 12.6.sp,
                                    color = Color(0xFFB0B0B0),
                                    lineHeight = 15.1.sp
                                )
                            }
                        }
                    }
                }
                
                // Monto total
                Text(
                    "Monto total: ${serviceData.cost ?: 0.0} MXN",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón descargar factura
            Button(
                onClick = { /* TODO: Descargar factura */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Icon(
                    Icons.Outlined.FileDownload,
                    contentDescription = "Descargar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Descargar factura PDF",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}