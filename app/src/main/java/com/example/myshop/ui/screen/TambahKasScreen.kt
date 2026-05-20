package com.example.myshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myshop.viewmodel.KasUiState

@Composable
fun TambahKasScreen(
    namaKas: String,
    saldoAwal: String,
    uiState: KasUiState,
    onNamaKasChange: (String) -> Unit,
    onSaldoAwalChange: (String) -> Unit,
    onSimpanClick: () -> Unit,
    onNavigateBack: () -> Unit // Untuk tombol kembali/batal
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Buat Akun Kas Baru",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = namaKas,
            onValueChange = onNamaKasChange,
            label = { Text("Nama Kas (Contoh: Main Cash Register)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = saldoAwal,
            onValueChange = onSaldoAwalChange,
            label = { Text("Saldo Awal") },
            // Menampilkan keyboard angka agar user tidak mengetik huruf
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSimpanClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is KasUiState.Loading
        ) {
            // Logika loading persis seperti LoginScreen
            if (uiState is KasUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Simpan Kas")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Batal")
        }

        /*
         * Menampilkan pesan error jika terjadi kegagalan saat simpan
         */
        if (uiState is KasUiState.Error) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
