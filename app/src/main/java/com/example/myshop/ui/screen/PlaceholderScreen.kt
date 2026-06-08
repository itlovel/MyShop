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
import com.example.myshop.ui.produk.ProdukScreen
import com.example.myshop.ui.theme.*
import com.example.myshop.viewmodel.ProdukViewModel

@Composable
fun StokScreen(
    vm: ProdukViewModel,
    onTambahClick: () -> Unit,
    onDetailClick: (String) -> Unit
) {
    ProdukScreen(
        onTambahClick = onTambahClick,
        onDetailClick = onDetailClick,
        produkViewModel = vm
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
