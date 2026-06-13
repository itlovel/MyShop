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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myshop.model.Pelanggan
import com.example.myshop.model.PelangganLog
import com.example.myshop.ui.theme.BackgroundLight
import com.example.myshop.ui.theme.CardWhite
import com.example.myshop.ui.theme.SuccessGreen
import com.example.myshop.ui.theme.TextPrimary
import com.example.myshop.ui.theme.TextSecondary
import com.example.myshop.viewmodel.PelangganViewModel
import com.example.myshop.viewmodel.UiState

@Composable
fun PelangganListScreen(
    onAddClick: () -> Unit,
    onEditClick: (Pelanggan) -> Unit,
    onLogClick: (String) -> Unit,
    viewModel: PelangganViewModel = viewModel()
) {
    val state by viewModel.pelangganState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchPelanggan()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Tambah pelanggan"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
        ) {
            when (val uiState = state) {
                UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is UiState.Success -> {
                    PelangganListContent(
                        pelangganList = uiState.data,
                        onEditClick = onEditClick,
                        onLogClick = onLogClick
                    )
                }
            }
        }
    }
}

@Composable
private fun PelangganListContent(
    pelangganList: List<Pelanggan>,
    onEditClick: (Pelanggan) -> Unit,
    onLogClick: (String) -> Unit
) {
    if (pelangganList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada pelanggan", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Daftar Pelanggan",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(
            items = pelangganList,
            key = { it.id }
        ) { pelanggan ->
            PelangganItem(
                pelanggan = pelanggan,
                onEditClick = onEditClick,
                onLogClick = onLogClick
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PelangganItem(
    pelanggan: Pelanggan,
    onEditClick: (Pelanggan) -> Unit,
    onLogClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pelanggan.nama,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = pelanggan.noTelp ?: "-",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusBadge(isActive = pelanggan.isActive)
            }

            IconButton(onClick = { onLogClick(pelanggan.id) }) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = "Riwayat pelanggan"
                )
            }

            IconButton(onClick = { onEditClick(pelanggan) }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit pelanggan"
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    val backgroundColor = if (isActive) {
        Color(0xFFE8F5ED)
    } else {
        Color(0xFFF3F4F6)
    }

    val textColor = if (isActive) {
        SuccessGreen
    } else {
        TextSecondary
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isActive) "Aktif" else "Non-aktif",
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PelangganFormScreen(
    pelanggan: Pelanggan? = null,
    onSaved: () -> Unit,
    viewModel: PelangganViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    var nama by remember(pelanggan?.id) {
        mutableStateOf(pelanggan?.nama.orEmpty())
    }

    var noTelp by remember(pelanggan?.id) {
        mutableStateOf(pelanggan?.noTelp.orEmpty())
    }

    var isActive by remember(pelanggan?.id) {
        mutableStateOf(pelanggan?.isActive ?: true)
    }

    var hasSubmitted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(formState, hasSubmitted) {
        if (hasSubmitted && formState is UiState.Success) {
            hasSubmitted = false
            onSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (pelanggan == null) "Tambah Pelanggan" else "Edit Profil Pelanggan",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = noTelp,
            onValueChange = { noTelp = it },
            label = { Text("No telp") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Status pelanggan",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (isActive) "Aktif" else "Non-aktif",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        when (val state = formState) {
            UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Success -> Unit
        }

        Button(
            onClick = {
                hasSubmitted = true

                if (pelanggan == null) {
                    viewModel.addPelanggan(
                        nama = nama,
                        noTelp = noTelp
                    )
                } else {
                    viewModel.editPelanggan(
                        id = pelanggan.id,
                        nama = nama,
                        noTelp = noTelp,
                        isActive = isActive
                    )
                }
            },
            enabled = formState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Save,
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(if (pelanggan == null) "Simpan" else "Simpan Perubahan")
        }
    }
}

@Composable
fun PelangganLogScreen(
    pelangganId: String,
    viewModel: PelangganViewModel = viewModel()
) {
    val state by viewModel.pelangganLogState.collectAsStateWithLifecycle()

    LaunchedEffect(pelangganId) {
        viewModel.fetchPelangganLog(pelangganId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp)
    ) {
        when (val uiState = state) {
            UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success -> {
                PelangganLogContent(logs = uiState.data)
            }
        }
    }
}

@Composable
private fun PelangganLogContent(logs: List<PelangganLog>) {
    if (logs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada riwayat pelanggan", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = logs,
            key = { it.id }
        ) { log ->
            PelangganLogItem(log = log)
        }
    }
}

@Composable
private fun PelangganLogItem(log: PelangganLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = log.labelAksi,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            log.createdAt?.let {
                Text(
                    text = it,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            ChangeLine(
                label = "Nama",
                before = log.namaSebelum,
                after = log.namaSesudah
            )

            ChangeLine(
                label = "No telp",
                before = log.noTelpSebelum,
                after = log.noTelpSesudah
            )

            ChangeLine(
                label = "Status",
                before = log.isActiveSebelum?.toStatusText(),
                after = log.isActiveSesudah?.toStatusText()
            )
        }
    }
}

@Composable
private fun ChangeLine(
    label: String,
    before: String?,
    after: String?
) {
    if (before == null && after == null) return

    Text(
        text = "$label: ${before ?: "-"} -> ${after ?: "-"}",
        color = TextPrimary,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun Boolean.toStatusText(): String {
    return if (this) "Aktif" else "Non-aktif"
}