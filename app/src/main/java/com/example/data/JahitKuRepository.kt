package com.example.data

import kotlinx.coroutines.flow.Flow

class JahitKuRepository(private val database: JahitKuDatabase) {
    val allJahitan: Flow<List<Jahitan>> = database.jahitanDao().getAllJahitan()
    val allPermakan: Flow<List<Permakan>> = database.permakanDao().getAllPermakan()
    val allTransaksi: Flow<List<Transaksi>> = database.transaksiDao().getAllTransaksi()

    suspend fun insertJahitan(jahitan: Jahitan) = database.jahitanDao().insertJahitan(jahitan)
    suspend fun updateJahitan(jahitan: Jahitan) = database.jahitanDao().updateJahitan(jahitan)
    suspend fun deleteJahitan(jahitan: Jahitan) = database.jahitanDao().deleteJahitan(jahitan)

    suspend fun insertPermakan(permakan: Permakan) = database.permakanDao().insertPermakan(permakan)
    suspend fun updatePermakan(permakan: Permakan) = database.permakanDao().updatePermakan(permakan)
    suspend fun deletePermakan(permakan: Permakan) = database.permakanDao().deletePermakan(permakan)

    suspend fun insertTransaksi(transaksi: Transaksi) = database.transaksiDao().insertTransaksi(transaksi)
    suspend fun updateTransaksi(transaksi: Transaksi) = database.transaksiDao().updateTransaksi(transaksi)
    suspend fun deleteTransaksi(transaksi: Transaksi) = database.transaksiDao().deleteTransaksi(transaksi)

    suspend fun clearAllData() {
        database.clearAllTables()
    }
}
