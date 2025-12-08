package com.autosync.main.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.autosync.main.ui.components.CustomTextField
import com.autosync.main.ui.icons.FacebookIcon
import com.autosync.main.ui.icons.GoogleIcon

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegistro: () -> Unit,
    callbackManager: com.facebook.CallbackManager,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Register Facebook Callback
    DisposableEffect(Unit) {
        val loginManager = com.facebook.login.LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : com.facebook.FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) {
                viewModel.signInWithFacebook(result.accessToken)
            }

            override fun onCancel() {
                // Handle cancellation
            }

            override fun onError(error: com.facebook.FacebookException) {
                viewModel.signInWithGoogle("FAIL") // Reuse error logic
            }
        })
        onDispose { }
    }

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Ingresa tus datos para continuar",
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            if (state.generalError != null) {
                Text(
                    text = state.generalError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = "",
                    placeholder = "example@gmail.com",
                    isError = state.emailError != null,
                    errorMessage = state.emailError,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email Icon") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contraseña",
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = "",
                    placeholder = "Ingresa tu contraseña",
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.recordarme, onCheckedChange = { viewModel.onRecordarmeChange(it) })
                    Text("Recordarme", color = Color.White)
                }
                TextButton(
                    onClick = { showForgotPasswordDialog = true },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        "¿Olvidó su contraseña?",
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            if (showForgotPasswordDialog) {
                var resetEmail by remember { mutableStateOf(state.email) }
                var isSending by remember { mutableStateOf(false) }
                var errorMsg by remember { mutableStateOf<String?>(null) }
                var successMsg by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showForgotPasswordDialog = false },
                    title = { Text("Recuperar Contraseña") },
                    text = {
                        Column {
                            Text("Ingresa tu correo electrónico para recibir un enlace de recuperación.")
                            Spacer(modifier = Modifier.height(16.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = resetEmail,
                                onValueChange = { resetEmail = it; errorMsg = null },
                                label = "",
                                placeholder = "tuemail@ejemplo.com",
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )
                            if (errorMsg != null) {
                                Text(errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            if (successMsg != null) {
                                Text(successMsg!!, color = Color.Green, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    },
                    confirmButton = {
                        if (successMsg == null) {
                            Button(
                                onClick = {
                                    if (resetEmail.isBlank()) {
                                        errorMsg = "Ingresa un correo válido"
                                    } else {
                                        isSending = true
                                        viewModel.resetPassword(resetEmail) { success, error ->
                                            isSending = false
                                            if (success) {
                                                successMsg = "Correo enviado. Revisa tu bandeja de entrada o SPAM."
                                            } else {
                                                errorMsg = error ?: "Error al enviar correo"
                                            }
                                        }
                                    }
                                },
                                enabled = !isSending
                            ) {
                                if (isSending) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                } else {
                                    Text("Enviar")
                                }
                            }
                        } else {
                            Button(onClick = { showForgotPasswordDialog = false }) {
                                Text("Cerrar")
                            }
                        }
                    },
                    dismissButton = {
                        if (successMsg == null) {
                            TextButton(onClick = { showForgotPasswordDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Login Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar sesión", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text("O inicia sesión con", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Setup Google Sign In
                val context = androidx.compose.ui.platform.LocalContext.current
                val googleSignInClient = remember {
                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(com.autosync.main.R.string.default_web_client_id)) 
                        .requestEmail()
                        .build()
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                }
                
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == android.app.Activity.RESULT_OK) {
                        val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                            account?.idToken?.let { token ->
                                viewModel.signInWithGoogle(token)
                            }
                        } catch (e: com.google.android.gms.common.api.ApiException) {
                            viewModel.signInWithGoogle("FAIL")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { 
                        launcher.launch(googleSignInClient.signInIntent) 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = GoogleIcon, contentDescription = "Google Icon", tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", color = Color.White)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                OutlinedButton(
                    onClick = { 
                        com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(
                            context as androidx.activity.ComponentActivity,
                            listOf("email", "public_profile")
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = FacebookIcon, contentDescription = "Facebook Icon", tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¿No tienes una cuenta?", color = Color.Gray)
                TextButton(onClick = onNavigateToRegistro) {
                    Text("Regístrate aquí", color = Color(0xFF4A90B5))
                }
            }
        }
    }
}
