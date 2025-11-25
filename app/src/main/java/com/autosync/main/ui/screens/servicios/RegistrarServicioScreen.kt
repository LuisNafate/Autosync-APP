package com.autosync.main.ui.screens.servicios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autosync.main.ui.components.CustomTextField
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarServicioScreen(
    onNavigateBack: () -> Unit,
    viewModel: RegistrarServicioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var isServicioDropdownExpanded by remember { mutableStateOf(false) }
    var isCategoriaDropdownExpanded by remember { mutableStateOf(false) }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fecha
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onFechaChange(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar servicio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text("Vehículo", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = isVehicleDropdownExpanded,
                onExpandedChange = { isVehicleDropdownExpanded = !isVehicleDropdownExpanded }
            ) {
                CustomTextField(
                    value = state.selectedVehicleName ?: "Seleccionar vehículo...",
                    onValueChange = {},
                    label = "",
                    placeholder = "Selecciona un vehículo",
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isVehicleDropdownExpanded
                        )
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isVehicleDropdownExpanded,
                    onDismissRequest = { isVehicleDropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF1F2937))
                ) {
                    state.vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehicle.make} ${vehicle.model} - ${vehicle.licensePlate}",
                                    color = Color.White
                                )
                            },
                            onClick = {
                                viewModel.onVehicleSelected(vehicle)
                                isVehicleDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))


            Text("Servicio", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = isServicioDropdownExpanded,
                onExpandedChange = { isServicioDropdownExpanded = !isServicioDropdownExpanded }
            ) {
                CustomTextField(
                    value = state.tipoServicio,
                    onValueChange = {},
                    label = "",
                    placeholder = "Seleccionar servicio...",
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isServicioDropdownExpanded
                        )
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isServicioDropdownExpanded,
                    onDismissRequest = { isServicioDropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF1F2937))
                ) {
                    state.tiposServicio.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo, color = Color.White) },
                            onClick = {
                                viewModel.onTipoServicioChange(tipo)
                                isServicioDropdownExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Row {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Otro servicio (especificar)", color = Color.White)
                            }
                        },
                        onClick = {
                            viewModel.onTipoServicioChange("Otro")
                            isServicioDropdownExpanded = false
                        }
                    )
                }
            }

            if (state.tipoServicio == "Otro") {
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.otroServicio,
                    onValueChange = { viewModel.onOtroServicioChange(it) },
                    label = "",
                    placeholder = "Ej: Cambio de bujías"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))



            Text("Taller", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.taller,
                onValueChange = { viewModel.onTallerChange(it) },
                label = "",
                placeholder = "Nombre del taller"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fecha
            Text("Fecha", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(state.fecha)),
                onValueChange = {},
                label = "",
                placeholder = "DD/MM/AAAA",
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Costo (opcional)", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.costo,
                onValueChange = { viewModel.onCostoChange(it) },
                label = "",
                placeholder = "0.00",
                leadingIcon = {
                    Text("$", color = Color.Gray)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))


            Text("Detalles adicionales", color = Color.White, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                placeholder = { Text("Describe qué incluye este servicio...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(10.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(24.dp))


            if (state.errorMessage != null) {
                Text(
                    state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }


            Button(
                onClick = {
                    viewModel.registrarServicio()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isValid && !state.isLoading,
                shape = RoundedCornerShape(10.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar servicio", color = Color.White)
                }
            }
        }
    }
}