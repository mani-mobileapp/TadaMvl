package com.tada.mvl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun BookResultScreen(
    vm: MapViewModel,
    onHistory: () -> Unit
) {

    val resp = vm.bookResponse.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp)
    ) {

        Text(
            text = "Booking Result",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        resp?.let { result ->

            LocationCard(
                label = "Location A",
                location = result.a
            )

            Spacer(modifier = Modifier.height(16.dp))

            LocationCard(
                label = "Location B",
                location = result.b
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Total Price",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₹ ${result.price}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onHistory,
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AQI : ${location.aqi}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!location.nickname.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nickname : ${location.nickname}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


