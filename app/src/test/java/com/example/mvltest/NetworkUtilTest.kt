package com.example.mvltest

import android.content.Context
import android.net.ConnectivityManager
import com.tada.mvl.utils.NetworkUtil
import junit.framework.TestCase.assertFalse
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NetworkUtilTest {

    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()

    @Test
    fun `returns false when no active network`() {

        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(connectivityManager)

        whenever(connectivityManager.activeNetwork)
            .thenReturn(null)

        val result = NetworkUtil.isInternetAvailable(context)

        assertFalse(result)
    }
}
