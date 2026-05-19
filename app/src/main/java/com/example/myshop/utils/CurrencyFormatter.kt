package com.example.myshop.utils

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(
    amount: Double
): String {

    val localeID =
        Locale("in", "ID")

    val format =
        NumberFormat.getCurrencyInstance(
            localeID
        )

    return format.format(amount)
}