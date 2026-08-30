package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ≺⧼ Loka Enskriba Aliro 💾 ⧽≻

@Dao
interface LokoLogDao {

  @Query("SELECT * FROM loko_protokoloj ORDER BY tempoMilisekundoj DESC")
  fun ciujProtokoloj(): Flow<List<LokoLogEntity>>

  @Query("SELECT * FROM loko_protokoloj WHERE stibix = :stibix AND pal2stif = :pal2stif AND stafl2 = :stafl2 ORDER BY tempoMilisekundoj ASC")
  fun protokolojLauKtashDato(stibix: Long, pal2stif: Long, stafl2: Long): Flow<List<LokoLogEntity>>

  @Query("SELECT DISTINCT stibix, pal2stif, stafl2 FROM loko_protokoloj ORDER BY stibix DESC, pal2stif DESC, stafl2 DESC")
  fun ciujDistinctKtashTagoj(): Flow<List<KtashTagoIdentigilo>>

  @Query("SELECT * FROM loko_protokoloj ORDER BY tempoMilisekundoj DESC LIMIT 1")
  suspend fun lastaProtokolo(): LokoLogEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun enmetiProtokolon(protokolo: LokoLogEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun enmetiCiujn(protokoloj: List<LokoLogEntity>): List<Long>

  @Query("SELECT * FROM loko_protokoloj ORDER BY tempoMilisekundoj ASC")
  suspend fun akiriCiujnListon(): List<LokoLogEntity>

  @Query("DELETE FROM loko_protokoloj WHERE id = :id")
  suspend fun forigiLauId(id: Long)

  @Query("DELETE FROM loko_protokoloj")
  suspend fun vakigiCiujn()
}

data class KtashTagoIdentigilo(
  val stibix: Long,
  val pal2stif: Long,
  val stafl2: Long
)

