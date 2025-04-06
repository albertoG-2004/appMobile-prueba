// LoginViewModel.kt
package com.example.moviles.ui.login.ui

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles.apiService.RetroClient
import com.example.moviles.ui.login.ui.models.LoginReq
import com.example.moviles.ui.login.ui.models.LoginRes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class LoginViewModel(private val applicationContext: Context) : ViewModel() {

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loginEnabled by mutableStateOf(false)
        private set

    private val _navigateToHome = MutableSharedFlow<String?>()
    val navigateToHome: SharedFlow<String?> = _navigateToHome.asSharedFlow()

    var errorEmail by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    // Nueva variable de estado para el rol seleccionado:
    var selectedRole by mutableStateOf<String?>(null)
        private set

    private val masterKeyAlias by lazy {
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "encrypted_user_prefs",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun onEmailChanged(newEmail: String) {
        email = newEmail
        validateEmail()
        validateLogin()

    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
        validateLogin()
    }

    private fun validateEmail() {
        errorEmail = if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Email no válido"
        } else {
            null
        }
    }

    private fun validateLogin() {
        loginEnabled = email.isNotBlank() && password.isNotBlank() && errorEmail == null
    }

    // Nueva función para manejar la selección de rol:
    fun onRoleSelected(role: String) {
        selectedRole = role
    }


    private fun saveUserRole(role: String) {
        encryptedSharedPreferences.edit().putString("user_role", role).apply()
    }

    fun onLoginButtonClicked() {
        if (!loginEnabled) {
            return
        }

        val currentRole = selectedRole

        if (currentRole == null) {
            loginError = "Por favor, selecciona un rol (Administrador o Cliente)"
            return
        }

        isLoading = true
        loginError = null

        val credentials = LoginReq(email, password)
        val call: Call<LoginRes> = when (currentRole) {
            "admin" -> RetroClient.instance.login(credentials)
            "client" -> RetroClient.instance.loginClient(credentials)
            else -> {
                loginError = "Rol no válido seleccionado"
                isLoading = false
                return
            }
        }


        call.enqueue(object : retrofit2.Callback<LoginRes> {
            override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                isLoading = false
                if (response.isSuccessful) {
                    val body = response.body()
                    println(body)
                    if (body?.message == "Login exitoso") {
                        saveUserRole(currentRole)
                        viewModelScope.launch{
                            _navigateToHome.emit(currentRole)
                        }
                        println("Login con exito como $currentRole")
                    } else {
                        loginError = "Inicio de sesión fallido como $currentRole"
                        println("Error en login")
                    }
                } else {
                    loginError = "Error del servidor al intentar login como $currentRole: ${response.errorBody()?.string()}"
                    println("Error del servidor: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                isLoading = false
                loginError = "Error en la conexión al intentar login como $currentRole: ${t.message}"
                println("Error en la conexión: ${t.message}")
            }
        })
    }
}