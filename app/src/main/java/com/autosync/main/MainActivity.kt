package com.autosync.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autosync.main.ui.screens.login.LoginScreen
import com.autosync.main.ui.screens.registro.RegistroScreen
import com.autosync.main.ui.theme.MainTheme

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
                                onLoginSuccess = { /* TODO: Handle successful login */ },
                                onNavigateToRegistro = { navController.navigate("registro") }
                            )
                        }
                        composable("registro") {
                            RegistroScreen(
                                onRegistroSuccess = { /* TODO: Handle successful registration */ },
                                onNavigateToLogin = { navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                } }
                            )
                        }
                    }
                }
            }
        }
    }
}
