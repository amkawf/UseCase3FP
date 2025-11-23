package pam.mobile.usecase3fp.model

import java.time.LocalDate

data class FoodItem(
    val id: String,
    val name: String,
    val kcal: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val date: LocalDate
)

data class DailyTargets(
    val kcal: Int = 2200,
    val protein: Int = 150,
    val carbs: Int = 250,
    val fat: Int = 75
)

data class NutrientProgress(
    val current: Int,
    val target: Int,
    val remaining: Int,
    val progress: Float
)