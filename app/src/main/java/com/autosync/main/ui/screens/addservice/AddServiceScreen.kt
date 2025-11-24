package com.autosync.main.ui.screens.addservice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddServiceViewModel = hiltViewModel()
) {
    val serviceType by viewModel.serviceType.collectAsState()
    val workshop by viewModel.workshop.collectAsState()
    val date by viewModel.date.collectAsState()
    val description by viewModel.description.collectAsState()

    var isServiceDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val serviceOptions = listOf("Cambio de Aceite", "Rotación y Balanceo", "Frenos", "Afinación", "Otro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar nuevo servicio", fontWeight = FontWeight.Bold) },
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
            Text("Servicio")
            ExposedDropdownMenuBox(
                expanded = isServiceDropdownExpanded,
                onExpandedChange = { isServiceDropdownExpanded = !isServiceDropdownExpanded }
            ) {
                CustomTextField(
                    value = serviceType,
                    onValueChange = {},
                    label = "",
                    placeholder = "Seleccionar servicio...",
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isServiceDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = isServiceDropdownExpanded,
                    onDismissRequest = { isServiceDropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    serviceOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                viewModel.onServiceTypeChange(it)
                                isServiceDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Taller")
            CustomTextField(modifier = Modifier.fillMaxWidth(), value = workshop, onValueChange = viewModel::onWorkshopChange, label = "", placeholder = "Nombre del taller")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Fecha")
            CustomTextField(
                value = date?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                onValueChange = {},
                label = "",
                placeholder = "DD/MM/AAAA",
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Descripción")
            CustomTextField(modifier = Modifier.fillMaxWidth().height(120.dp), value = description, onValueChange = viewModel::onDescriptionChange, label = "", placeholder = "Detalles adicionales del servicio...")
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveService()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10374A))
            ) {
                Icon(Icons.Default.Build, contentDescription = "Registrar Servicio", tint = Color.White)
                Text("Registrar servicio", color = Color.White)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onDateChange(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
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
}
