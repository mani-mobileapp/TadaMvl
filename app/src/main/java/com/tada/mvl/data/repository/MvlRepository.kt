package com.tada.mvl.data.repository

import android.content.Context
import com.tada.mvl.data.model.BookRequest
import com.tada.mvl.data.model.BookResponse
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.data.network.ApiService
import com.tada.mvl.utils.NetworkUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MvlRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val context: Context
) {

    // In-memory cache keyed by rounded coordinates (3 decimals)
    private val cache = mutableMapOf<String, LocationInfo>()

    private fun key(lat: Double, lon: Double): String {
        val a = String.format("%.3f", lat)
        val b = String.format("%.3f", lon)
        return "$a,$b"
    }

    private fun ensureInternet() {
        if (!NetworkUtil.isInternetAvailable(context)) {
            throw IllegalStateException("No internet connection")
        }
    }

    suspend fun fetchAddressName(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
        ensureInternet()
        val resp = api.reverseGeocode(lat, lon)
        if (!resp.isSuccessful) return@withContext "Unknown address"
        val body = resp.body() ?: return@withContext "Unknown address"
        val admin = body.localityInfo.administrative
        val sorted = admin.sortedByDescending { it.order }
        val topTwo = sorted.take(2).map { it.name }
        return@withContext topTwo.joinToString(", ")
    }

    suspend fun fetchAqi(lat: Double, lon: Double): Int = withContext(Dispatchers.IO) {
        ensureInternet()
        val resp = api.fetchAqi(lat, lon)
        if (!resp.isSuccessful) return@withContext -1
        return@withContext resp.body()?.aqi ?: -1
    }

    suspend fun fetchLocation(lat: Double, lon: Double): LocationInfo =
        withContext(Dispatchers.IO) {
            val k = key(lat, lon)
            cache[k]?.let { return@withContext it }

            val name = fetchAddressName(lat, lon)
            val aqi = fetchAqi(lat, lon)
            val loc = LocationInfo(latitude = lat, longitude = lon, aqi = aqi, name = name)
            cache[k] = loc
            return@withContext loc
        }

    suspend fun refreshAqi(loc: LocationInfo): LocationInfo = withContext(Dispatchers.IO) {
        val aqi = fetchAqi(loc.latitude, loc.longitude)
        val new = loc.copy(aqi = aqi)
        cache[key(loc.latitude, loc.longitude)] = new
        return@withContext new
    }

    fun setNicknameFor(lat: Double, lon: Double, nickname: String) {
        val k = key(lat, lon)
        cache[k]?.let {
            val new = it.copy(nickname = nickname)
            cache[k] = new
        }
    }

    suspend fun postBook(a: LocationInfo, b: LocationInfo): BookResponse =
        withContext(Dispatchers.IO) {
            ensureInternet()
            val req = BookRequest(a, b)
            val resp = api.postBook(req)
            if (!resp.isSuccessful) throw IllegalStateException("Book failed")
            return@withContext resp.body()!!
        }

    suspend fun getBooks(year: Int, month: Int): List<BookResponse> = withContext(Dispatchers.IO) {
        ensureInternet()
        val resp = api.getBooks(year, month)
        if (!resp.isSuccessful) return@withContext emptyList()
        return@withContext resp.body() ?: emptyList()
    }

    fun getCachedLocations(): List<LocationInfo> = cache.values.toList()
}
