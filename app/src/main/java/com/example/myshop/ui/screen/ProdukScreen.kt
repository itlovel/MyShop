package com.example.myshop.ui.produk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.viewmodel.ProdukUiState
import com.example.myshop.viewmodel.ProdukViewModel

@Composable
fun ProdukScreen(

    onTambahClick: () -> Unit,

    onDetailClick: (String) -> Unit,

    produkViewModel: ProdukViewModel = viewModel()

) {

    val uiState =
        produkViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {

        produkViewModel.getProduk()
    }

    var search by remember {

        mutableStateOf("")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (val state = uiState.value) {

            is ProdukUiState.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            is ProdukUiState.Error -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(state.message)
                }
            }

            is ProdukUiState.Empty -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text("Produk kosong")
                }
            }

            is ProdukUiState.Success -> {

                val filteredData = state.data.filter {

                    it.nama.contains(
                        search,
                        ignoreCase = true
                    )
                }

                val totalProduk = filteredData.size

                val totalItem =
                    filteredData.sumOf {
                        it.stok
                    }

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
                                RoundedCornerShape(20.dp),

                            colors =
                                CardDefaults.cardColors(

                                    containerColor =
                                        MaterialTheme.colorScheme.surface
                                )
                        ) {

                            OutlinedTextField(

                                value = search,

                                onValueChange = {
                                    search = it
                                },

                                leadingIcon = {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.Search,

                                        contentDescription = null
                                    )
                                },

                                placeholder = {

                                    Text(
                                        "Search products..."
                                    )
                                },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),

                                shape =
                                    RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    item {

                        Card(

                            shape =
                                RoundedCornerShape(20.dp),

                            colors =
                                CardDefaults.cardColors(

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
                                        "RINGKASAN STOK",

                                    color =
                                        MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )
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
                                            "Total Produk"
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(8.dp)
                                        )

                                        Text(

                                            text =
                                                totalProduk.toString(),

                                            style =
                                                MaterialTheme.typography.headlineMedium
                                        )
                                    }

                                    Column(
                                        modifier =
                                            Modifier.weight(1f)
                                    ) {

                                        Text(
                                            "Total Item"
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.height(8.dp)
                                        )

                                        Text(

                                            text =
                                                totalItem.toInt().toString(),

                                            style =
                                                MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {

                        Text(

                            text =
                                "DAFTAR PRODUK",

                            color =
                                MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.6f
                                )
                        )
                    }

                    items(filteredData) { produk ->

                        ProdukItem(

                            produk = produk,

                            onDetailClick = {

                                onDetailClick(
                                    produk.id
                                )
                            }
                        )
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

        FloatingActionButton(

            onClick = onTambahClick,

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),

            containerColor =
                MaterialTheme.colorScheme.primary

        ) {

            Icon(

                imageVector =
                    Icons.Outlined.Add,

                contentDescription = null,

                tint =
                    MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}