package com.example.safetyandcheap.service

import com.example.safetyandcheap.service.dto.User
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.service.dto.search.AnnouncementSearchDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface MainApiService {
    @GET("user/get_current")
    suspend fun getCurrentUser(): Response<User>

    @PUT("user/update")
    suspend fun updateUser(@Body user: User, @Query("user_id") userId: Long): Response<String>

    @POST("user/get_code")
    suspend fun getCode(@Query("phoneNumber") phoneNumber: String): Response<String>

    @POST("user/verify_number")
    suspend fun verifyNumber(
        @Query("phone_number") phoneNumber: String,
        @Query("code") code: String
    ): Response<String>

    @POST("announcement/add")
    suspend fun addAnnouncement(@Body announcement: Announcement): Response<String>

    @PUT("announcement/delete")
    suspend fun deleteAnnouncement(@Query("id") announcementId: Long): Response<String>

    @PUT("announcement/update")
    suspend fun updateAnnouncement(
        @Query("id") announcementId: Long,
        @Body announcement: Announcement
    ): Response<String>

    @POST("announcement/search")
    suspend fun searchAnnouncement(@Body announcement: AnnouncementSearchDto): Response<List<Announcement>>

    @GET("announcement/find_by_author")
    suspend fun findByAuthor(@Query("author_id") authorId: Long): Response<List<Announcement>>

    @GET("user/get_cart")
    suspend fun getCart(): Response<List<Announcement>>

    @PUT("user/move_to_cart")
    suspend fun moveToCart(@Query("id") announcementId: Long): Response<String>

    @PUT("user/remove_from_cart")
    suspend fun removeFromCart(@Query("id") announcementId: Long): Response<String>
}