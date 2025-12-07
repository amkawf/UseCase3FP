package pam.mobile.usecase3fp.api

import retrofit2.http.*

interface ApiService {

    // Get all food items - return raw String
    @GET("food_items")
    suspend fun getFoodItemsRaw(
        @Query("select") select: String = "*"
    ): String

    // Get daily intakes by date - return raw String
    @GET("daily_intakes")
    suspend fun getDailyIntakesRaw(
        @Query("intake_date") intakeDate: String? = null,
        @Query("select") select: String = "*"
    ): String

    // Get daily targets - return raw String
    @GET("daily_targets")
    suspend fun getDailyTargetsRaw(
        @Query("select") select: String = "*"
    ): String

    // Update daily targets - GANTI jadi Unit (no response expected)
    @Headers("Prefer: return=minimal")
    @PATCH("daily_targets")
    suspend fun updateDailyTargets(
        @Query("id") id: String,
        @Body body: UpdateTargetsRequest
    ): Unit  // GANTI dari tanpa return type jadi Unit

    // Insert daily targets - GANTI jadi Unit (no response expected)
    @Headers("Prefer: return=minimal")
    @POST("daily_targets")
    suspend fun insertDailyTargets(
        @Body body: UpdateTargetsRequest
    ): Unit  // GANTI dari tanpa return type jadi Unit
}

// Request body untuk update targets
data class UpdateTargetsRequest(
    val calories: Int,
    val protein: Int,
    val carbohydrates: Int,
    val fat: Int
)