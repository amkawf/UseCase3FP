package pam.mobile.usecase3fp.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pam.mobile.usecase3fp.api.RetrofitClient
import pam.mobile.usecase3fp.api.UpdateTargetsRequest
import pam.mobile.usecase3fp.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SupabaseFoodRepository {

    private val apiService = RetrofitClient.apiService
    private val gson = Gson()
    private val TAG = "SupabaseRepo"

    suspend fun getFoodsByDate(date: LocalDate): List<FoodItem> = withContext(Dispatchers.IO) {
        try {
            val dateString = date.format(DateTimeFormatter.ISO_DATE)
            Log.d(TAG, "========================================")
            Log.d(TAG, "Fetching foods for date: $dateString")

            // Ambil daily intakes sebagai String
            val intakesJson = apiService.getDailyIntakesRaw(intakeDate = "eq.$dateString")
            Log.d(TAG, "Raw intakes JSON: $intakesJson")

            // Parse dengan Gson
            val intakesType = object : TypeToken<List<DailyIntakeResponse>>() {}.type
            val dailyIntakes: List<DailyIntakeResponse> = gson.fromJson(intakesJson, intakesType)
            Log.d(TAG, "Parsed intakes: ${dailyIntakes.size}")

            if (dailyIntakes.isEmpty()) {
                Log.d(TAG, "No intakes found for $dateString")
                return@withContext emptyList()
            }

            // Log setiap intake dengan detail type
            dailyIntakes.forEachIndexed { index, intake ->
                Log.d(TAG, "Intake[$index]:")
                Log.d(TAG, "  - id: ${intake.id} (${intake.id?.javaClass?.simpleName})")
                Log.d(TAG, "  - food_id: ${intake.foodId} (${intake.foodId.javaClass.simpleName})")
                Log.d(TAG, "  - intake_date: ${intake.intakeDate}")
                Log.d(TAG, "  - portion: ${intake.portion}")
            }

            // Ambil semua food items sebagai String
            val foodsJson = apiService.getFoodItemsRaw()
            Log.d(TAG, "Raw foods JSON: ${foodsJson.take(200)}...") // Log first 200 chars

            // Parse dengan Gson
            val foodsType = object : TypeToken<List<FoodItemResponse>>() {}.type
            val allFoods: List<FoodItemResponse> = gson.fromJson(foodsJson, foodsType)
            Log.d(TAG, "Parsed food items: ${allFoods.size}")

            if (allFoods.isEmpty()) {
                Log.e(TAG, "ERROR: food_items table is EMPTY!")
                return@withContext emptyList()
            }

            // Log setiap food item dengan detail type
            allFoods.forEachIndexed { index, food ->
                Log.d(TAG, "Food[$index]:")
                Log.d(TAG, "  - id: ${food.id} (${food.id.javaClass.simpleName})")
                Log.d(TAG, "  - name: ${food.name}")
                Log.d(TAG, "  - calories: ${food.calories}")
            }

            // Gabungkan data dengan DETAILED LOG
            Log.d(TAG, "========================================")
            Log.d(TAG, "Starting JOIN process...")

            val foodItems = mutableListOf<FoodItem>()

            dailyIntakes.forEach { intake ->
                Log.d(TAG, "Looking for food_id: ${intake.foodId} (type: ${intake.foodId.javaClass.simpleName})")

                val matchingFood = allFoods.find { food ->
                    val match = food.id == intake.foodId
                    Log.d(TAG, "  Comparing ${food.id} (${food.id.javaClass.simpleName}) == ${intake.foodId}? $match")
                    match
                }

                if (matchingFood == null) {
                    Log.e(TAG, "❌ NO MATCH FOUND for food_id=${intake.foodId}")
                    Log.e(TAG, "Available food IDs: ${allFoods.map { it.id }}")
                } else {
                    Log.d(TAG, "✅ MATCH FOUND: food_id=${intake.foodId} -> ${matchingFood.name}")

                    val portion = intake.portion ?: 1.0
                    val finalKcal = ((matchingFood.calories ?: 0) * portion).toInt()
                    val finalProtein = ((matchingFood.protein ?: 0) * portion).toInt()
                    val finalCarbs = ((matchingFood.carbohydrates ?: 0) * portion).toInt()
                    val finalFat = ((matchingFood.fat ?: 0) * portion).toInt()

                    Log.d(TAG, "Calculated: ${matchingFood.name} x${portion} = ${finalKcal}kcal, P:${finalProtein}g, C:${finalCarbs}g, F:${finalFat}g")

                    foodItems.add(FoodItem(
                        id = intake.id ?: "",
                        name = matchingFood.name,
                        kcal = finalKcal,
                        protein = finalProtein,
                        carbs = finalCarbs,
                        fat = finalFat,
                        date = date,
                        portion = portion
                    ))
                }
            }

            Log.d(TAG, "========================================")
            Log.d(TAG, "Final food items: ${foodItems.size}")
            foodItems.forEachIndexed { index, item ->
                Log.d(TAG, "Result[$index]: ${item.name} (${item.portion}x), ${item.kcal} kcal")
            }
            Log.d(TAG, "========================================")

            foodItems
        } catch (e: Exception) {
            Log.e(TAG, "========================================")
            Log.e(TAG, "EXCEPTION in getFoodsByDate", e)
            Log.e(TAG, "Error message: ${e.message}")
            e.printStackTrace()
            Log.e(TAG, "========================================")
            emptyList()
        }
    }

    suspend fun getDailyTargets(): DailyTargets = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching daily targets...")

            val targetsJson = apiService.getDailyTargetsRaw()
            Log.d(TAG, "Raw targets JSON: $targetsJson")

            val targetsType = object : TypeToken<List<DailyTargetsResponse>>() {}.type
            val targets: List<DailyTargetsResponse> = gson.fromJson(targetsJson, targetsType)
            Log.d(TAG, "Parsed targets: ${targets.size}")

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

            Log.d(TAG, "Using targets: kcal=${result.kcal}, protein=${result.protein}")

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

            val targetsJson = apiService.getDailyTargetsRaw()
            val targetsType = object : TypeToken<List<DailyTargetsResponse>>() {}.type
            val existingList: List<DailyTargetsResponse> = gson.fromJson(targetsJson, targetsType)
            val existing = existingList.firstOrNull()

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