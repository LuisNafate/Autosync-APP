package com.autosync.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autosync.main.ui.screens.home.HomeScreen
import com.autosync.main.ui.screens.login.LoginScreen
import com.autosync.main.ui.screens.login.LoginViewModel
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
                            val loginViewModel: LoginViewModel = viewModel()
                            val loginState by loginViewModel.state.collectAsState()
                            LoginScreen(
                                onLoginSuccess = { 
                                    navController.navigate("home/${loginState.userName}") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                 },
                                onNavigateToRegistro = { navController.navigate("registro") }
                            )
                        }
                        composable("registro") {
                            RegistroScreen(
                                onRegistroSuccess = { 
                                    navController.navigate("home/Usuario") {
                                        popUpTo("registro") { inclusive = true }
                                    }
                                 },
                                onNavigateToLogin = { navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                } }
                            )
                        }
                        composable("home/{userName}") { backStackEntry ->
                            val userName = backStackEntry.arguments?.getString("userName") ?: ""
                            HomeScreen(userName = userName)
                        }
                    }
                }
            }
        }
    }
}