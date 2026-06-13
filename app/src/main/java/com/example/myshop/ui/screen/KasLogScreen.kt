package com.example.myshop.ui.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myshop.formatRupiah
import com.example.myshop.model.KasModels
import com.example.myshop.model.KasLogModels
import com.example.myshop.viewmodel.KasUiState

@Composable
fun KasLogScreen(
    kas: KasModels?,
    logs: List<KasLogModels>,
    uiState: KasUiState,
    onNavigateBack: () -> Unit,
    onManualTransactionClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            kas?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 28.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = it.namaKas,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "cash-${it.id.take(5)}",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "RIWAYAT TRANSAKSI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState is KasUiState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (uiState is KasUiState.Error) {
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
                    Text(
                        text = (uiState as KasUiState.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada riwayat transaksi", color = Color.Gray)
                }
            }
        } else {
            items(logs) { log ->
                LogItem(log)
            }
        }
    }
}

@Composable
fun LogItem(log: KasLogModels) {
    val perubahanSaldo = log.saldoSesudah - log.saldoSebelum
    val isMasuk = perubahanSaldo >= 0
    val nominalTampil = if (perubahanSaldo != 0.0) kotlin.math.abs(perubahanSaldo) else kotlin.math.abs(log.nominal)
    val nominalColor = if (isMasuk) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val keterangan = log.keterangan?.takeIf { it.isNotBlank() } ?: log.jenis.replaceFirstChar { it.uppercase() }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatTanggalKasLog(log.tanggalTransaksi),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = keterangan,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = (if (isMasuk) "+" else "-") + formatRupiah(nominalTampil),
                fontWeight = FontWeight.ExtraBold,
                color = nominalColor,
                fontSize = 15.sp
            )
        }
    }
}

private fun formatTanggalKasLog(rawDate: String): String {
    val datePart = rawDate.substringBefore("T")
    val timePart = rawDate.substringAfter("T", "").take(5)

    return if (timePart.isBlank()) datePart else "$datePart, $timePart"
}
