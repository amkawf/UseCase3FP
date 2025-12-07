package pam.mobile.usecase3fp.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

// Model untuk tabel food_items
data class FoodItemResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("carbohydrates")
    val carbohydrates: Int? = null,

    @SerializedName("protein")
    val protein: Int? = null,

    @SerializedName("fat")
    val fat: Int? = null,

    @SerializedName("sugar")
    val sugar: Int? = null,

    @SerializedName("fiber")
    val fiber: Int? = null,

    @SerializedName("calories")
    val calories: Int? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

// Model untuk daily_intakes
data class DailyIntakeResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("food_id")
    val foodId: Int,

    @SerializedName("intake_date")
    val intakeDate: String,

    @SerializedName("portion")
    val portion: Double? = 1.0,

    @SerializedName("created_at")
    val createdAt: String? = null
)

// Model untuk daily_targets
data class DailyTargetsResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("calories")
    val calories: Int,

    @SerializedName("protein")
    val protein: Int,

    @SerializedName("carbohydrates")
    val carbohydrates: Int,

    @SerializedName("fat")
    val fat: Int,

    @SerializedName("updated_at")
    val updatedAt: String? = null
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