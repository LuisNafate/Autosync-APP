package com.autosync.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autosync.main.ui.screens.addvehicle.AddVehicleScreen
import com.autosync.main.ui.screens.home.HomeScreen
import com.autosync.main.ui.screens.login.LoginScreen
import com.autosync.main.ui.screens.registro.RegistroScreen
import com.autosync.main.ui.theme.MainTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
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
                            HomeScreen(onNavigateToAddVehicle = { navController.navigate("add_vehicle") })
                        }
                        composable("add_vehicle") {
                            AddVehicleScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
