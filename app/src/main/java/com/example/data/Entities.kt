package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jahitan")
data class Jahitan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val kategori: String,
    val harga: Double,
    val catatan: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "permakan")
data class Permakan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nama: String,
    val kategori: String,
    val harga: Double,
    val catatan: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val namaPelanggan: String,
    val nomorHp: String,
    val itemsJson: String, 
    val totalHarga: Double,
    val uangPelanggan: Double,
    val kembalian: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class TransaksiItem(
    val id: Long = System.currentTimeMillis(),
    val jenis: String,
    val itemId: Long,
    val itemName: String,
    val harga: Double
)
