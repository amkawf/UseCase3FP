/*
package pam.mobile.usecase3fp.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pam.mobile.usecase3fp.api.RetrofitClient
import pam.mobile.usecase3fp.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SupabaseFoodRepository {

    private val apiService = RetrofitClient.apiService
    private val TAG = "SupabaseRepo"

    suspend fun getFoodsByDate(date: LocalDate): List<FoodItem> = withContext(Dispatchers.IO) {
        try {
            val dateString = date.format(DateTimeFormatter.ISO_DATE)
            Log.d(TAG, "Fetching foods for date: $dateString")

            // Ambil daily intake untuk tanggal tertentu
            val dailyIntakes = apiService.getDailyIntake(tanggal = "eq.$dateString")
            Log.d(TAG, "Total intakes from API: ${dailyIntakes.size}")

            // Ambil semua nutrisi
            val allNutrisi = apiService.getNutrisi()
            Log.d(TAG, "Total nutrisi from API: ${allNutrisi.size}")

            // Gabungkan data
            val foodItems = dailyIntakes.mapNotNull { intake ->
                allNutrisi.find { it.id == intake.makananId }?.let { nutrisi ->
                    FoodItem(
                        id = intake.id ?: "",
                        name = nutrisi.namaMakan,
                        kcal = nutrisi.totalKkal ?: 0,
                        protein = nutrisi.protein ?: 0,
                        carbs = nutrisi.karbohidrat ?: 0,
                        fat = nutrisi.lemak ?: 0,
                        date = date
                    )
                }
            }

            Log.d(TAG, "Final food items: ${foodItems.size}")
            foodItems.forEach {
                Log.d(TAG, "Food: ${it.name}, ${it.kcal} kcal")
            }

            foodItems
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching foods", e)
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
                    kcal = it.kcal,
                    protein = it.protein,
                    carbs = it.carbs,
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

            val existing = apiService.getDailyTargets().firstOrNull()

            val body = mapOf(
                "kcal" to targets.kcal,
                "protein" to targets.protein,
                "carbs" to targets.carbs,
                "fat" to targets.fat
            )

            if (existing != null && existing.id != null) {
                Log.d(TAG, "Updating existing target with id: ${existing.id}")
                apiService.updateDailyTargets(id = "eq.${existing.id}", body = body)
                Log.d(TAG, "Target updated successfully")
            } else {
                Log.d(TAG, "Inserting new target")
                apiService.insertDailyTargets(body = body)
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
}*/
