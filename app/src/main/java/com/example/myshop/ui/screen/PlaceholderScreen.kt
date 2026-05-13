package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.ui.theme.*


@Composable
fun KasScreen() {
    PlaceholderScreen(
        icon  = Icons.Default.AccountBalanceWallet,
        title = "Kas",
        desc  = "Modul Manajemen Kas\n(Anggota A)"
    )
}

@Composable
fun StokScreen() {
    PlaceholderScreen(
        icon  = Icons.Default.Inventory,
        title = "Stok",
        desc  = "Modul Produk & Inventory\n(Kolaborasi Tim)"
    )
}

@Composable
fun BiayaScreen() {
    PlaceholderScreen(
        icon  = Icons.Default.TrendingDown,
        title = "Biaya",
        desc  = "Modul Pengeluaran Operasional\n(Anggota C)"
    )
}

@Composable
private fun PlaceholderScreen(icon: ImageVector, title: String, desc: String) {
    Box(
        modifier         = Modifier.fillMaxSize().background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier         = Modifier
                    .size(80.dp)
                    .background(ProductIconBg, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ProductIconTint, modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(desc, fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 20.sp)
        }
    }
}