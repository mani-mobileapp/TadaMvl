package com.tada.mvl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun BookResultScreen(vm: MapViewModel, onBack: () -> Unit, onHistory: () -> Unit) {
    val resp = vm.bookResponse.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Text("Booking Result", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        resp?.let {
            Text("A: ${it.a.nickname ?: it.a.name} (AQI ${it.a.aqi})")
            Text("B: ${it.b.nickname ?: it.b.name} (AQI ${it.b.aqi})")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Price: ${it.price}")
        } ?: Text("No booking yet")

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onHistory, modifier = Modifier.fillMaxWidth()) {
            Text("Go to History")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Map (reset)")
        }
    }
}
