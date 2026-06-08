package com.example.myshop.viewmodel

import com.example.myshop.model.Produk

sealed class ProdukUiState {

    object Loading : ProdukUiState()

    data class Success(
        val data: List<Produk>
    ) : ProdukUiState()

    data class Error(
        val message: String
    ) : ProdukUiState()

    object Empty : ProdukUiState()
}