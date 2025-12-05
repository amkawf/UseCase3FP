package pam.mobile.uiusecase3fp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Trigger snackbar setelah penyimpanan berhasil
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Target berhasil disimpan")
            viewModel.resetSavedState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Goal", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF03A9F4))
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        // ---- Loading saat fetch awal ----
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // ---- Konten utama ----
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Daily Target Nutrisi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                NutrientSlider(
                    label = "Kalori",
                    value = uiState.kcal,
                    unit = "kcal",
                    color = Color(0xFF03A9F4),
                    minValue = 500,
                    maxValue = 5000,
                    onValueChange = viewModel::updateKcal
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                NutrientSlider(
                    label = "Protein",
                    value = uiState.protein,
                    unit = "g",
                    color = Color(0xFF4CAF50),
                    minValue = 0,
                    maxValue = 500,
                    onValueChange = viewModel::updateProtein
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                NutrientSlider(
                    label = "Karbohidrat",
                    value = uiState.carbs,
                    unit = "g",
                    color = Color(0xFFFF9800),
                    minValue = 0,
                    maxValue = 1000,
                    onValueChange = viewModel::updateCarbs
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                NutrientSlider(
                    label = "Lemak",
                    value = uiState.fat,
                    unit = "g",
                    color = Color(0xFF9C27B0),
                    minValue = 0,
                    maxValue = 300,
                    onValueChange = viewModel::updateFat
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            // Tombol Simpan
            item {
                Button(
                    onClick = { viewModel.saveTargets() },
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Simpan Target",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
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
            Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = "$value $unit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
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

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value.toString(),
            onValueChange = { input -> input.toIntOrNull()?.let(onValueChange) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Text(unit) },
            singleLine = true
        )
    }
}
