package com.tada.mvl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.tada.mvl.data.model.AdministrativeEntry
import com.tada.mvl.data.model.AqiResponse
import com.tada.mvl.data.model.BookResponse
import com.tada.mvl.data.model.LocalityInfo
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.data.model.ReverseResponse
import com.tada.mvl.data.network.ApiService
import com.tada.mvl.data.repository.MvlRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MvlRepositoryTest {

    private lateinit var repository: MvlRepository
    private val api: ApiService = mock()
    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()
    private val network: Network = mock()
    private val capabilities: NetworkCapabilities = mock()

    @Before
    fun setup() {

        // Mock internet available
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)

        whenever(connectivityManager.activeNetwork)
            .thenReturn(network)

        whenever(connectivityManager.getNetworkCapabilities(network))
            .thenReturn(capabilities)

        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(true)

        repository = MvlRepository(api, context)
    }

    @Test
    fun `fetchAqi returns value when api successful`() = runTest {

        val response = Response.success(AqiResponse(55))
        whenever(api.fetchAqi(10.0, 20.0)).thenReturn(response)

        val result = repository.fetchAqi(10.0, 20.0)

        assertEquals(55, result)
    }

    @Test
    fun `fetchAqi returns -1 when api fails`() = runTest {

        val response = Response.error<AqiResponse>(
            500,
            "error".toResponseBody()
        )
        whenever(api.fetchAqi(10.0, 20.0)).thenReturn(response)

        val result = repository.fetchAqi(10.0, 20.0)

        assertEquals(-1, result)
    }

    @Test
    fun `postBook returns response when successful`() = runTest {

        val location = LocationInfo(1.0, 2.0, 30, "Test")
        val expected = BookResponse("1", location, location, 10000.0)

        whenever(api.postBook(any())).thenReturn(Response.success(expected))

        val result = repository.postBook(location, location)

        assertEquals(10000.0, result.price, 0.0)
    }

    @Test(expected = IllegalStateException::class)
    fun `throws exception when no internet`() = runTest {

        // Simulate no internet
        whenever(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(false)

        repository.fetchAqi(1.0, 2.0)
    }
    @Test
    fun `fetchLocation uses cache`() = runTest {

        val response = Response.success(AqiResponse(50))
        whenever(api.fetchAqi(any(), any())).thenReturn(response)

        whenever(api.reverseGeocode(any(), any()))
            .thenReturn(Response.success(
                ReverseResponse(
                    LocalityInfo(
                        administrative = listOf(
                            AdministrativeEntry(1, "Seoul")
                        )
                    )
                )
            ))

        val first = repository.fetchLocation(10.0, 20.0)
        val second = repository.fetchLocation(10.0, 20.0)

        // Should be same instance from cache
        assertEquals(first, second)
    }

}
