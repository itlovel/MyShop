package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.ui.theme.*

/**
 * Beranda – Halaman utama setelah login
 * Menampilkan ringkasan bisnis hari ini.
 */
@Composable
fun BerandaScreen(
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        //  Greeting Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyPrimary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text("Selamat Datang 👋", color = CardWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                Text(
                    "Toko Ibu Sari",
                    color      = CardWhite,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Rabu, 13 Mei 2026",
                    color    = CardWhite.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        //  Ringkasan Hari Ini
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Ringkasan Hari Ini",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier    = Modifier.weight(1f),
                    label       = "Penjualan",
                    value       = "Rp 450.000",
                    icon        = Icons.Default.ShoppingCart,
                    iconBgColor = ProductIconBg,
                    iconTint    = ProductIconTint
                )
                SummaryCard(
                    modifier    = Modifier.weight(1f),
                    label       = "Pengeluaran",
                    value       = "Rp 75.000",
                    icon        = Icons.Default.TrendingDown,
                    iconBgColor = androidx.compose.ui.graphics.Color(0xFFFFF3E0),
                    iconTint    = androidx.compose.ui.graphics.Color(0xFFE65100)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard(
                    modifier    = Modifier.weight(1f),
                    label       = "Saldo Kas",
                    value       = "Rp 2.300.000",
                    icon        = Icons.Default.AccountBalanceWallet,
                    iconBgColor = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
                    iconTint    = androidx.compose.ui.graphics.Color(0xFF1565C0)
                )
                SummaryCard(
                    modifier    = Modifier.weight(1f),
                    label       = "Transaksi",
                    value       = "12 Transaksi",
                    icon        = Icons.Default.Receipt,
                    iconBgColor = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
                    iconTint    = androidx.compose.ui.graphics.Color(0xFF6A1B9A)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        //  Menu Cepat
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Menu Cepat",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMenuButton(
                    modifier = Modifier.weight(1f),
                    label    = "Transaksi Baru",
                    icon     = Icons.Default.AddShoppingCart
                )
                QuickMenuButton(
                    modifier = Modifier.weight(1f),
                    label    = "Tambah Produk",
                    icon     = Icons.Default.Inventory
                )
                QuickMenuButton(
                    modifier = Modifier.weight(1f),
                    label    = "Laporan",
                    icon     = Icons.Default.BarChart
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        //  Logout
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedButton(
                onClick  = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                border   = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
            ) {
                Icon(Icons.Default.Logout, null, tint = ErrorRed)
                Spacer(Modifier.width(8.dp))
                Text("Keluar", color = ErrorRed, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

// Sub-composables

@Composable
private fun SummaryCard(
    modifier    : Modifier,
    label       : String,
    value       : String,
    icon        : ImageVector,
    iconBgColor : androidx.compose.ui.graphics.Color,
    iconTint    : androidx.compose.ui.graphics.Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
private fun QuickMenuButton(
    modifier : Modifier,
    label    : String,
    icon     : ImageVector
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ProductIconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = ProductIconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text      = label,
                fontSize  = 10.sp,
                color     = TextPrimary,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
