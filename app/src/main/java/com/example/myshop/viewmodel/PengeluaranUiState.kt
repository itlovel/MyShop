package com.example.myshop.viewmodel

import com.example.myshop.model.PengeluaranWithKas

sealed class PengeluaranUiState {
    object Loading : PengeluaranUiState()

    data class Success(
        val data: List<PengeluaranWithKas>
    ) : PengeluaranUiState()

    data class DetailSuccess(
        val data: PengeluaranWithKas
    ) : PengeluaranUiState()

    data class Error(
        val message: String
    ) : PengeluaranUiState()

    object Empty : PengeluaranUiState()
}
