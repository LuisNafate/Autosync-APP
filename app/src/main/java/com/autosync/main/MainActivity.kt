package com.autosync.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.autosync.main.ui.navigation.BottomNavigationBar
import com.autosync.main.ui.screens.addservice.AddServiceScreen
import com.autosync.main.ui.screens.addvehicle.AddVehicleScreen
import com.autosync.main.ui.screens.home.HomeScreen
import com.autosync.main.ui.screens.login.LoginScreen
import com.autosync.main.ui.screens.registro.RegistroScreen
import com.autosync.main.ui.screens.vehiclehistory.VehicleHistoryScreen
import com.autosync.main.ui.screens.vehicles.VehiclesScreen
import com.autosync.main.ui.theme.MainTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainTheme {
                AppNavigation()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val routesWithoutBottomBar = setOf("login", "registro")
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in routesWithoutBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(it)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegistro = { navController.navigate("registro") }
                )
            }
            composable("registro") {
                RegistroScreen(
                    onRegistroSuccess = {
                        navController.navigate("home") {
                            popUpTo("registro") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    } }
                )
            }
            composable("home") {
                HomeScreen(onNavigateToAddVehicle = { navController.navigate("vehicle_details") })
            }
            composable(
                route = "vehicle_details?vehicleId={vehicleId}",
                arguments = listOf(navArgument("vehicleId") { 
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) {
                AddVehicleScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("vehicles") {
                VehiclesScreen(
                    onNavigateToAddVehicle = { navController.navigate("vehicle_details") },
                    onNavigateToEditVehicle = { vehicleId ->
                        navController.navigate("vehicle_details?vehicleId=$vehicleId")
                    },
                    onNavigateToVehicleHistory = { vehicleId ->
                        navController.navigate("vehicle_history/$vehicleId")
                    }
                )
            }
            composable(
                route = "vehicle_history/{vehicleId}",
                arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
            ) {
                VehicleHistoryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddService = { vehicleId ->
                        navController.navigate("add_service/$vehicleId")
                    }
                )
            }
            composable(
                route = "add_service/{vehicleId}",
                arguments = listOf(navArgument("vehicleId") { type = NavType.IntType })
            ) {
                AddServiceScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
