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
    object Biaya    : Screen("biaya")

    // Produk
    object TambahProduk : Screen("tambah_produk")
    object DetailProduk : Screen("detail_produk/{produkId}") {
        fun createRoute(produkId: String) = "detail_produk/$produkId"
    }
    object EditProduk : Screen("edit_produk/{produkId}") {
        fun createRoute(produkId: String) = "edit_produk/$produkId"
    }

    // Profil
    object Profile : Screen("profile")
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
    BottomNavItem(Screen.Biaya,   "Biaya",   Icons.Default.TrendingDown),
)
