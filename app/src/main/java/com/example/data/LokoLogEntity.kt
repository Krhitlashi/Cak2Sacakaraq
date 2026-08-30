package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ktash.KadrajKoordinatoj
import com.example.ktash.KsakaNomoj

// ≺⧼ Loka Enskribo 📝 ⧽≻

@Entity(tableName = "loko_protokoloj")
data class LokoLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0L,
  val latitudo: Double,
  val longitudo: Double,
  val tempoMilisekundoj: Long,
  val ksakaNomo: String,
  val latinaNomo: String,
  val chmuahNomo: String,
  val v1: Int, val h1: Int,
  val v2: Int, val h2: Int,
  val v3: Int, val h3: Int,
  val v4: Int, val h4: Int,
  val stibix: Long,
  val pal2stif: Long,
  val stafl2: Long,
  val temperaturoKelvino: Double?,
  val distancoDeAntauaMetroj: Double,
  val distancoDeAntauaPeu: Double,
  val distancoDeAntauaC2ta: Double,
  val rapidoMetrojSekundo: Double = 0.0,
  val noto: String? = null
) {
  fun akiriKadrajn(): KadrajKoordinatoj = KadrajKoordinatoj(v1, h1, v2, h2, v3, h3, v4, h4)
  fun akiriNomojn(): KsakaNomoj = KsakaNomoj(ksakaNomo, latinaNomo, chmuahNomo)
}
