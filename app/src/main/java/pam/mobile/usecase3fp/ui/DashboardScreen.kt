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
import pam.mobile.usecase3fp.model.NutrientProgress
import pam.mobile.usecase3fp.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.widget.Toast

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
                .padding(horizontal = 20.dp)
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
                Spacer(modifier = Modifier.height(24.dp))
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
                            formatter.format(uiState.selectedDate)
                    )
                    IconButton(onClick = { viewModel.nextDay() }) {
                        Icon(Icons.Default.ChevronRight, "Next")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Circular Progress
            item {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MultiSegmentCircularProgress(
                        proteinProgress = uiState.proteinProgress.progress,
                        carbsProgress = uiState.carbsProgress.progress,
                        fatProgress = uiState.fatProgress.progress
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
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        LegendItem("Protein", Color(0xFF2196F3))
                        LegendItem("Karbo", Color(0xFF00BCD4))
                        LegendItem("Lemak", Color(0xFFF44336))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Section Title
            item {
                Text(
                    text = "Rekap Nutrisi Harian",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nutrient Cards
            item {
                NutrientCard(
                    title = "Kalori",
                    current = uiState.kcalProgress.current,
                    target = uiState.kcalProgress.target,
                    remaining = uiState.kcalProgress.remaining,
                    unit = "kcal",
                    progress = uiState.kcalProgress.progress,
                    color = Color(0xFF03A9F4)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCard(
                    title = "Protein",
                    current = uiState.proteinProgress.current,
                    target = uiState.proteinProgress.target,
                    remaining = uiState.proteinProgress.remaining,
                    unit = "g",
                    progress = uiState.proteinProgress.progress,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCard(
                    title = "Karbohidrat",
                    current = uiState.carbsProgress.current,
                    target = uiState.carbsProgress.target,
                    remaining = uiState.carbsProgress.remaining,
                    unit = "g",
                    progress = uiState.carbsProgress.progress,
                    color = Color(0xFFFF9800)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                NutrientCard(
                    title = "Lemak",
                    current = uiState.fatProgress.current,
                    target = uiState.fatProgress.target,
                    remaining = uiState.fatProgress.remaining,
                    unit = "g",
                    progress = uiState.fatProgress.progress,
                    color = Color(0xFF9C27B0)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Section Title untuk Food List
            item {
                Text(
                    text = "Asupan Nutrisi Terbaru",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
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

@Composable
fun MultiSegmentCircularProgress(
    proteinProgress: Float,
    carbsProgress: Float,
    fatProgress: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background circle
        drawCircle(
            color = Color(0xFFE0E0E0),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val segmentAngle = 360f / 3
        val gapAngle = 8f

        // Protein segment (Blue)
        drawArc(
            color = Color(0xFF2196F3),
            startAngle = -90f,
            sweepAngle = (segmentAngle - gapAngle) * proteinProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Carbs segment (Cyan)
        drawArc(
            color = Color(0xFF00BCD4),
            startAngle = -90f + segmentAngle,
            sweepAngle = (segmentAngle - gapAngle) * carbsProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Fat segment (Red)
        drawArc(
            color = Color(0xFFF44336),
            startAngle = -90f + segmentAngle * 2,
            sweepAngle = (segmentAngle - gapAngle) * fatProgress,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
fun NutrientCard(
    title: String,
    current: Int,
    target: Int,
    remaining: Int,
    unit: String,
    progress: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $target $unit",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$remaining $unit remaining",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}