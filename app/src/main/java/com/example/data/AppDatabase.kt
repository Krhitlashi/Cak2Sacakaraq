package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ≺⧼ Datumbazo 🗄️ ⧽≻

@Database(entities = [LokoLogEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun lokoLogDao(): LokoLogDao

  companion object {
    @Volatile
    private var INSTANCO: AppDatabase? = null

    fun akiriDatumbazon(kunteksto: Context): AppDatabase {
      return INSTANCO ?: synchronized(this) {
        val instanco = Room.databaseBuilder(
          kunteksto.applicationContext,
          AppDatabase::class.java,
          "ktash_loko_datumbazo"
        ).fallbackToDestructiveMigration().build()
        INSTANCO = instanco
        instanco
      }
    }
  }
}
