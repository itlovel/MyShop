package com.example.myshop.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myshop.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    email: String,
    password: String,
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    /*
     * LoginScreen tidak menyimpan state email/password sendiri.
     * State dikirim dari luar, yaitu dari ViewModel.
     * Inilah konsep state hoisting.
     */
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.Companion.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = {
                Text("Email")
            },
            modifier = Modifier.Companion.fillMaxWidth()
        )

        Spacer(modifier = Modifier.Companion.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.Companion.fillMaxWidth()
        )

        Spacer(modifier = Modifier.Companion.height(16.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.Companion.fillMaxWidth(),
            enabled = uiState !is AuthUiState.Loading
        ) {
            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.Companion.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.Companion.height(8.dp))

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.Companion.fillMaxWidth()
        ) {
            Text("Belum punya akun? Register")
        }

        /*
         * Jika state Error, tampilkan pesan error.
         */
        if (uiState is AuthUiState.Error) {
            Spacer(modifier = Modifier.Companion.height(12.dp))

            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}