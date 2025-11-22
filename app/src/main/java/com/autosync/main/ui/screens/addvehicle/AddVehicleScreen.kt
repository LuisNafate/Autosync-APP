package com.autosync.main.ui.screens.addvehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddVehicleViewModel = hiltViewModel()
) {

    val marca by viewModel.marca.collectAsState()
    val modelo by viewModel.modelo.collectAsState()
    val year by viewModel.year.collectAsState()
    val licensePlate by viewModel.licensePlate.collectAsState()
    val modelSuggestions by viewModel.modelSuggestions.collectAsState()

    var isModelsDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar nuevo vehículo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
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
            Text("Marca")
            CustomTextField(modifier = Modifier.fillMaxWidth(), value = marca, onValueChange = viewModel::onMarcaChange, label = "", placeholder = "Toyota")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Modelo")
            ExposedDropdownMenuBox(
                expanded = isModelsDropdownExpanded,
                onExpandedChange = { isModelsDropdownExpanded = !isModelsDropdownExpanded }
            ) {
                CustomTextField(
                    value = modelo,
                    onValueChange = { viewModel.onModeloChange(it) },
                    label = "",
                    placeholder = "Selecciona un modelo",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelsDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isModelsDropdownExpanded,
                    onDismissRequest = { isModelsDropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF1F2937))
                ) {
                    modelSuggestions.forEach {
                        DropdownMenuItem(
                            text = { Text(it.modelName, color = Color.White) },
                            onClick = {
                                viewModel.onModelSelected(it)
                                isModelsDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Año")
            CustomTextField(modifier = Modifier.fillMaxWidth(), value = year, onValueChange = viewModel::onYearChange, label = "", placeholder = "2020")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Placas")
            CustomTextField(modifier = Modifier.fillMaxWidth(), value = licensePlate, onValueChange = viewModel::onLicensePlateChange, label = "", placeholder = "ABC-123")
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Image, contentDescription = "Imagen del vehículo", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Imagen del vehículo (opcional)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveVehicle()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.DirectionsCar, contentDescription = "Registrar vehículo", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar vehículo", color = Color.White)
            }
        }
    }
}
