/*
package pam.mobile.usecase3fp.repository

import pam.mobile.usecase3fp.model.DailyTargets
import pam.mobile.usecase3fp.model.FoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class FoodRepository {
    private val today = LocalDate.now()

    // StateFlow untuk reactive data
    private val _foods = MutableStateFlow<List<FoodItem>>(
        listOf(
            FoodItem("1", "Nasi Goreng", 350, 6, 50, 12, today),
            FoodItem("2", "Ayam Bakar", 280, 35, 5, 12, today),
            FoodItem("3", "Apel", 95, 0, 25, 0, today),
            FoodItem("4", "Sereal", 200, 6, 30, 4, today),
            FoodItem("5", "Kacang Tanah", 160, 8, 10, 12, today)
        )
    )
    val foods: Flow<List<FoodItem>> = _foods.asStateFlow()

    private val _targets = MutableStateFlow(DailyTargets())
    val targets: Flow<DailyTargets> = _targets.asStateFlow()

    fun getFoodsByDate(date: LocalDate): List<FoodItem> {
        return _foods.value.filter { it.date == date }
    }

    fun updateTargets(targets: DailyTargets) {
        _targets.value = targets
    }

    companion object {
        @Volatile
        private var instance: FoodRepository? = null

        fun getInstance(): FoodRepository {
            return instance ?: synchronized(this) {
                instance ?: FoodRepository().also { instance = it }
            }
        }
    }
}*/
