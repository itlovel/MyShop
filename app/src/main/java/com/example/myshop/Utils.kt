package com.example.myshop

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(nominal: Double): String {
    // 1. Menentukan lokasi/wilayah (Locale)
    // "id" untuk bahasa Indonesia, "ID" untuk negara Indonesia
    val localeID = Locale("id", "ID")

    // 2. Membuat alat pemformat angka berdasarkan lokasi tersebut
    val formatter = NumberFormat.getNumberInstance(localeID)

    // 3. Memformat angka (misal: 15750000.0 menjadi 15.750.000)
    val angkaTerformat = formatter.format(nominal)

    // 4. Menggabungkannya dengan teks "Rp "
    return "Rp $angkaTerformat"
}