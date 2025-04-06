// LoginScreen.kt
package com.example.moviles.ui.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.moviles.R
import kotlinx.coroutines.launch
import com.example.moviles.ui.login.ui.LoginViewModelFactory

@Composable
fun LoginScreen(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
    val scope = rememberCoroutineScope()
    val navigateToHomeRole by viewModel.navigateToHome.collectAsState(initial = null)

    LaunchedEffect(navigateToHomeRole) {
        navigateToHomeRole?.let { role ->
            if (role == "admin") {
                navController.navigate("home_screen")
            } else if (role == "client") {
                navController.navigate("client_home_screen")
            } else if (role != null) {
                println("Rol desconocido o no manejado: $role")
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Login(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, navController: NavHostController) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // **Añadiendo el Título aquí:**
        Text(
            text = "Chuchesitos Piccolinos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HeaderImage(Modifier.padding(bottom = 16.dp))
        EmailField(viewModel)
        PasswordField(viewModel)

        // **Nuevos Botones de Rol:**
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Distribución equitativa de espacio
        ) {
            Button(
                onClick = { viewModel.onRoleSelected("admin") }, // Establecer rol admin
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Administrador")
            }
            Button(
                onClick = { viewModel.onRoleSelected("client") }, // Establecer rol cliente
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cliente")
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre botones de rol y botón de login

        LoginButton(viewModel) // Botón de Login (ahora usará el rol seleccionado)
        NoAccountButton(navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(viewModel: LoginViewModel) {
    val errorEmail = viewModel.errorEmail
    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { viewModel.onEmailChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Correo", color = MaterialTheme.colorScheme.onBackground) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        isError = errorEmail != null,
        supportingText = {
            if (errorEmail != null) {
                Text(text = errorEmail, color = MaterialTheme.colorScheme.error)
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        shape = RoundedCornerShape(16.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(viewModel: LoginViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.onPasswordChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Contraseña", color = MaterialTheme.colorScheme.onBackground) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Image(
                    painter = if (passwordVisible) painterResource(id = R.drawable.ic_visibility_off) else painterResource(id = R.drawable.ic_visibility),
                    contentDescription = description
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun LoginButton(viewModel: LoginViewModel) {
    val scope = rememberCoroutineScope()
    Button(
        onClick = {
            scope.launch {
                viewModel.onLoginButtonClicked() // El botón de login ahora usa el rol seleccionado
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = viewModel.loginEnabled // Mantén la habilitación basada en email y password
    ) {
        Text(text = "Acceder", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
    }
}

@Composable
fun NoAccountButton(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = { navController.navigate("register_screen") }) {
            Text(text = "¿Aún no tienes cuenta? Unete ahora")
        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Header",
        modifier = modifier
            .size(220.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}