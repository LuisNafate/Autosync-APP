package com.autosync.main.ui.screens.vehiclehistory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
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
import com.autosync.main.data.local.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: VehicleHistoryViewModel = hiltViewModel()
) {
    val vehicle by viewModel.vehicle.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle?.let { "${it.make} (${it.licensePlate})" } ?: "Historial") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(it)) {
            if (vehicle == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    VehicleInfoCard(vehicle!!)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Historial de servicios", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // This will be replaced with a list of services in the future
                    Text("No hay servicios registrados", color = Color.Gray)
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = { /* TODO: Navigate to add service */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10374A))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Registrar Nuevo Servicio", tint = Color.White)
                        Text("Registrar Nuevo Servicio", color = Color.White)
                    }
                }
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
