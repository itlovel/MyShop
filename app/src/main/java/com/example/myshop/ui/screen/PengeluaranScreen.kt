package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.PengeluaranWithKas
import com.example.myshop.utils.formatRupiah
import com.example.myshop.viewmodel.PengeluaranUiState
import com.example.myshop.viewmodel.PengeluaranViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PengeluaranScreen(
    onTambahClick: () -> Unit,
    onDetailClick: (String) -> Unit,
    pengeluaranViewModel: PengeluaranViewModel = viewModel()
) {
    val uiState =
        pengeluaranViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        pengeluaranViewModel.getPengeluaran()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState.value) {
            is PengeluaranUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PengeluaranUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }

            is PengeluaranUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pengeluaran kosong")
                }
            }

            is PengeluaranUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "RINGKASAN PENGELUARAN",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Total Transaksi",
                                            color = Color.White.copy(alpha = 0.75f)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = state.data.size.toString(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = Color.White
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Total Nominal",
                                            color = Color.White.copy(alpha = 0.75f)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = formatRupiah(
                                                state.data.sumOf { it.pengeluaran.total }
                                            ),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "DAFTAR PENGELUARAN",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    items(state.data) { item ->
                        PengeluaranItem(
                            data = item,
                            onDetailClick = {
                                onDetailClick(item.pengeluaran.id)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            is PengeluaranUiState.DetailSuccess -> Unit
        }

        FloatingActionButton(
            onClick = onTambahClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun PengeluaranItem(
    data: PengeluaranWithKas,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.namaKas,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatTanggalPengeluaran(data.pengeluaran.tanggal),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.pengeluaran.deskripsi,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatRupiah(data.pengeluaran.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDetailClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Lihat Detail Pengeluaran")
            }
        }
    }
}

fun formatTanggalPengeluaran(tanggal: String): String {
    return try {
        val parsed = LocalDate.parse(tanggal.take(10))
        val formatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("id-ID")
        )
        parsed.format(formatter)
    } catch (e: Exception) {
        tanggal
    }
}
