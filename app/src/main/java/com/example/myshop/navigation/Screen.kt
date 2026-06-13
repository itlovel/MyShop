package com.example.myshop.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {

    // Auth
    object Login    : Screen("login")
    object Register : Screen("register")

    // Main – bottom nav
    object Beranda  : Screen("beranda")
    object Kasir    : Screen("kasir")
    object Kas      : Screen("kas")
    object TambahKas : Screen("tambah_kas")
    object DetailKas : Screen("detail_kas")
    object Stok     : Screen("stok")
    object Pengeluaran : Screen("pengeluaran")

    // Produk
    object TambahProduk : Screen("tambah_produk")
    object DetailProduk : Screen("detail_produk/{produkId}") {
        fun createRoute(produkId: String) = "detail_produk/$produkId"
    }
    object EditProduk : Screen("edit_produk/{produkId}") {
        fun createRoute(produkId: String) = "edit_produk/$produkId"
    }

    // Pengeluaran
    object TambahPengeluaran : Screen("tambah_pengeluaran")
    object DetailPengeluaran : Screen("detail_pengeluaran/{pengeluaranId}") {
        fun createRoute(pengeluaranId: String) = "detail_pengeluaran/$pengeluaranId"
    }

    // Profil
    object Profile : Screen("profile")

    // Pelanggan
    object Pelanggan : Screen("pelanggan")
    object TambahPelanggan : Screen("tambah_pelanggan")
    object EditPelanggan : Screen("edit_pelanggan/{pelangganId}") {
        fun createRoute(pelangganId: String) = "edit_pelanggan/$pelangganId"
    }
    object PelangganLog : Screen("pelanggan_log/{pelangganId}") {
        fun createRoute(pelangganId: String) = "pelanggan_log/$pelangganId"
    }
}

data class BottomNavItem(
    val screen : Screen,
    val label  : String,
    val icon   : ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Beranda, "Beranda", Icons.Default.Home),
    BottomNavItem(Screen.Kasir,   "Kasir",   Icons.Default.ShoppingCart),
    BottomNavItem(Screen.Kas,     "Kas",     Icons.Default.AccountBalanceWallet),
    BottomNavItem(Screen.Stok,    "Stok",    Icons.Default.Inventory),
    BottomNavItem(Screen.Pengeluaran, "Pengeluaran", Icons.Default.TrendingDown),
)
