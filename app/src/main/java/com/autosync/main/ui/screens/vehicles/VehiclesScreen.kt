package com.autosync.main.ui.screens.vehicles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    onNavigateToAddVehicle: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Vehículos", fontWeight = FontWeight.Bold, fontSize = 24.sp) })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Text("Mis Vehículos", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateToAddVehicle, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = "Agregar vehículo")
                Text("Agregar nuevo vehículo")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Vehicle items will be displayed here
            }
        }
    }
}
