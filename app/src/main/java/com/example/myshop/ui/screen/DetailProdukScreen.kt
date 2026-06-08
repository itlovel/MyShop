package com.example.myshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.InventoryLog
import com.example.myshop.repository.ProdukRepository
import com.example.myshop.utils.formatRupiah
import com.example.myshop.viewmodel.DetailProdukUiState
import com.example.myshop.viewmodel.DetailProdukViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailProdukScreen(

    produkId: String,

    onBackClick: () -> Unit,

    onEditClick: (String) -> Unit = {}

) {

    val viewModel: DetailProdukViewModel =
        viewModel()

    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle()

    var showDeleteDialog by remember {

        mutableStateOf(false)
    }

    val scope =
        rememberCoroutineScope()

    val repository =
        ProdukRepository()

    LaunchedEffect(Unit) {

        viewModel.getDetailProduk(produkId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val state = uiState.value) {

            is DetailProdukUiState.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
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

                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background
                        )
                        .padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {

                    item {

                        Card(
                            shape =
                                RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(

                                containerColor =
                                    MaterialTheme.colorScheme.surface
                            )

                        ) {

                            Column(
                                modifier =
                                    Modifier.padding(20.dp)
                            ) {

                                Text(
                                    text =
                                        state.produk.nama,

                                    style =
                                        MaterialTheme.typography.headlineSmall,

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(20.dp)
                                )

                                Row {

                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {

                                        Text(
                                            "Harga Jual",
                                            color = Color.Gray
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(8.dp)
                                        )

                                        Text(
                                            text =
                                                formatRupiah(
                                                    state.produk.hargaJual
                                                ),

                                            style =
                                                MaterialTheme.typography.titleLarge
                                        )
                                    }

                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {

                                        Text(
                                            "Stok Saat Ini",
                                            color = Color.Gray
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(8.dp)
                                        )

                                        Text(
                                            text =
                                                "${state.produk.stok}",

                                            style =
                                                MaterialTheme.typography.titleLarge,

                                            color =
                                                Color(0xFF2E7D32)
                                        )
                                    }
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(20.dp)
                                )

                                OutlinedButton(

                                    onClick = {

                                        onEditClick(produkId)
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.Edit,

                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text(
                                        "Edit Produk"
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                OutlinedButton(

                                    onClick = {

                                        showDeleteDialog = true
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.DeleteOutline,

                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text(
                                        "Nonaktifkan Produk"
                                    )
                                }
                            }
                        }
                    }

                    item {

                        Text(
                            text = "RIWAYAT INVENTORY",
                            color = Color.Gray
                        )
                    }

                    items(state.logs) { log ->

                        InventoryLogItem(log)
                    }

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(100.dp)
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {

            AlertDialog(

                onDismissRequest = {

                    showDeleteDialog = false
                },

                title = {

                    Text(
                        "Nonaktifkan Produk"
                    )
                },

                text = {

                    Text(
                        "Apakah anda yakin ingin menonaktifkan produk ini?"
                    )
                },

                confirmButton = {

                    TextButton(

                        onClick = {

                            scope.launch {

                                repository.nonaktifkanProduk(
                                    produkId
                                )

                                showDeleteDialog =
                                    false

                                onBackClick()
                            }
                        }
                    ) {

                        Text("Ya")
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {

                            showDeleteDialog =
                                false
                        }
                    ) {

                        Text("Tidak")
                    }
                }
            )
        }
    }
}

@Composable
fun InventoryLogItem(
    log: InventoryLog
) {

    val badgeColor =
        when (log.jenis.uppercase()) {

            "MASUK" -> Color(0xFF2E7D32)

            "KELUAR" -> Color(0xFFC62828)

            "TERJUAL" -> Color(0xFFFF8F00)

            "EDIT" -> Color(0xFF1565C0)

            else -> Color.Gray
        }

    Card(

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(

            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(

                    color = badgeColor,

                    shape =
                        RoundedCornerShape(50)
                ) {

                    Text(

                        text = log.jenis,

                        color = Color.White,

                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(
                    text = log.keterangan
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        "Sebelum",
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        "${log.stokSebelum}"
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        "Delta",
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        "${log.deltaStok}"
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        "Sesudah",
                        color = Color.Gray
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        "${log.stokSesudah}"
                    )
                }
            }
        }
    }
}
