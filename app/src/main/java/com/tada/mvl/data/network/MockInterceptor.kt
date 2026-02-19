package com.tada.mvl.data.network

import com.tada.mvl.data.model.AdministrativeEntry
import com.tada.mvl.data.model.LocalityInfo
import com.tada.mvl.data.model.ReverseResponse
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.nio.charset.StandardCharsets
import java.util.UUID
import kotlin.math.absoluteValue

class MockInterceptor(val gson: Gson) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val path = req.url.encodedPath
        val method = req.method
        val queryParams = req.url.queryParameterNames.associateWith { req.url.queryParameter(it) }

        val bodyString = when {
            req.body != null -> {
                val buffer = Buffer()
                req.body!!.writeTo(buffer)
                buffer.readString(StandardCharsets.UTF_8)
            }

            else -> ""
        }

        val responseJson = when {
            path.endsWith("reverse") -> {
                val lat = queryParams["lat"]?.toDoubleOrNull() ?: 37.5

                val admin = if (lat > 37.57) {
                    listOf(
                        AdministrativeEntry(2, "South Korea"),
                        AdministrativeEntry(3, "Seoul"),
                        AdministrativeEntry(4, "Gangnam District"),
                        AdministrativeEntry(5, "Yeoksam-dong")
                    )
                } else {
                    listOf(
                        AdministrativeEntry(2, "South Korea"),
                        AdministrativeEntry(3, "Seoul"),
                        AdministrativeEntry(4, "Seocho District"),
                        AdministrativeEntry(5, "Yangjae 2(i)-dong")
                    )
                }

                gson.toJson(ReverseResponse(LocalityInfo(administrative = admin)))
            }

            path.endsWith("/aqi") || path.endsWith("aqi") -> {
                val lat = queryParams["lat"]?.toDoubleOrNull() ?: 37.5665
                val lon = queryParams["lon"]?.toDoubleOrNull() ?: 126.9780
                val aqi = ((lat.hashCode() + lon.hashCode()).absoluteValue % 200)
                gson.toJson(mapOf("aqi" to aqi))
            }

            (path.endsWith("/books") || path.endsWith("books")) && method == "POST" -> {
                val parsed = gson.fromJson(bodyString, Map::class.java)
                val price = 10000 + (parsed.hashCode() % 5000)

                val response = HashMap(parsed)
                response["price"] = price
                response["id"] = UUID.randomUUID().toString()
                gson.toJson(response)
            }


            (path.endsWith("/books") || path.endsWith("books")) && method == "GET" -> {
                val list = listOf(
                    mapOf(
                        "a" to mapOf(
                            "latitude" to 36.564,
                            "longitude" to 127.001,
                            "aqi" to 30,
                            "name" to "서울 A 위치"
                        ),
                        "b" to mapOf(
                            "latitude" to 36.567,
                            "longitude" to 127.0,
                            "aqi" to 40,
                            "name" to "서울 B 위치"
                        ),
                        "price" to 10000
                    ),
                    mapOf(
                        "a" to mapOf(
                            "latitude" to 36.577,
                            "longitude" to 127.033,
                            "aqi" to 50,
                            "name" to "서울 C 위치"
                        ),
                        "b" to mapOf(
                            "latitude" to 36.567,
                            "longitude" to 127.0,
                            "aqi" to 60,
                            "name" to "서울 D 위치"
                        ),
                        "price" to 20000
                    )
                )
                gson.toJson(list)
            }

            else -> {
                gson.toJson(mapOf("message" to "unknown endpoint"))
            }
        }

        val body = responseJson.toByteArray().toResponseBody("application/json".toMediaTypeOrNull())

        return Response.Builder()
            .request(req)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body)
            .addHeader("content-type", "application/json")
            .build()
    }
}
