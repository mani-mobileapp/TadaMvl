package com.tada.mvl.data.network

import com.tada.mvl.data.model.AqiResponse
import com.tada.mvl.data.model.BookRequest
import com.tada.mvl.data.model.BookResponse
import com.tada.mvl.data.model.ReverseResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("reverse")
    suspend fun reverseGeocode(@Query("lat") lat: Double, @Query("lon") lon: Double): Response<ReverseResponse>

    @GET("aqi")
    suspend fun fetchAqi(@Query("lat") lat: Double, @Query("lon") lon: Double): Response<AqiResponse>

    @POST("books")
    suspend fun postBook(@Body request: BookRequest): Response<BookResponse>

    @GET("books")
    suspend fun getBooks(@Query("year") year: Int, @Query("month") month: Int): Response<List<BookResponse>>
}
