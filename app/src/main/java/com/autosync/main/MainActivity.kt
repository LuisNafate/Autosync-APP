package com.autosync.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.autosync.main.ui.navigation.BottomNavigationBar
import com.autosync.main.ui.screens.addvehicle.AddVehicleScreen
import com.autosync.main.ui.screens.home.HomeScreen
import com.autosync.main.ui.screens.login.LoginScreen
import com.autosync.main.ui.screens.registro.RegistroScreen
import com.autosync.main.ui.screens.vehiclehistory.VehicleHistoryScreen
import com.autosync.main.ui.screens.servicios.RegistrarServicioScreen
import com.autosync.main.ui.screens.servicios.ServiciosScreen
import com.autosync.main.ui.screens.vehicles.VehiclesScreen
import com.autosync.main.ui.theme.MainTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@AndroidEntryPoint
    class MainActivity : ComponentActivity() {

    @javax.inject.Inject lateinit var userRepository: com.autosync.main.data.repository.UserRepository
    @javax.inject.Inject lateinit var vehicleRepository: com.autosync.main.data.repository.VehicleRepository
    @javax.inject.Inject lateinit var serviceRepository: com.autosync.main.data.repository.ServiceRepository

    private val callbackManager = com.facebook.CallbackManager.Factory.create()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val sharedPrefs = getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
        val expiryTime = sharedPrefs.getLong("session_expiry", 0)
        val currentTime = System.currentTimeMillis()
        
        val isValidSession = auth.currentUser != null && expiryTime > currentTime
        val nextDestination = if (isValidSession) "home" else "login"

        setContent {
            MainTheme {
                AppNavigation(
                    startDestination = "splash",
                    nextDestination = nextDestination,
                    userRepository = userRepository,
                    vehicleRepository = vehicleRepository,
                    serviceRepository = serviceRepository,
                    callbackManager = callbackManager
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    startDestination: String,
    nextDestination: String,
    userRepository: com.autosync.main.data.repository.UserRepository,
    vehicleRepository: com.autosync.main.data.repository.VehicleRepository,
    serviceRepository: com.autosync.main.data.repository.ServiceRepository,
    callbackManager: com.facebook.CallbackManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val routesWithoutBottomBar = setOf("splash", "login", "registro")

    Scaffold(
        bottomBar = {
            if (currentRoute !in routesWithoutBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(it)
        ) {
            composable("splash") {
                com.autosync.main.ui.screens.splash.SplashScreen(
                    onAnimationEnd = {
                        navController.navigate(nextDestination) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegistro = { navController.navigate("registro") },
                    callbackManager = callbackManager
                )
            }
            composable("registro") {
                RegistroScreen(
                    onRegistroSuccess = {
                        navController.navigate("home") {
                            popUpTo("registro") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    callbackManager = callbackManager
                )
            }
            composable("home") {
                val context = androidx.compose.ui.platform.LocalContext.current
                val scope = androidx.compose.runtime.rememberCoroutineScope()
                
                HomeScreen(
                    onNavigateToAddVehicle = {
                        navController.navigate("vehicle_details")
                    },
                    onLogout = {
                        scope.launch {
                            try {
                                val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken("510711962650-t0ub0bhp1kaobpb80ogul9plugbb6b7o.apps.googleusercontent.com")
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                                googleSignInClient.signOut()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            try {
                                userRepository.clearLocalUser()
                                vehicleRepository.clearLocalVehicles()
                                serviceRepository.clearLocalServices()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            val sharedPrefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                            sharedPrefs.edit().clear().apply()

                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(
                route = "vehicle_details?vehicleId={vehicleId}",
                arguments = listOf(navArgument("vehicleId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) {
                AddVehicleScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("vehicles") {
                VehiclesScreen(
                    onNavigateToAddVehicle = {
                        navController.navigate("vehicle_details")
                    },
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
                        navController.navigate("registrar_servicio")
                    },
                    onNavigateToInvoiceDetail = { serviceId ->
                        navController.navigate("detalle_factura/$serviceId")
                    }
                )
            }
            
            composable(
                route = "detalle_factura/{serviceId}",
                arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: -1
                com.autosync.main.ui.screens.facturas.DetalleFacturaScreen(
                    serviceId = serviceId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("services") {
                ServiciosScreen(
                    onNavigateToRegistrarServicio = {
                        navController.navigate("registrar_servicio")
                    }
                )
            }
            composable("registrar_servicio") {
                RegistrarServicioScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}