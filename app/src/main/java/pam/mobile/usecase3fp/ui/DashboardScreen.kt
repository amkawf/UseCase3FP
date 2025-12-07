package pam.mobile.usecase3fp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pam.mobile.usecase3fp.model.FoodItem
import pam.mobile.usecase3fp.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.widget.Toast
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF03A9F4)
            )
        )

        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        // Error State
        if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
            return
        }

        // Main Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Date Picker
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.previousDay() }) {
                        Icon(Icons.Default.ChevronLeft, "Previous")
                    }
                    Text(
                        text = if (uiState.selectedDate == LocalDate.now())
                            "Hari ini"
                        else
                            formatter.format(uiState.selectedDate),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(onClick = { viewModel.nextDay() }) {
                        Icon(Icons.Default.ChevronRight, "Next")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Overall Circular Progress - CENTERED
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OverallCircularProgress(
                        overallProgress = uiState.kcalProgress.progress,
                        current = uiState.kcalProgress.current,
                        target = uiState.kcalProgress.target
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Legend
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        LegendItem("Kalori", Color(0xFF2196F3))
/*                        LegendItem("Karbo", Color(0xFF00BCD4))
                        LegendItem("Lemak", Color(0xFFF44336))*/
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Section Title
            item {
                Text(
                    text = "Rekap Nutrisi Harian",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nutrient Cards - NO GRAPHICS
            item {
                NutrientCardSimple(
                    title = "Kalori",
                    current = uiState.kcalProgress.current,
                    target = uiState.kcalProgress.target,
                    remaining = uiState.kcalProgress.remaining,
                    unit = "kcal",
                    progress = uiState.kcalProgress.progress,  // ← ADD THIS
                    color = Color(0xFF03A9F4)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCardSimple(
                    title = "Protein",
                    current = uiState.proteinProgress.current,
                    target = uiState.proteinProgress.target,
                    remaining = uiState.proteinProgress.remaining,
                    unit = "g",
                    progress = uiState.proteinProgress.progress,  // ← ADD THIS
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCardSimple(
                    title = "Karbohidrat",
                    current = uiState.carbsProgress.current,
                    target = uiState.carbsProgress.target,
                    remaining = uiState.carbsProgress.remaining,
                    unit = "g",
                    progress = uiState.carbsProgress.progress,  // ← ADD THIS
                    color = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCardSimple(
                    title = "Lemak",
                    current = uiState.fatProgress.current,
                    target = uiState.fatProgress.target,
                    remaining = uiState.fatProgress.remaining,
                    unit = "g",
                    progress = uiState.fatProgress.progress,  // ← ADD THIS
                    color = Color(0xFF9C27B0)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Section Title untuk Food List
            item {
                Text(
                    text = "Asupan Nutrisi Terbaru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Food List
            items(
                items = uiState.foods,
                key = { food -> food.id }
            ) { food ->
                FoodItemRow(
                    food = food,
                    onClick = { clickedFood ->
                        Toast.makeText(
                            context,
                            "${clickedFood.name}: ${clickedFood.kcal} kcal\n" +
                                    "Protein: ${clickedFood.protein}g, " +
                                    "Karbo: ${clickedFood.carbs}g, " +
                                    "Lemak: ${clickedFood.fat}g",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

            // Bottom Spacer
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FoodItemRow(
    food: FoodItem,
    onClick: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(food) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = food.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Text(
                text = "${food.kcal} kcal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF03A9F4)
            )
        }

        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
    }
}

// OVERALL CIRCULAR PROGRESS - Single Progress
@Composable
fun OverallCircularProgress(
    overallProgress: Float,
    current: Int,
    target: Int
) {
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas for circular progress
        Canvas(modifier = Modifier.size(220.dp)) {
            val strokeWidth = 28.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background circle
            drawCircle(
                color = Color(0xFFE8E8E8),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc (Blue - overall)
            drawArc(
                color = Color(0xFF03A9F4),
                startAngle = -90f,
                sweepAngle = 360f * overallProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center Text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(overallProgress * 100).roundToInt()}%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF03A9F4)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$current / $target kcal",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// SIMPLE CARD
@Composable
fun NutrientCardSimple(
    title: String,
    current: Int,
    target: Int,
    remaining: Int,
    unit: String,
    progress: Float,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$current / $target $unit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$remaining $unit remaining",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // Right side - Percentage only (no graphic)
            Text(
                text = "${(progress * 100).roundToInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}