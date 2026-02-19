package com.tada.mvl.ui.screens


import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.tada.mvl.ui.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    vm: MapViewModel
) {
    val context = LocalContext.current

    val cameraState = rememberCameraPositionState()

    val aqi by vm.currentAqi.collectAsState()
    val slotA by vm.slotA.collectAsState()
    val slotB by vm.slotB.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val buttonText by vm.buttonState.collectAsState()

    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Permission launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        loc?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            cameraState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    15f
                                )
                            )
                        }
                    }
            }
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Observe camera movement (new maps compose way)
    LaunchedEffect(cameraState.isMoving) {
        if (!cameraState.isMoving) {
            val pos = cameraState.position.target
            vm.updateCamera(pos.latitude, pos.longitude)
            vm.updateAqi(pos.latitude, pos.longitude)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        // FULL SCREEN MAP
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState
        ) {
            Marker(
                state = MarkerState(position = cameraState.position.target)
            )
        }

        // AQI Top Right Card
        ElevatedCard(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "AQI: ${aqi ?: "--"}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Bottom Sheet Style Panel
        ElevatedCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                // A LABEL
                LocationRow(
                    title = "A",
                    location = slotA?.nickname ?: slotA?.name,
                    aqi = slotA?.aqi,
                    onClick = {
                        if (slotA != null)
                            navController.navigate("detail/A")
                    }
                )

                Divider()

                // B LABEL
                LocationRow(
                    title = "B",
                    location = slotB?.nickname ?: slotB?.name,
                    aqi = slotB?.aqi,
                    onClick = {
                        if (slotB != null)
                            navController.navigate("detail/B")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val center = cameraState.position.target
                        vm.onVClicked(center.latitude, center.longitude)

                        if (buttonText == "Book") {
                            navController.navigate("book_result")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(buttonText)
                }
            }
        }

        // Loading Overlay
        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Error
        error?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                vm.clearError()
            }
        }

        // History FAB
        FloatingActionButton(
            onClick = { navController.navigate("history") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
        }
    }
}
@Composable
fun LocationRow(
    title: String,
    location: String?,
    aqi: Int?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = location != null) { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(40.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location ?: "Select location",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = aqi?.toString() ?: "--",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

