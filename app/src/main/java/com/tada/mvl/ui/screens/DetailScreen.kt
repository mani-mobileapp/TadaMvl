package com.tada.mvl.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val originalNickname = loc?.nickname ?: ""
    val trimmedNickname = nickname.trim()
    val isValid = trimmedNickname.length <= 20
    val isChanged = trimmedNickname != originalNickname
    val isSaveEnabled = isValid && isChanged

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .navigationBarsPadding()
    ) {

        loc?.let { location ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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

                    DetailRow("AQI", location.aqi.toString())

                    if (!location.nickname.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DetailRow("Current Nickname", location.nickname!!)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    vm.setNickname(which, trimmedNickname)
                    onBack()
                },
                enabled = isSaveEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC400),
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Save Changes",
                    color = Color.Black
                )
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

    DetailContentPreview(fakeLocation)
}

@Composable
fun DetailContentPreview(location: LocationInfo) {

    var nickname by remember { mutableStateOf(location.nickname ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("A - ${location.name}")
                Spacer(modifier = Modifier.height(12.dp))
                Text("AQI: ${location.aqi}")

                if (!location.nickname.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Current Nickname: ${location.nickname}")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("Set Nickname (Optional)")
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {},
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC400)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Save Changes", color = Color.Black)
        }
    }
}