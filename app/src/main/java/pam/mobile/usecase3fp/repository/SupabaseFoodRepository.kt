package pam.mobile.usecase3fp.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pam.mobile.usecase3fp.api.RetrofitClient
import pam.mobile.usecase3fp.api.UpdateTargetsRequest
import pam.mobile.usecase3fp.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SupabaseFoodRepository {

    private val apiService = RetrofitClient.apiService
    private val TAG = "SupabaseRepo"

    suspend fun getFoodsByDate(date: LocalDate): List<FoodItem> = withContext(Dispatchers.IO) {
        try {
            val dateString = date.format(DateTimeFormatter.ISO_DATE)
            Log.d(TAG, "========================================")
            Log.d(TAG, "Fetching foods for date: $dateString")

            // Ambil daily intakes untuk tanggal tertentu
            val dailyIntakes = apiService.getDailyIntakes(intakeDate = "eq.$dateString")
            Log.d(TAG, "Total intakes from API: ${dailyIntakes.size}")

            if (dailyIntakes.isEmpty()) {
                Log.d(TAG, "No intakes found for $dateString")
                return@withContext emptyList()
            }

            // Log setiap intake
            dailyIntakes.forEachIndexed { index, intake ->
                Log.d(TAG, "Intake[$index]: id=${intake.id}, food_id=${intake.foodId}, date=${intake.intakeDate}, portion=${intake.portion}")
            }

            // Ambil semua food items
            val allFoods = apiService.getFoodItems()
            Log.d(TAG, "Total food items from API: ${allFoods.size}")

            if (allFoods.isEmpty()) {
                Log.e(TAG, "ERROR: food_items table is EMPTY!")
                return@withContext emptyList()
            }

            // Log setiap food item
            allFoods.forEachIndexed { index, food ->
                Log.d(TAG, "Food[$index]: id=${food.id}, name=${food.name}, calories=${food.calories}")
            }

            // Gabungkan data dengan DETAILED LOG
            Log.d(TAG, "========================================")
            Log.d(TAG, "Starting JOIN process...")

            val foodItems = dailyIntakes.mapNotNull { intake ->
                Log.d(TAG, "Processing intake: food_id=${intake.foodId}")

                val matchingFood = allFoods.find { it.id == intake.foodId }

                if (matchingFood == null) {
                    Log.e(TAG, "❌ NO MATCH FOUND for food_id=${intake.foodId}")
                    null
                } else {
                    Log.d(TAG, "✅ MATCH FOUND: food_id=${intake.foodId} -> ${matchingFood.name}")

                    val portion = intake.portion ?: 1.0
                    val finalKcal = ((matchingFood.calories ?: 0) * portion).toInt()
                    val finalProtein = ((matchingFood.protein ?: 0) * portion).toInt()
                    val finalCarbs = ((matchingFood.carbohydrates ?: 0) * portion).toInt()
                    val finalFat = ((matchingFood.fat ?: 0) * portion).toInt()

                    Log.d(TAG, "Calculated: ${matchingFood.name} x${portion} = ${finalKcal}kcal")

                    FoodItem(
                        id = intake.id ?: "",
                        name = matchingFood.name,
                        kcal = finalKcal,
                        protein = finalProtein,
                        carbs = finalCarbs,
                        fat = finalFat,
                        date = date,
                        portion = portion
                    )
                }
            }

            Log.d(TAG, "========================================")
            Log.d(TAG, "Final food items: ${foodItems.size}")
            foodItems.forEachIndexed { index, item ->
                Log.d(TAG, "Result[$index]: ${item.name} (${item.portion}x), ${item.kcal} kcal, P:${item.protein}g, C:${item.carbs}g, F:${item.fat}g")
            }
            Log.d(TAG, "========================================")

            foodItems
        } catch (e: Exception) {
            Log.e(TAG, "========================================")
            Log.e(TAG, "EXCEPTION in getFoodsByDate", e)
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Stack trace:", e)
            Log.e(TAG, "========================================")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getDailyTargets(): DailyTargets = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching daily targets...")

            val targets = apiService.getDailyTargets()
            Log.d(TAG, "Targets found: ${targets.size}")

            val result = targets.firstOrNull()?.let {
                DailyTargets(
                    id = it.id,
                    kcal = it.calories,
                    protein = it.protein,
                    carbs = it.carbohydrates,
                    fat = it.fat
                )
            } ?: DailyTargets(
                kcal = 2200,
                protein = 150,
                carbs = 250,
                fat = 75
            )

            Log.d(TAG, "Using targets: kcal=${result.kcal}, protein=${result.protein}, carbs=${result.carbs}, fat=${result.fat}")

            result
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching targets", e)
            e.printStackTrace()
            DailyTargets(kcal = 2200, protein = 150, carbs = 250, fat = 75)
        }
    }

    suspend fun updateDailyTargets(targets: DailyTargets) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Updating targets: kcal=${targets.kcal}, protein=${targets.protein}")

            val existing = apiService.getDailyTargets().firstOrNull()

            val requestBody = UpdateTargetsRequest(
                calories = targets.kcal,
                protein = targets.protein,
                carbohydrates = targets.carbs,
                fat = targets.fat
            )

            if (existing != null && existing.id != null) {
                Log.d(TAG, "Updating existing target with id: ${existing.id}")
                apiService.updateDailyTargets(id = "eq.${existing.id}", body = requestBody)
                Log.d(TAG, "Target updated successfully")
            } else {
                Log.d(TAG, "Inserting new target")
                apiService.insertDailyTargets(body = requestBody)
                Log.d(TAG, "Target inserted successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating targets", e)
            e.printStackTrace()
            throw e
        }
    }

    companion object {
        @Volatile
        private var instance: SupabaseFoodRepository? = null

        fun getInstance(): SupabaseFoodRepository {
            return instance ?: synchronized(this) {
                instance ?: SupabaseFoodRepository().also { instance = it }
            }
        }
    }
}