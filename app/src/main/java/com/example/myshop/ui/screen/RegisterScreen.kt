package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.ui.theme.*
import com.example.myshop.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    email             : String,
    password          : String,
    uiState           : AuthUiState,
    onEmailChange     : (String) -> Unit,
    onPasswordChange  : (String) -> Unit,
    onRegisterClick   : () -> Unit,
    onNavigateToLogin : () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(NavyPrimary)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Text("Toko-I", color = CardWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Buat Akun Baru", color = CardWhite.copy(alpha = 0.8f), fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Daftar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Isi data untuk membuat akun", fontSize = 13.sp, color = TextSecondary)

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value         = email,
                        onValueChange = onEmailChange,
                        label         = { Text("Email") },
                        leadingIcon   = { Icon(Icons.Default.Email, null) },
                        modifier      = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary,
                            focusedLabelColor  = NavyPrimary,
                            focusedLeadingIconColor = NavyPrimary
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value         = password,
                        onValueChange = onPasswordChange,
                        label         = { Text("Password") },
                        leadingIcon   = { Icon(Icons.Default.Lock, null) },
                        trailingIcon  = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    null
                                )
                            }
                        },
                        modifier      = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine    = true,
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary,
                            focusedLabelColor  = NavyPrimary,
                            focusedLeadingIconColor = NavyPrimary
                        )
                    )

                    if (uiState is AuthUiState.Error) {
                        Spacer(Modifier.height(8.dp))
                        Text(text = uiState.message, color = ErrorRed, fontSize = 12.sp)
                    }

                    if (uiState is AuthUiState.Success) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Registrasi berhasil! Cek email untuk verifikasi.",
                            color    = SuccessGreen,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick  = onRegisterClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        enabled  = uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = CardWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Daftar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sudah punya akun? ", color = TextSecondary, fontSize = 13.sp)
                        TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                            Text("Masuk", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}