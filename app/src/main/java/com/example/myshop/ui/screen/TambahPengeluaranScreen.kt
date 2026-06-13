package com.example.myshop.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.viewmodel.FormPengeluaranState
import com.example.myshop.viewmodel.FormPengeluaranViewModel

@Composable
fun TambahPengeluaranScreen(
    onBackClick: () -> Unit
) {
    val viewModel: FormPengeluaranViewModel =
        viewModel()

    val state =
        viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.success) {
        if (state.value.success) {
            onBackClick()
        }
    }

    TambahPengeluaranContent(
        state = state.value,
        onKasChange = viewModel::updateKasId,
        onTanggalChange = viewModel::updateTanggal,
        onDeskripsiChange = viewModel::updateDeskripsi,
        onTotalChange = viewModel::updateTotal,
        onSaveClick = viewModel::simpanPengeluaran
    )
}

@Composable
fun TambahPengeluaranContent(
    state: FormPengeluaranState,
    onKasChange: (String) -> Unit,
    onTanggalChange: (String) -> Unit,
    onDeskripsiChange: (String) -> Unit,
    onTotalChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tambah Pengeluaran",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        KasDropdown(
            state = state,
            onKasChange = onKasChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.tanggal,
            onValueChange = onTanggalChange,
            label = {
                Text("Tanggal")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.deskripsi,
            onValueChange = onDeskripsiChange,
            label = {
                Text("Deskripsi")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.total,
            onValueChange = onTotalChange,
            label = {
                Text("Total")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Simpan Pengeluaran")
            }
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun KasDropdown(
    state: FormPengeluaranState,
    onKasChange: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val selectedKas =
        state.daftarKas.firstOrNull { it.id == state.kasId }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedKas?.namaKas ?: "Pilih Kas"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            state.daftarKas.forEach { kas ->
                DropdownMenuItem(
                    text = {
                        Text(kas.namaKas)
                    },
                    onClick = {
                        onKasChange(kas.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
