package pam.mobile.usecase3fp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import pam.mobile.usecase3fp.model.DailyTargets
import pam.mobile.usecase3fp.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoalsUiState(
    val kcal: Int = 2200,
    val protein: Int = 150,
    val carbs: Int = 250,
    val fat: Int = 75,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)

class GoalsViewModel(
    private val repository: FoodRepository = FoodRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentTargets()
    }

    private fun loadCurrentTargets() {
        viewModelScope.launch {
            repository.targets.collect { targets ->
                _uiState.value = _uiState.value.copy(
                    kcal = targets.kcal,
                    protein = targets.protein,
                    carbs = targets.carbs,
                    fat = targets.fat
                )
            }
        }
    }

    fun updateKcal(value: Int) {
        _uiState.value = _uiState.value.copy(kcal = value.coerceIn(500, 5000))
    }

    fun updateProtein(value: Int) {
        _uiState.value = _uiState.value.copy(protein = value.coerceIn(0, 500))
    }

    fun updateCarbs(value: Int) {
        _uiState.value = _uiState.value.copy(carbs = value.coerceIn(0, 1000))
    }

    fun updateFat(value: Int) {
        _uiState.value = _uiState.value.copy(fat = value.coerceIn(0, 300))
    }

    fun saveTargets() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            val newTargets = DailyTargets(
                kcal = _uiState.value.kcal,
                protein = _uiState.value.protein,
                carbs = _uiState.value.carbs,
                fat = _uiState.value.fat
            )

            repository.updateTargets(newTargets)

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }

    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}