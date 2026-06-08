package com.example.myshop.viewmodel

data class FormProdukState(

    val nama: String = "",

    val hargaBeli: String = "",

    val hargaJual: String = "",

    val stok: String = "",

    val isLoading: Boolean = false,

    val error: String? = null,

    val success: Boolean = false
)