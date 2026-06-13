package com.example.myshop.viewmodel

import com.example.myshop.model.KasModels

data class FormPengeluaranState(
    val daftarKas: List<KasModels> = emptyList(),
    val kasId: String = "",
    val tanggal: String = "",
    val deskripsi: String = "",
    val total: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)
