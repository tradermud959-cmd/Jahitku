package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JahitanDao {
    @Query("SELECT * FROM jahitan ORDER BY timestamp DESC")
    fun getAllJahitan(): Flow<List<Jahitan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJahitan(jahitan: Jahitan)

    @androidx.room.Update
    suspend fun updateJahitan(jahitan: Jahitan)

    @androidx.room.Delete
    suspend fun deleteJahitan(jahitan: Jahitan)
}

@Dao
interface PermakanDao {
    @Query("SELECT * FROM permakan ORDER BY timestamp DESC")
    fun getAllPermakan(): Flow<List<Permakan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermakan(permakan: Permakan)

    @androidx.room.Update
    suspend fun updatePermakan(permakan: Permakan)

    @androidx.room.Delete
    suspend fun deletePermakan(permakan: Permakan)
}

@Dao
interface TransaksiDao {
    @Query("SELECT * FROM transaksi ORDER BY timestamp DESC")
    fun getAllTransaksi(): Flow<List<Transaksi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaksi(transaksi: Transaksi)

    @androidx.room.Update
    suspend fun updateTransaksi(transaksi: Transaksi)

    @androidx.room.Delete
    suspend fun deleteTransaksi(transaksi: Transaksi)
}
