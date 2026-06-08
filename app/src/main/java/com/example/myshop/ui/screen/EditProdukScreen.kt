package com.example.myshop.ui.screen

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.viewmodel.EditProdukViewModel
import com.example.myshop.viewmodel.DetailProdukViewModel
import com.example.myshop.viewmodel.DetailProdukUiState

@Composable
fun EditProdukScreen(

    produkId: String,

    onBackClick: () -> Unit

) {

    val detailViewModel:
            DetailProdukViewModel = viewModel()

    val editViewModel:
            EditProdukViewModel = viewModel()

    val detailState =
        detailViewModel.uiState
            .collectAsStateWithLifecycle()

    val editState =
        editViewModel.state
            .collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {

        detailViewModel.getDetailProduk(
            produkId
        )
    }

    LaunchedEffect(detailState.value) {

        if (
            detailState.value
                    is DetailProdukUiState.Success
        ) {

            val produk =
                (detailState.value
                        as DetailProdukUiState.Success)
                    .produk

            editViewModel.loadProduk(
                produk
            )
        }
    }

    LaunchedEffect(editState.value.success) {

        if (editState.value.success) {

            onBackClick()
        }
    }

    TambahProdukContent(

        title = "Edit Produk",

        state = editState.value,

        onNamaChange =
            editViewModel::updateNama,

        onHargaBeliChange =
            editViewModel::updateHargaBeli,

        onHargaJualChange =
            editViewModel::updateHargaJual,

        onStokChange =
            editViewModel::updateStok,

        onSaveClick = {

            editViewModel.simpanProduk()
        }
    )
}