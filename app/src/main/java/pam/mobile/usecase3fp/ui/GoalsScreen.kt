package pam.mobile.usecase3fp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pam.mobile.usecase3fp.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Show snackbar when saved
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.resetSavedState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar biru
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF03A9F4)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Daily Goal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Kalori
            NutrientSlider(
                label = "Kalori",
                value = uiState.kcal,
                unit = "kcal",
                color = Color(0xFF03A9F4),
                minValue = 500,
                maxValue = 5000,
                onValueChange = { viewModel.updateKcal(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Protein
            NutrientSlider(
                label = "Protein",
                value = uiState.protein,
                unit = "g",
                color = Color(0xFF4CAF50),
                minValue = 0,
                maxValue = 500,
                onValueChange = { viewModel.updateProtein(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Karbohidrat
            NutrientSlider(
                label = "Karbohidrat",
                value = uiState.carbs,
                unit = "g",
                color = Color(0xFFFF9800),
                minValue = 0,
                maxValue = 1000,
                onValueChange = { viewModel.updateCarbs(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Lemak
            NutrientSlider(
                label = "lemak",
                value = uiState.fat,
                unit = "g",
                color = Color(0xFF9C27B0),
                minValue = 0,
                maxValue = 300,
                onValueChange = { viewModel.updateFat(it) }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Terapkan button
            Button(
                onClick = { viewModel.saveTargets() },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03A9F4)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Terapkan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NutrientSlider(
    label: String,
    value: Int,
    unit: String,
    color: Color,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$value $unit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = minValue.toFloat()..maxValue.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Value text field
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { onValueChange(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
            trailingIcon = {
                Text(
                    text = unit,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            singleLine = true
        )
    }
}