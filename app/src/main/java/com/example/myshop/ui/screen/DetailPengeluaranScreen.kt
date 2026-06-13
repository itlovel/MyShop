package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.ui.components.DetailTopBar
import com.example.myshop.utils.formatRupiah
import com.example.myshop.viewmodel.PengeluaranUiState
import com.example.myshop.viewmodel.PengeluaranViewModel

@Composable
fun DetailPengeluaranScreen(
    pengeluaranId: String,
    onBackClick: () -> Unit,
    pengeluaranViewModel: PengeluaranViewModel = viewModel()
) {
    val uiState =
        pengeluaranViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pengeluaranId) {
        pengeluaranViewModel.getDetailPengeluaran(pengeluaranId)
    }

    Scaffold(
        topBar = {
            DetailTopBar(
                title = "Detail Pengeluaran",
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            when (val state = uiState.value) {
                is PengeluaranUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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

                is PengeluaranUiState.DetailSuccess -> {
                    val pengeluaran = state.data.pengeluaran

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
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
                                        text = state.data.namaKas,
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = formatRupiah(pengeluaran.total),
                                        color = Color.White.copy(alpha = 0.75f),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "DETAIL PENGELUARAN",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    DetailPengeluaranRow(
                                        label = "Tanggal",
                                        value = formatTanggalPengeluaran(pengeluaran.tanggal)
                                    )

                                    DetailPengeluaranRow(
                                        label = "Deskripsi",
                                        value = pengeluaran.deskripsi
                                    )

                                    DetailPengeluaranRow(
                                        label = "Status",
                                        value = pengeluaran.status
                                    )
                                }
                            }
                        }
                    }
                }

                is PengeluaranUiState.Empty -> Unit
                is PengeluaranUiState.Success -> Unit
            }
        }
    }
}

@Composable
private fun DetailPengeluaranRow(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
