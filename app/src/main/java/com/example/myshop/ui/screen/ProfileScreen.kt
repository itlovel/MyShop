package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.UserProfile
import com.example.myshop.ui.theme.*
import com.example.myshop.viewmodel.ProfileUiState
import com.example.myshop.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    vm          : ProfileViewModel = viewModel(),
    onLogout    : () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val profilSaya    by vm.profilSaya.collectAsStateWithLifecycle()
    val daftarProfil  by vm.daftarProfil.collectAsStateWithLifecycle()
    val uiState       by vm.uiState.collectAsStateWithLifecycle()
    val profilDiedit  by vm.profilDiedit.collectAsStateWithLifecycle()

    // Dialog tambah kasir (hanya untuk admin)
    var showTambahKasir by remember { mutableStateOf(false) }

    // Dialog edit nama
    if (profilDiedit != null) {
        EditNamaDialog(
            vm          = vm,
            targetProfil = profilDiedit!!,
            onDismiss   = vm::batalEditProfil,
        )
    }

    if (showTambahKasir) {
        TambahKasirDialog(
            vm        = vm,
            uiState   = uiState,
            onDismiss = {
                showTambahKasir = false
                vm.resetUiState()
            },
            onSuccess = {
                showTambahKasir = false
                vm.resetUiState()
            }
        )
    }

    // Snackbar untuk error ringan
    if (uiState is ProfileUiState.Error && profilDiedit == null && !showTambahKasir) {
        AlertDialog(
            onDismissRequest = vm::resetUiState,
            title   = { Text("Gagal") },
            text    = { Text((uiState as ProfileUiState.Error).message) },
            confirmButton = {
                TextButton(onClick = vm::resetUiState) { Text("OK") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Header banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyPrimary)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar inisial
                Box(
                    modifier         = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(YellowAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = profilSaya?.nama?.firstOrNull()?.uppercase() ?: "?",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = NavyPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))

                if (uiState is ProfileUiState.Loading && profilSaya == null) {
                    CircularProgressIndicator(color = CardWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text       = profilSaya?.nama?.ifBlank { "Belum ada nama" } ?: "Memuat...",
                        color      = CardWhite,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = profilSaya?.email ?: "",
                        color    = CardWhite.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    // Badge role
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (profilSaya?.isAdmin == true)
                                    YellowAccent.copy(alpha = 0.95f)
                                else
                                    CardWhite.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text       = profilSaya?.roleLabel ?: "",
                            color      = if (profilSaya?.isAdmin == true) NavyPrimary else CardWhite,
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tombol edit nama sendiri
        profilSaya?.let { profil ->
            InfoCard {
                Text(
                    "INFORMASI AKUN",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = TextSecondary,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(12.dp))

                InfoBaris(label = "Nama",  nilai = profil.nama.ifBlank { "-" })
                InfoBaris(label = "Email", nilai = profil.email)
                InfoBaris(label = "Role",  nilai = profil.roleLabel)

                Spacer(Modifier.height(14.dp))

                OutlinedButton(
                    onClick = { vm.mulaiEditProfil(profil) },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit Nama")
                }
            }
        }

        // Seksi admin (manajemen pengguna)
        if (profilSaya?.isAdmin == true) {
            Spacer(Modifier.height(12.dp))

            InfoCard {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "MANAJEMEN PENGGUNA",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    TextButton(
                        onClick = vm::muatDaftarProfil,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp), tint = NavyPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text("Refresh", fontSize = 11.sp, color = NavyPrimary)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (uiState is ProfileUiState.Loading && daftarProfil.isEmpty()) {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NavyPrimary, modifier = Modifier.size(24.dp))
                    }
                } else if (daftarProfil.isEmpty()) {
                    Text(
                        "Belum ada pengguna",
                        color    = TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    daftarProfil.forEach { profil ->
                        PenggunaBaris(
                            profil  = profil,
                            onEdit  = { vm.mulaiEditProfil(profil) }
                        )
                        if (profil != daftarProfil.last()) {
                            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick  = { showTambahKasir = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Kasir")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tombol logout
        InfoCard {
            OutlinedButton(
                onClick  = onLogout,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border   = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
            ) {
                Icon(Icons.Default.Logout, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Keluar dari Akun", color = ErrorRed, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// Sub-composables

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun InfoBaris(label: String, nilai: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(nilai, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
private fun PenggunaBaris(profil: UserProfile, onEdit: () -> Unit) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar inisial
        Box(
            modifier         = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (profil.isAdmin) NavyPrimary else ProductIconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = profil.nama.firstOrNull()?.uppercase() ?: "?",
                color      = if (profil.isAdmin) CardWhite else ProductIconTint,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                profil.nama.ifBlank { "(belum ada nama)" },
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = TextPrimary
            )
            Text(profil.email, fontSize = 11.sp, color = TextSecondary)
        }
        // Badge role
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (profil.isAdmin) NavyPrimary.copy(alpha = 0.1f) else ProductIconBg
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                profil.roleLabel,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color      = if (profil.isAdmin) NavyPrimary else ProductIconTint
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun EditNamaDialog(
    vm           : ProfileViewModel,
    targetProfil : UserProfile,
    onDismiss    : () -> Unit,
) {
    val formNamaEdit by vm.formNamaEdit.collectAsStateWithLifecycle()
    val uiState      by vm.uiState.collectAsStateWithLifecycle()

    // Tutup dialog otomatis jika sukses
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) {
            vm.resetUiState()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardWhite,
        title = {
            Text(
                "Edit Nama",
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    "Email: ${targetProfil.email}",
                    fontSize = 12.sp,
                    color    = TextSecondary
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value         = formNamaEdit,
                    onValueChange = vm::onFormNamaEditChange,
                    label         = { Text("Nama") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary,
                        focusedLabelColor  = NavyPrimary,
                    )
                )
                if (uiState is ProfileUiState.Error) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        (uiState as ProfileUiState.Error).message,
                        color    = ErrorRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = vm::simpanEditNama,
                enabled  = uiState !is ProfileUiState.Loading,
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                if (uiState is ProfileUiState.Loading) {
                    CircularProgressIndicator(
                        color       = CardWhite,
                        modifier    = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextSecondary) }
        }
    )
}

@Composable
private fun TambahKasirDialog(
    vm       : ProfileViewModel,
    uiState  : ProfileUiState,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {
    val formNama     by vm.formNama.collectAsStateWithLifecycle()
    val formEmail    by vm.formEmail.collectAsStateWithLifecycle()
    val formPassword by vm.formPassword.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.Success) onSuccess()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardWhite,
        title = {
            Text("Tambah Kasir", fontWeight = FontWeight.Bold, color = TextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value         = formNama,
                    onValueChange = vm::onFormNamaChange,
                    label         = { Text("Nama") },
                    leadingIcon   = { Icon(Icons.Default.Person, null) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = NavyPrimary,
                        focusedLabelColor       = NavyPrimary,
                        focusedLeadingIconColor = NavyPrimary,
                    )
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value           = formEmail,
                    onValueChange   = vm::onFormEmailChange,
                    label           = { Text("Email") },
                    leadingIcon     = { Icon(Icons.Default.Email, null) },
                    modifier        = Modifier.fillMaxWidth(),
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape           = RoundedCornerShape(8.dp),
                    colors          = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = NavyPrimary,
                        focusedLabelColor       = NavyPrimary,
                        focusedLeadingIconColor = NavyPrimary,
                    )
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value         = formPassword,
                    onValueChange = vm::onFormPasswordChange,
                    label         = { Text("Password") },
                    leadingIcon   = { Icon(Icons.Default.Lock, null) },
                    trailingIcon  = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                null
                            )
                        }
                    },
                    modifier             = Modifier.fillMaxWidth(),
                    singleLine           = true,
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    shape  = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = NavyPrimary,
                        focusedLabelColor       = NavyPrimary,
                        focusedLeadingIconColor = NavyPrimary,
                    )
                )
                if (uiState is ProfileUiState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(uiState.message, color = ErrorRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = vm::tambahKasir,
                enabled  = uiState !is ProfileUiState.Loading,
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                if (uiState is ProfileUiState.Loading) {
                    CircularProgressIndicator(
                        color       = CardWhite,
                        modifier    = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Tambah")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextSecondary) }
        }
    )
}
