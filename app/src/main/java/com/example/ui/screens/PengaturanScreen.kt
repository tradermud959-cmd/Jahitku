package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.CardDark
import com.example.ui.theme.ErrorRed
import com.example.viewmodel.JahitKuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import com.example.data.Jahitan
import com.example.data.Permakan
import com.example.data.Transaksi
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengaturanScreen(navController: NavController, viewModel: JahitKuViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val allJahitan by viewModel.allJahitan.collectAsStateWithLifecycle()
    val allPermakan by viewModel.allPermakan.collectAsStateWithLifecycle()
    val allTransaksi by viewModel.allTransaksi.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showNamaTokoDialog by remember { mutableStateOf(false) }
    var tempNamaToko by remember { mutableStateOf("") }
    val currentNamaToko by viewModel.namaToko.collectAsStateWithLifecycle()

    val showToast: (String) -> Unit = { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val root = JSONObject()
                    
                    val jahitArray = JSONArray()
                    allJahitan.forEach {
                        val obj = JSONObject()
                        obj.put("nama", it.nama)
                        obj.put("kategori", it.kategori)
                        obj.put("harga", it.harga)
                        obj.put("catatan", it.catatan)
                        jahitArray.put(obj)
                    }
                    root.put("jahitan", jahitArray)

                    val permakArray = JSONArray()
                    allPermakan.forEach {
                        val obj = JSONObject()
                        obj.put("nama", it.nama)
                        obj.put("kategori", it.kategori)
                        obj.put("harga", it.harga)
                        obj.put("catatan", it.catatan)
                        permakArray.put(obj)
                    }
                    root.put("permakan", permakArray)

                    val transArray = JSONArray()
                    allTransaksi.forEach {
                        val obj = JSONObject()
                        obj.put("namaPelanggan", it.namaPelanggan)
                        obj.put("nomorHp", it.nomorHp)
                        obj.put("itemsJson", it.itemsJson)
                        obj.put("totalHarga", it.totalHarga)
                        obj.put("uangPelanggan", it.uangPelanggan)
                        obj.put("kembalian", it.kembalian)
                        obj.put("timestamp", it.timestamp)
                        transArray.put(obj)
                    }
                    root.put("transaksi", transArray)

                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(root.toString(2).toByteArray())
                        }
                    }
                    showToast("Backup berhasil disimpan")
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Gagal membuat backup")
                }
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                try {
                    val jsonString = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(it)?.use { inputStream ->
                            BufferedReader(InputStreamReader(inputStream)).readText()
                        }
                    } ?: return@launch

                    val root = JSONObject(jsonString)
                    
                    val jahitArray = root.optJSONArray("jahitan")
                    if (jahitArray != null) {
                        for (i in 0 until jahitArray.length()) {
                            val obj = jahitArray.getJSONObject(i)
                            viewModel.addJahitan(
                                obj.getString("nama"),
                                obj.getString("kategori"),
                                obj.getDouble("harga"),
                                obj.optString("catatan", "")
                            )
                        }
                    }

                    val permakArray = root.optJSONArray("permakan")
                    if (permakArray != null) {
                        for (i in 0 until permakArray.length()) {
                            val obj = permakArray.getJSONObject(i)
                            viewModel.addPermakan(
                                obj.getString("nama"),
                                obj.getString("kategori"),
                                obj.getDouble("harga"),
                                obj.optString("catatan", "")
                            )
                        }
                    }

                    val transArray = root.optJSONArray("transaksi")
                    if (transArray != null) {
                        for (i in 0 until transArray.length()) {
                            val obj = transArray.getJSONObject(i)
                            viewModel.addTransaksi(
                                namaPelanggan = obj.getString("namaPelanggan"),
                                nomorHp = obj.getString("nomorHp"),
                                items = viewModel.parseTransaksiItems(obj.getString("itemsJson")),
                                uangPelanggan = obj.getDouble("uangPelanggan")
                            )
                        }
                    }

                    showToast("Restore berhasil")
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast("Gagal melakukan restore")
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Semua Data") },
            text = { Text("Peringatan: Tindakan ini akan menghapus semua data (Jahitan, Permakan, dan Riwayat Transaksi). Data yang dihapus tidak dapat dikembalikan. Lanjutkan?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showResetDialog = false
                    showToast("Data berhasil direset")
                }) {
                    Text("Hapus", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showNamaTokoDialog) {
        AlertDialog(
            onDismissRequest = { showNamaTokoDialog = false },
            title = { Text("Nama Toko") },
            text = {
                OutlinedTextField(
                    value = tempNamaToko,
                    onValueChange = { tempNamaToko = it },
                    label = { Text("Masukkan nama toko") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateNamaToko(tempNamaToko)
                    showNamaTokoDialog = false
                    showToast("Nama toko berhasil disimpan")
                }) {
                    Text("Simpan", color = AccentCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNamaTokoDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsItem(
                    icon = Icons.Outlined.Store,
                    title = "Nama Toko",
                    subtitle = currentNamaToko.ifEmpty { "Belum diatur" },
                    onClick = {
                        tempNamaToko = currentNamaToko
                        showNamaTokoDialog = true
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Backup,
                    title = "Backup Data (JSON)",
                    subtitle = "Simpan data ke perangkat",
                    onClick = { createDocumentLauncher.launch("JahitKu_Backup.json") }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Restore,
                    title = "Restore Data",
                    subtitle = "Kembalikan data dari backup JSON",
                    onClick = { openDocumentLauncher.launch(arrayOf("application/json", "*/*")) }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Reset Data",
                    subtitle = "Hapus semua data secara permanen",
                    onClick = { showResetDialog = true },
                    iconTint = ErrorRed
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "Tentang Aplikasi",
                    subtitle = "JahitKu v1.0.0 (Offline Mode)",
                    onClick = { showToast("Aplikasi JahitKu v1.0.0") }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit, iconTint: androidx.compose.ui.graphics.Color = AccentCyan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}
