package com.autosync.main.ui.screens.vehicles

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.autosync.main.R
import com.autosync.main.data.local.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    onNavigateToAddVehicle: () -> Unit,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Text("Mis Vehículos", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToAddVehicle, 
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10374A))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar vehículo", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar nuevo vehículo", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (vehicles.isEmpty()) {
                Text("No tienes vehículos registrados", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleListItem(vehicle = vehicle)
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(vehicle: Vehicle) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = vehicle.imageUri,
                    error = painterResource(id = R.drawable.ic_car_placeholder)
                ),
                contentDescription = "Imagen del vehículo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Column {
                Text(vehicle.make, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                Text("${vehicle.year}", fontSize = 16.sp, color = Color.Gray)
                Text("Placas: ${vehicle.licensePlate}", fontSize = 16.sp, color = Color.Gray)
                TextButton(onClick = { /* TODO: Navigate to history */ }) {
                    Text("Ver historial", color = Color(0xFF3B82F6))
                }
            }
        }
    }
}
