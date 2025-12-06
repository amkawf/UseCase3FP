package pam.mobile.usecase3fp.api

import com.google.gson.annotations.SerializedName
import pam.mobile.usecase3fp.model.DailyIntakeResponse
import pam.mobile.usecase3fp.model.DailyTargetsResponse
import pam.mobile.usecase3fp.model.FoodItemResponse
import retrofit2.http.*

interface ApiService {

    // Get all food items
    @GET("food_items")
    suspend fun getFoodItems(
        @Query("select") select: String = "*"
    ): List<FoodItemResponse>

    // Get daily intakes by date
    @GET("daily_intakes")
    suspend fun getDailyIntakes(
        @Query("intake_date") intakeDate: String? = null,
        @Query("select") select: String = "*"
    ): List<DailyIntakeResponse>

    // Get daily targets
    @GET("daily_targets")
    suspend fun getDailyTargets(
        @Query("select") select: String = "*"
    ): List<DailyTargetsResponse>

    // Update daily targets
    @Headers("Prefer: return=minimal")
    @PATCH("daily_targets")
    suspend fun updateDailyTargets(
        @Query("id") id: String,
        @Body body: UpdateTargetsRequest
    )

    // Insert daily targets
    @Headers("Prefer: return=minimal")
    @POST("daily_targets")
    suspend fun insertDailyTargets(
        @Body body: UpdateTargetsRequest
    )
}

// Request body untuk update targets
data class UpdateTargetsRequest(
    val calories: Int,
    val protein: Int,
    val carbohydrates: Int,
    val fat: Int
)