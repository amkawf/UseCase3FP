package pam.mobile.uiusecase3fp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pam.mobile.uiusecase3fp.model.DailyTargets
import pam.mobile.uiusecase3fp.model.FoodItem
import pam.mobile.uiusecase3fp.model.NutrientProgress
import pam.mobile.uiusecase3fp.repository.SupabaseFoodRepository
import java.time.LocalDate

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val foods: List<FoodItem> = emptyList(),
    val targets: DailyTargets = DailyTargets(
        kcal = 2200,
        protein = 150,
        carbs = 250,
        fat = 75
    ),
    val kcalProgress: NutrientProgress = NutrientProgress(0, 2200, 2200, 0f),
    val proteinProgress: NutrientProgress = NutrientProgress(0, 150, 150, 0f),
    val carbsProgress: NutrientProgress = NutrientProgress(0, 250, 250, 0f),
    val fatProgress: NutrientProgress = NutrientProgress(0, 75, 75, 0f),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val repository: SupabaseFoodRepository = SupabaseFoodRepository.getInstance()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                try {
                    // Fetch targets
                    val targets = repository.getDailyTargets()

                    // Fetch foods for selected date
                    val foods = repository.getFoodsByDate(date)

                    // Calculate totals
                    val totalKcal = foods.sumOf { it.kcal }
                    val totalProtein = foods.sumOf { it.protein }
                    val totalCarbs = foods.sumOf { it.carbs }
                    val totalFat = foods.sumOf { it.fat }

                    // Update UI state
                    _uiState.value = DashboardUiState(
                        selectedDate = date,
                        foods = foods,
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
                        ),
                        isLoading = false,
                        error = null
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred"
                    )
                }
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

    fun refresh() {
        loadData()
    }
}