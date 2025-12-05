package pam.mobile.uiusecase3fp.repository

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pam.mobile.uiusecase3fp.data.SupabaseClientInstance
import pam.mobile.uiusecase3fp.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SupabaseFoodRepository {

    private val supabase = SupabaseClientInstance.client

    // Fetch foods by date dengan JOIN ke Nutrisi Tabel
    suspend fun getFoodsByDate(date: LocalDate): List<FoodItem> {
        val dateString = date.format(DateTimeFormatter.ISO_DATE)

        // Get daily_intake for selected date
        val dailyIntakes = supabase.from("daily_intake")
            .select()
            .eq("tanggal", dateString)
            .decodeList<DailyIntake>()

        // Get makanan details untuk setiap intake
        val foodItems = mutableListOf<FoodItem>()

        dailyIntakes.forEach { intake ->
            val nutrisi = supabase.from("Nutrisi Tabel")
                .select()
                .eq("id", intake.makananId)
                .decodeSingleOrNull<NutrisiTabel>()

            nutrisi?.let {
                foodItems.add(
                    FoodItem(
                        id = intake.id ?: "",
                        name = it.namaMakan,
                        kcal = it.totalKkal ?: 0,
                        protein = it.protein ?: 0,
                        carbs = it.karbohidrat ?: 0,
                        fat = it.lemak ?: 0,
                        date = date
                    )
                )
            }
        }

        return foodItems
    }

    // Get daily targets
    suspend fun getDailyTargets(): DailyTargets {
        val targets = supabase.from("daily_targets")
            .select()
            .limit(1)
            .decodeSingleOrNull<DailyTargets>()

        // Return default jika belum ada
        return targets ?: DailyTargets(
            kcal = 2200,
            protein = 150,
            carbs = 250,
            fat = 75
        )
    }

    // Update daily targets
    suspend fun updateDailyTargets(targets: DailyTargets) {
        // Get existing target
        val existing = supabase.from("daily_targets")
            .select()
            .limit(1)
            .decodeSingleOrNull<DailyTargets>()

        if (existing != null) {
            // Update existing
            supabase.from("daily_targets")
                .update({
                    DailyTargets::kcal setTo targets.kcal
                    DailyTargets::protein setTo targets.protein
                    DailyTargets::carbs setTo targets.carbs
                    DailyTargets::fat setTo targets.fat
                }) {
                    eq("id", existing.id!!)
                }
        } else {
            // Insert new
            supabase.from("daily_targets")
                .insert(targets)
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