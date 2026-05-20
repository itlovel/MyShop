package com.example.myshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.viewmodel.FormProdukState
import com.example.myshop.viewmodel.FormProdukViewModel

@Composable
fun TambahProdukScreen(

    onBackClick: () -> Unit

) {

    val viewModel: FormProdukViewModel =
        viewModel()

    val state =
        viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.value.success) {

        if (state.value.success) {

            onBackClick()
        }
    }

    TambahProdukContent(

        title = "Tambah Produk",

        state = state.value,

        onNamaChange =
            viewModel::updateNama,

        onHargaBeliChange =
            viewModel::updateHargaBeli,

        onHargaJualChange =
            viewModel::updateHargaJual,

        onStokChange =
            viewModel::updateStok,

        onSaveClick = {

            viewModel.simpanProduk()
        }
    )
}

@Composable
fun TambahProdukContent(

    title: String,

    state: FormProdukState,

    onNamaChange: (String) -> Unit,

    onHargaBeliChange: (String) -> Unit,

    onHargaJualChange: (String) -> Unit,

    onStokChange: (String) -> Unit,

    onSaveClick: () -> Unit

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(
                rememberScrollState()
            )

    ) {

        Text(
            text = title,

            style =
                MaterialTheme.typography.headlineSmall
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(

            value = state.nama,

            onValueChange = onNamaChange,

            label = {
                Text("Nama Produk")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = state.hargaBeli,

            onValueChange =
                onHargaBeliChange,

            label = {
                Text("Harga Beli")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = state.hargaJual,

            onValueChange =
                onHargaJualChange,

            label = {
                Text("Harga Jual")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(

            value = state.stok,

            onValueChange =
                onStokChange,

            label = {
                Text("Stok")
            },

            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(

            onClick = onSaveClick,

            modifier = Modifier.fillMaxWidth(),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Color(0xFF1A2B48)
                )
        ) {

            if (state.isLoading) {

                CircularProgressIndicator(
                    color = Color.White
                )

            } else {

                Text("Simpan Produk")
            }
        }

        state.error?.let {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = it,
                color = Color.Red
            )
        }
    }
}