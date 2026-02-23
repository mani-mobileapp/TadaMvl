package com.tada.mvl.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, vm: com.tada.mvl.ui.viewmodel.MapViewModel) {
    val context = LocalContext.current
    val cameraState = rememberCameraPositionState()

    val aqi by vm.currentAqi.collectAsState()
    val slotA by vm.slotA.collectAsState()
    val slotB by vm.slotB.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val buttonText by vm.buttonState.collectAsState()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        cameraState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), 15f
                            )
                        )
                    }
                }
            }
        }

    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    LaunchedEffect(cameraState.isMoving) {
        if (!cameraState.isMoving) {
            val pos = cameraState.position.target
            vm.updateCamera(pos.latitude, pos.longitude)
            vm.updateAqi(pos.latitude, pos.longitude)
        }
    }

    LaunchedEffect(Unit) {
        vm.navigateToBook.collect { navController.navigate("bookResult") }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState
        ) { Marker(state = MarkerState(cameraState.position.target)) }

        OutlinedCard(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
        ) {
            Text(
                text = "AQI: ${aqi ?: "--"}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        MapBottomPanel(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            slotAName = slotA?.nickname ?: slotA?.name,
            slotAAqi = slotA?.aqi,
            slotBName = slotB?.nickname ?: slotB?.name,
            slotBAqi = slotB?.aqi,
            buttonText = buttonText,
            onAClick = { if (slotA != null) navController.navigate("detail/A") },
            onBClick = { if (slotB != null) navController.navigate("detail/B") },
            onButtonClick = {
                val center = cameraState.position.target
                vm.onVClicked(center.latitude, center.longitude)
            }
        )

        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }

        error?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                vm.clearError()
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("history") },
            containerColor = Color(0xFFFFC400),
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Icon(Icons.Default.History, contentDescription = null)
        }
    }
}

@Composable
fun MapBottomPanel(
    modifier: Modifier = Modifier,
    slotAName: String?,
    slotAAqi: Int?,
    slotBName: String?,
    slotBAqi: Int?,
    buttonText: String,
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(20.dp)) {

            LocationRowProduction("Starting From ?", slotAName, slotAAqi, onAClick)

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            LocationRowProduction("Where are you going ?", slotBName, slotBAqi, onBClick)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC400),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text(buttonText) }
        }
    }
}

@Composable
fun LocationRowProduction(title: String, location: String?, aqi: Int?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = location != null) { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(location ?: "Select location", style = MaterialTheme.typography.titleMedium)
        }
        Text(aqi?.toString() ?: "--", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA))
    ) {
        MapBottomPanel(
            modifier = Modifier.align(Alignment.BottomCenter),
            slotAName = "Home",
            slotAAqi = 110,
            slotBName = "Office",
            slotBAqi = 95,
            buttonText = "Book",
            onAClick = {},
            onBClick = {},
            onButtonClick = {}
        )
    }
}