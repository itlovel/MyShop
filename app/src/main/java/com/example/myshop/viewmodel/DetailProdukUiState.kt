package com.example.myshop.viewmodel

import com.example.myshop.model.InventoryLog
import com.example.myshop.model.Produk

sealed class DetailProdukUiState {

    object Loading : DetailProdukUiState()

    data class Success(

        val produk: Produk,

        val logs: List<InventoryLog>

    ) : DetailProdukUiState()

    data class Error(

        val message: String

    ) : DetailProdukUiState()
}