package pam.mobile.usecase3fp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import pam.mobile.usecase3fp.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top bar biru
        TopAppBar(
            title = {  },
            navigationIcon = {
                IconButton(onClick = { /* Handle back */ }) {
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
                text = "Dashboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Date picker
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousDay() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous day")
                }
                Text(
                    text = if (uiState.selectedDate == LocalDate.now())
                        "Hari ini"
                    else
                        formatter.format(uiState.selectedDate),
                    fontSize = 16.sp
                )
                IconButton(onClick = { viewModel.nextDay() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next day")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Multi-segment circular progress
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                MultiSegmentCircularProgress(
                    proteinProgress = uiState.proteinProgress.progress,
                    carbsProgress = uiState.carbsProgress.progress,
                    fatProgress = uiState.fatProgress.progress
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem("Protein", Color(0xFF2196F3))
                LegendItem("Karbo", Color(0xFF00BCD4))
                LegendItem("Lemak", Color(0xFFF44336))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Rekap Nutrisi Harian
            Text(
                text = "Rekap Nutrisi Harian",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kalori card
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

            // Protein card
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

            // Karbohidrat card
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

            // Lemak card
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

            // Asupan Nutrisi Terbaru
            Text(
                text = "Asupan Nutrisi Terbaru",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Food list
            uiState.foods.forEach { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = food.name,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${food.kcal} kcal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Divider()
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
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

        // Calculate segments
        val totalSegments = 3
        val segmentAngle = 360f / totalSegments
        val gapAngle = 8f

        // Protein segment (blue)
        val proteinSweep = (segmentAngle - gapAngle) * proteinProgress
        drawArc(
            color = Color(0xFF2196F3),
            startAngle = -90f,
            sweepAngle = proteinSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Carbs segment (cyan)
        val carbsStart = -90f + segmentAngle
        val carbsSweep = (segmentAngle - gapAngle) * carbsProgress
        drawArc(
            color = Color(0xFF00BCD4),
            startAngle = carbsStart,
            sweepAngle = carbsSweep,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Fat segment (red)
        val fatStart = -90f + segmentAngle * 2
        val fatSweep = (segmentAngle - gapAngle) * fatProgress
        drawArc(
            color = Color(0xFFF44336),
            startAngle = fatStart,
            sweepAngle = fatSweep,
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