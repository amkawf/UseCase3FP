package pam.mobile.uiusecase3fp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

// Model untuk tabel "Nutrisi Tabel"
@Serializable
data class NutrisiTabel(
    val id: Int,
    @SerialName("nama_makan") val namaMakan: String,
    val karbohidrat: Int?,
    val protein: Int?,
    val lemak: Int?,
    val gula: Int?,
    val serat: Int?,
    @SerialName("total-kkal") val totalKkal: Int?
)

// Model untuk daily_intake
@Serializable
data class DailyIntake(
    val id: String? = null,
    @SerialName("makanan_id") val makananId: Int,
    val tanggal: String, // Format: "2025-12-06"
    @SerialName("created_at") val createdAt: String? = null
)

// Model untuk daily_targets
@Serializable
data class DailyTargets(
    val id: String? = null,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    @SerialName("updated_at") val updatedAt: String? = null
)

// Model untuk UI (tidak perlu serializable)
data class FoodItem(
    val id: String,
    val name: String,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val date: LocalDate
)

// Model untuk progress UI
data class NutrientProgress(
    val current: Int,
    val target: Int,
    val remaining: Int,
    val progress: Float
)