package pam.mobile.usecase3fp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

// Model untuk tabel food_items
@Serializable
data class FoodItemResponse(
    val id: Int,
    val name: String,
    val carbohydrates: Int? = null,
    val protein: Int? = null,
    val fat: Int? = null,
    val sugar: Int? = null,
    val fiber: Int? = null,
    val calories: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// Model untuk daily_intakes
@Serializable
data class DailyIntakeResponse(
    val id: String? = null,
    @SerialName("food_id") val foodId: Int,
    @SerialName("intake_date") val intakeDate: String,
    val portion: Double? = 1.0,
    @SerialName("created_at") val createdAt: String? = null
)

// Model untuk daily_targets
@Serializable
data class DailyTargetsResponse(
    val id: String? = null,
    val calories: Int,
    val protein: Int,
    val carbohydrates: Int,
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
    val date: LocalDate,
    val portion: Double = 1.0
)

// Model untuk daily targets UI
data class DailyTargets(
    val id: String? = null,
    val kcal: Int = 2200,
    val protein: Int = 150,
    val carbs: Int = 250,
    val fat: Int = 75
)

// Model untuk progress UI
data class NutrientProgress(
    val current: Int,
    val target: Int,
    val remaining: Int,
    val progress: Float
)