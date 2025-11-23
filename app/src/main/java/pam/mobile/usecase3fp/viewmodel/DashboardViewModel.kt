package pam.mobile.usecase3fp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pam.mobile.usecase3fp.model.DailyTargets
import pam.mobile.usecase3fp.model.FoodItem
import pam.mobile.usecase3fp.model.NutrientProgress
import pam.mobile.usecase3fp.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val foods: List<FoodItem> = emptyList(),
    val targets: DailyTargets = DailyTargets(),
    val kcalProgress: NutrientProgress = NutrientProgress(0, 2200, 2200, 0f),
    val proteinProgress: NutrientProgress = NutrientProgress(0, 150, 150, 0f),
    val carbsProgress: NutrientProgress = NutrientProgress(0, 250, 250, 0f),
    val fatProgress: NutrientProgress = NutrientProgress(0, 75, 75, 0f)
)

class DashboardViewModel(
    private val repository: FoodRepository = FoodRepository.getInstance()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.foods,
                repository.targets,
                _selectedDate
            ) { foods, targets, date ->
                val foodsForDate = foods.filter { it.date == date }

                val totalKcal = foodsForDate.sumOf { it.kcal }
                val totalProtein = foodsForDate.sumOf { it.protein }
                val totalCarbs = foodsForDate.sumOf { it.carbs }
                val totalFat = foodsForDate.sumOf { it.fat }

                DashboardUiState(
                    selectedDate = date,
                    foods = foodsForDate,
                    targets = targets,
                    kcalProgress = NutrientProgress(
                        current = totalKcal,
                        target = targets.kcal,
                        remaining = (targets.kcal - totalKcal).coerceAtLeast(0),
                        progress = (totalKcal.toFloat() / targets.kcal).coerceIn(0f, 1f)
                    ),
                    proteinProgress = NutrientProgress(
                        current = totalProtein,
                        target = targets.protein,
                        remaining = (targets.protein - totalProtein).coerceAtLeast(0),
                        progress = (totalProtein.toFloat() / targets.protein).coerceIn(0f, 1f)
                    ),
                    carbsProgress = NutrientProgress(
                        current = totalCarbs,
                        target = targets.carbs,
                        remaining = (targets.carbs - totalCarbs).coerceAtLeast(0),
                        progress = (totalCarbs.toFloat() / targets.carbs).coerceIn(0f, 1f)
                    ),
                    fatProgress = NutrientProgress(
                        current = totalFat,
                        target = targets.fat,
                        remaining = (targets.fat - totalFat).coerceAtLeast(0),
                        progress = (totalFat.toFloat() / targets.fat).coerceIn(0f, 1f)
                    )
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun previousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun nextDay() {
        if (_selectedDate.value.isBefore(LocalDate.now())) {
            _selectedDate.value = _selectedDate.value.plusDays(1)
        }
    }
}