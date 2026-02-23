package com.tada.mvl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun DetailScreen(
    which: String,
    vm: MapViewModel,
    onBack: () -> Unit
) {

    val loc =
        if (which == "A")
            vm.slotA.collectAsState().value
        else
            vm.slotB.collectAsState().value

    var nickname by remember(loc) {
        mutableStateOf(loc?.nickname ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp)
    ) {

        Text(
            text = "Location $which Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        loc?.let { location ->

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = which,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailRow(label = "AQI", value = location.aqi.toString())

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Latitude", value = location.latitude.toString())

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Longitude", value = location.longitude.toString())

                    if (!location.nickname.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow(label = "Current Nickname", value = location.nickname!!)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Set Nickname (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nickname,
                        onValueChange = {
                            if (it.length <= 20) nickname = it
                        },
                        label = { Text("Max 20 characters") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    vm.setNickname(which, nickname)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {

    val fakeLocation = LocationInfo(
        name = "Chennai",
        aqi = 110,
        latitude = 13.0827,
        longitude = 80.2707,
        nickname = "Office"
    )

    DetailContent(
        which = "A",
        location = fakeLocation,
        onSave = {}
    )
}

@Composable
fun DetailContent(
    which: String,
    location: LocationInfo?,
    onSave: (String) -> Unit
) {
    var nickname by remember { mutableStateOf(location?.nickname ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Location $which Details",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        location?.let { it ->

            LocationCard("Details", it)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { if (it.length <= 20) nickname = it },
                label = { Text("Max 20 characters") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(nickname) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}

