package com.tada.mvl.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tada.mvl.data.model.BookResponse
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun BookResultScreen(
    vm: MapViewModel,
    onHistory: () -> Unit
) {
    val resp = vm.bookResponse.collectAsState().value

    BookResultContent(
        resp = resp,
        onHistory = onHistory
    )
}

@Composable
fun BookResultContent(
    resp: BookResponse?,
    onHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .navigationBarsPadding()
    ) {

        resp?.let { result ->

            LocationCard("A", result.a)

            Spacer(modifier = Modifier.height(16.dp))

            LocationCard("B", result.b)

            Spacer(modifier = Modifier.weight(1f))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Total Price",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "₹ ${result.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC400),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("View History")
            }

        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No booking yet")
        }
    }
}

@Composable
fun LocationCard(
    label: String,
    location: LocationInfo
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("AQI")
                Text(location.aqi.toString())
            }

            if (!location.nickname.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nickname")
                    Text(location.nickname!!)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookResultScreenPreview() {

    val fakeResponse = BookResponse(
        a = LocationInfo(
            name = "Chennai",
            aqi = 120,
            latitude = 13.0,
            longitude = 80.0,
            nickname = "Home"
        ),
        b = LocationInfo(
            name = "Bangalore",
            aqi = 90,
            latitude = 12.9,
            longitude = 77.5,
            nickname = null
        ),
        id = "123",
        price = 100.0
    )

    BookResultContent(
        resp = fakeResponse,
        onHistory = {}
    )
}