package com.example.myshop.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.InventoryLog
import com.example.myshop.repository.ProdukRepository
import com.example.myshop.ui.components.DetailTopBar
import com.example.myshop.utils.formatRupiah
import com.example.myshop.viewmodel.DetailProdukUiState
import com.example.myshop.viewmodel.DetailProdukViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun DetailProdukScreen(
    produkId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit = {}
) {
    val viewModel: DetailProdukViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = ProdukRepository()

    LaunchedEffect(produkId) {
        viewModel.getDetailProduk(produkId)
    }

    Scaffold(
        topBar = {
            DetailTopBar(
                title = "Detail Produk",
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
                is DetailProdukUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is DetailProdukUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message)
                    }
                }

                is DetailProdukUiState.Success -> {
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
                                        text = state.produk.nama,
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "produk-${state.produk.id.take(5)}",
                                        color = Color.White.copy(alpha = 0.75f),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onEditClick(produkId) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Edit",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteOutline,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Nonaktifkan",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "INFORMASI PRODUK",
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
                                    DetailProdukRow(
                                        label = "Harga Jual",
                                        value = formatRupiah(state.produk.hargaJual)
                                    )

                                    DetailProdukRow(
                                        label = "Stok Saat Ini",
                                        value = "${state.produk.stok}"
                                    )

                                    DetailProdukRow(
                                        label = "Status",
                                        value = if (state.produk.isActive) "Aktif" else "Nonaktif"
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "RIWAYAT INVENTORY",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (state.logs.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Belum ada riwayat inventory", color = Color.Gray)
                                }
                            }
                        } else {
                            items(state.logs) { log ->
                                InventoryLogItem(log)
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = {
                        Text("Nonaktifkan Produk")
                    },
                    text = {
                        Text("Apakah anda yakin ingin menonaktifkan produk ini?")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    repository.nonaktifkanProduk(produkId)
                                    showDeleteDialog = false
                                    onBackClick()
                                }
                            }
                        ) {
                            Text("Ya")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Tidak")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailProdukRow(
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

@Composable
fun InventoryLogItem(log: InventoryLog) {
    val isMasuk = log.deltaStok >= 0
    val nominalColor =
        if (isMasuk) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
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
                    text = log.jenis,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = log.keterangan,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = (if (isMasuk) "+" else "-") + abs(log.deltaStok).toString(),
                fontWeight = FontWeight.ExtraBold,
                color = nominalColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
