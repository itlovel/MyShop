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
    object Stok     : Screen("stok")
    object Biaya    : Screen("biaya")
}

/**
 * Item untuk Bottom Navigation Bar.
 * Tambahkan entry baru di sini kalau ada tab baru.
 */
data class BottomNavItem(
    val screen  : Screen,
    val label   : String,
    val icon    : ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Beranda, "Beranda", Icons.Default.Home),
    BottomNavItem(Screen.Kasir,   "Kasir",   Icons.Default.ShoppingCart),
    BottomNavItem(Screen.Kas,     "Kas",     Icons.Default.AccountBalanceWallet),
    BottomNavItem(Screen.Stok,    "Stok",    Icons.Default.Inventory),
    BottomNavItem(Screen.Biaya,   "Biaya",   Icons.Default.TrendingDown),
)
