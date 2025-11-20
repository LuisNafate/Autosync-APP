package com.autosync.main.ui.screens.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autosync.main.ui.components.CustomTextField
import com.autosync.main.ui.icons.FacebookIcon
import com.autosync.main.ui.icons.GoogleIcon

@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: RegistroViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isRegistroSuccessful) {
        if (state.isRegistroSuccessful) {
            onRegistroSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Crea una cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Ingresa tus datos para registrarte",
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nombre",
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.nombre,
                    onValueChange = { viewModel.onNombreChange(it) },
                    label = "",
                    placeholder = "Tu nombre completo",
                    isError = state.nombreError != null,
                    errorMessage = state.nombreError,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre Icon") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    placeholder = "tuemail@ejemplo.com",
                    isError = state.emailError != null,
                    errorMessage = state.emailError,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") }
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

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Confirmar Contraseña",
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = "",
                    placeholder = "Confirma tu contraseña",
                    isError = state.confirmPasswordError != null,
                    errorMessage = state.confirmPasswordError,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password Icon") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = state.aceptaTerminos, onCheckedChange = { viewModel.onAceptaTerminosChange(it) })
                Text("Acepto los ", color = Color.White)
                TextButton(onClick = { /* TODO */ }) {
                    Text("Términos y Condiciones", color = Color(0xFF4A90B5))
                }
            }
            if (state.terminosError != null) {
                Text(
                    text = state.terminosError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.registrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Register Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrarse", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text("O registrate con", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Google Login */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = GoogleIcon, contentDescription = "Google Icon", tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = { /* TODO: Facebook Login */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = FacebookIcon, contentDescription = "Facebook Icon", tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¿Ya tienes una cuenta?", color = Color.Gray)
                TextButton(onClick = onNavigateToLogin) {
                    Text("Inicia sesión aquí", color = Color(0xFF4A90B5))
                }
            }
        }
    }
}
