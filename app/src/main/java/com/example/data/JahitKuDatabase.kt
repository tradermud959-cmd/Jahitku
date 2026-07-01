package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Jahitan::class, Permakan::class, Transaksi::class], version = 2, exportSchema = false)
abstract class JahitKuDatabase : RoomDatabase() {
    abstract fun jahitanDao(): JahitanDao
    abstract fun permakanDao(): PermakanDao
    abstract fun transaksiDao(): TransaksiDao

    companion object {
        @Volatile
        private var Instance: JahitKuDatabase? = null

        fun getDatabase(context: Context): JahitKuDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JahitKuDatabase::class.java, "jahitku_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
