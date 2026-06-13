package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myshop.formatRupiah
import com.example.myshop.model.KasModels
import com.example.myshop.viewmodel.KasUiState

@Composable
fun KasListScreen(
    daftarKas: List<KasModels>,
    uiState: KasUiState,
    isLoadingData: Boolean,
    onTambahKasClick: () -> Unit = {},
    onDetailClick: (KasModels) -> Unit = {},
    onToggleStatus: (KasModels) -> Unit = {},
    onRetryLoad: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Menggunakan F8F9FA
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // --- HEADER & TOMBOL AKSI ---
            item {
                // 1. KARTU SALDO (Deep Navy)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "TOTAL SALDO KAS",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val totalSaldo = daftarKas.filter { it.isActive }.sumOf { it.saldo }
                        Text(
                            text = formatRupiah(totalSaldo),
                            color = Color.White,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // button untuk tambah kas baru
                Button(
                    onClick = onTambahKasClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Kuning FFD633
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp) ,
                    border = androidx.compose.foundation.BorderStroke (1.dp,
                        MaterialTheme.colorScheme.outline // StrokeGray DEE2E6
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buat Akun Kas Baru",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Label Daftar
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "DAFTAR AKUN KAS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // --- LIST KARTU KAS ---
            when {
                isLoadingData -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                uiState is KasUiState.Error -> {
                    val errorMessage = uiState.message
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.25f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = onRetryLoad) {
                                    Text("Muat ulang")
                                }
                            }
                        }
                    }
                }

                daftarKas.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Belum ada akun kas", color = Color.Gray)
                        }
                    }
                }

                else -> {
                    items(daftarKas) { kas ->
                        ItemKartuKas(
                            kas = kas,
                            onDetailClick = { onDetailClick(kas) },
                            onToggleStatus = { onToggleStatus(kas) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemKartuKas(
    kas: KasModels,
    onDetailClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline // StrokeGray DEE2E6
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = kas.namaKas,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "cash-${kas.id.take(5)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Status Badge (Hidup & Bisa Diklik)
                StatusBadge(isActive = kas.isActive, onClick = onToggleStatus)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Text(
                text = formatRupiah(kas.saldo),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDetailClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lihat Detail Transaksi")
            }
        }
    }
}

@Composable
fun StatusBadge(isActive: Boolean, onClick: () -> Unit) {
    val greenBase = MaterialTheme.colorScheme.tertiary // EmeraldGreen
    val redBase = MaterialTheme.colorScheme.error     // BrightRed

    val bgColor = if (isActive) greenBase.copy(alpha = 0.15f) else redBase.copy(alpha = 0.15f)
    val txtColor = if (isActive) greenBase else redBase

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isActive) "Aktif" else "Nonaktif",
            style = MaterialTheme.typography.labelMedium,
            color = txtColor,
            fontWeight = FontWeight.Bold
        )
    }
}
