package com.autosync.main.ui.screens.servicios

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.data.local.entities.Servicio
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    onNavigateToRegistrarServicio: () -> Unit,
    viewModel: ServiciosViewModel = hiltViewModel()
) {
    val servicios by viewModel.servicios.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var servicioToDelete by remember { mutableStateOf<Servicio?>(null) }

    if (showDeleteDialog && servicioToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                servicioToDelete = null
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar este servicio?") },
            confirmButton = {
                Button(
                    onClick = {
                        servicioToDelete?.let { viewModel.deleteServicio(it) }
                        showDeleteDialog = false
                        servicioToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    servicioToDelete = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Servicios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRegistrarServicio,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar servicio", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (servicios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay servicios registrados",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateToRegistrarServicio) {
                            Text("Registrar primer servicio")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(servicios) { servicio ->
                        val vehicle = vehicles.find { it.id == servicio.vehicleId }
                        ServicioCard(
                            servicio = servicio,
                            vehicleName = vehicle?.let { "${it.make} ${it.model}" } ?: "Vehículo desconocido",
                            onDeleteClick = {
                                servicioToDelete = servicio
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServicioCard(
    servicio: Servicio,
    vehicleName: String,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(getCategoriaColor(servicio.categoria)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoriaIcon(servicio.categoria),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            servicio.tipoServicio,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            vehicleName,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.DateRange,
                    text = dateFormat.format(Date(servicio.fecha))
                )
                InfoItem(
                    icon = Icons.Default.Build,
                    text = servicio.taller
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    icon = Icons.Default.Settings,
                    text = servicio.categoria
                )
                if (servicio.costo > 0) {
                    Text(
                        currencyFormat.format(servicio.costo),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (servicio.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    servicio.descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun getCategoriaIcon(categoria: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (categoria.lowercase()) {
        "motor" -> Icons.Default.Settings
        "frenos" -> Icons.Default.Warning
        "sistema eléctrico" -> Icons.Default.Build
        "aire acondicionado" -> Icons.Default.Info
        else -> Icons.Default.Build
    }
}

fun getCategoriaColor(categoria: String): Color {
    return when (categoria.lowercase()) {
        "motor" -> Color(0xFF3B82F6)
        "frenos" -> Color(0xFFEF4444)
        "sistema eléctrico" -> Color(0xFFF59E0B)
        "aire acondicionado" -> Color(0xFF10B981)
        else -> Color(0xFF6366F1)
    }
}