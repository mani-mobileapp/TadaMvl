package com.tada.mvl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun DetailScreen(which: String, vm: MapViewModel, onBack: () -> Unit) {
    val loc = if (which == "A") vm.slotA.collectAsState().value else vm.slotB.collectAsState().value
    var nickname by remember { mutableStateOf(loc?.nickname ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text(text = "$which Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Name: ${loc?.name ?: "--"}")
        Text(text = "AQI: ${loc?.aqi ?: "--"}")
        Text(text = "Lat: ${loc?.latitude ?: "--"}  Lon: ${loc?.longitude ?: "--"}")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = { if (it.length <= 20) nickname = it },
            label = { Text("Nickname (max 20 chars, optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            vm.setNickname(which, nickname)
            onBack()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }
}
