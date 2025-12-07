package pam.mobile.usecase3fp.api

import retrofit2.http.*

interface ApiService {

    // Existing endpoints
    @GET("food_items")
    suspend fun getFoodItemsRaw(
        @Query("select") select: String = "*"
    ): String

    @GET("daily_intakes")
    suspend fun getDailyIntakesRaw(
        @Query("intake_date") intakeDate: String? = null,
        @Query("select") select: String = "*"
    ): String

    @GET("daily_targets")
    suspend fun getDailyTargetsRaw(
        @Query("select") select: String = "*"
    ): String

    // TAMBAHAN BARU - Get images
    @GET("images")
    suspend fun getImagesRaw(
        @Query("select") select: String = "*"
    ): String

    @Headers("Prefer: return=minimal")
    @PATCH("daily_targets")
    suspend fun updateDailyTargets(
        @Query("id") id: String,
        @Body body: UpdateTargetsRequest
    ): Unit

    @Headers("Prefer: return=minimal")
    @POST("daily_targets")
    suspend fun insertDailyTargets(
        @Body body: UpdateTargetsRequest
    ): Unit
}

// Request body untuk update targets
data class UpdateTargetsRequest(
    val calories: Int,
    val protein: Int,
    val carbohydrates: Int,
    val fat: Int
)