package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.JahitKuRepository
import com.example.data.Jahitan
import com.example.data.Permakan
import com.example.data.Transaksi
import com.example.data.TransaksiItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class JahitKuViewModel(
    private val repository: JahitKuRepository,
    private val isFirstRun: Boolean,
    private val onFirstRunComplete: () -> Unit,
    initialNamaToko: String,
    private val onNamaTokoChanged: (String) -> Unit
) : ViewModel() {
    private val _namaToko = kotlinx.coroutines.flow.MutableStateFlow(initialNamaToko)
    val namaToko: StateFlow<String> = _namaToko.asStateFlow()

    fun updateNamaToko(newName: String) {
        _namaToko.value = newName
        onNamaTokoChanged(newName)
    }

    val allJahitan: StateFlow<List<Jahitan>> = repository.allJahitan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPermakan: StateFlow<List<Permakan>> = repository.allPermakan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransaksi: StateFlow<List<Transaksi>> = repository.allTransaksi
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (isFirstRun) {
            viewModelScope.launch {
                val currentJahitan = repository.allJahitan.first()
                if (currentJahitan.isEmpty()) {
                    insertDummyData()
                }
                onFirstRunComplete()
            }
        }
    }

    private suspend fun insertDummyData() {
        repository.insertJahitan(Jahitan(nama = "Kemeja Pria", kategori = "Atasan", harga = 150000.0, catatan = "Bahan Katun"))
        repository.insertJahitan(Jahitan(nama = "Celana Panjang", kategori = "Bawahan", harga = 120000.0, catatan = "Bahan Drill"))
        
        repository.insertPermakan(Permakan(nama = "Potong Celana", kategori = "Bawahan", harga = 25000.0, catatan = "Potong bawah"))
        repository.insertPermakan(Permakan(nama = "Kecilin Pinggang", kategori = "Bawahan", harga = 35000.0, catatan = "Karet pinggang"))

        repository.insertTransaksi(Transaksi(
            namaPelanggan = "Budi",
            nomorHp = "081234567890",
            itemsJson = "[{\"id\":1,\"jenis\":\"Jahitan\",\"itemId\":1,\"itemName\":\"Kemeja Pria\",\"harga\":150000.0}]",
            totalHarga = 150000.0,
            uangPelanggan = 200000.0,
            kembalian = 50000.0
        ))
    }

    fun addJahitan(nama: String, kategori: String, harga: Double, catatan: String) {
        viewModelScope.launch {
            repository.insertJahitan(Jahitan(nama = nama, kategori = kategori, harga = harga, catatan = catatan))
        }
    }

    fun updateJahitan(jahitan: Jahitan) {
        viewModelScope.launch { repository.updateJahitan(jahitan) }
    }

    fun deleteJahitan(jahitan: Jahitan) {
        viewModelScope.launch { repository.deleteJahitan(jahitan) }
    }

    fun addPermakan(nama: String, kategori: String, harga: Double, catatan: String) {
        viewModelScope.launch {
            repository.insertPermakan(Permakan(nama = nama, kategori = kategori, harga = harga, catatan = catatan))
        }
    }

    fun updatePermakan(permakan: Permakan) {
        viewModelScope.launch { repository.updatePermakan(permakan) }
    }

    fun deletePermakan(permakan: Permakan) {
        viewModelScope.launch { repository.deletePermakan(permakan) }
    }

    fun addTransaksi(namaPelanggan: String, nomorHp: String, items: List<TransaksiItem>, uangPelanggan: Double) {
        viewModelScope.launch {
            val totalHarga = items.sumOf { it.harga }
            val kembalian = uangPelanggan - totalHarga
            
            val array = JSONArray()
            items.forEach { item ->
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("jenis", item.jenis)
                obj.put("itemId", item.itemId)
                obj.put("itemName", item.itemName)
                obj.put("harga", item.harga)
                array.put(obj)
            }
            
            repository.insertTransaksi(
                Transaksi(
                    namaPelanggan = namaPelanggan,
                    nomorHp = nomorHp,
                    itemsJson = array.toString(),
                    totalHarga = totalHarga,
                    uangPelanggan = uangPelanggan,
                    kembalian = kembalian
                )
            )
        }
    }

    fun parseTransaksiItems(json: String): List<TransaksiItem> {
        val list = mutableListOf<TransaksiItem>()
        if (json.isBlank()) return list
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TransaksiItem(
                        id = obj.optLong("id", System.currentTimeMillis()),
                        jenis = obj.optString("jenis", ""),
                        itemId = obj.optLong("itemId", 0L),
                        itemName = obj.optString("itemName", ""),
                        harga = obj.optDouble("harga", 0.0)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearAllData() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.clearAllData()
        }
    }
}

class JahitKuViewModelFactory(
    private val repository: JahitKuRepository,
    private val isFirstRun: Boolean,
    private val onFirstRunComplete: () -> Unit,
    private val initialNamaToko: String,
    private val onNamaTokoChanged: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JahitKuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JahitKuViewModel(repository, isFirstRun, onFirstRunComplete, initialNamaToko, onNamaTokoChanged) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
