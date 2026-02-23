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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.tada.mvl.ui.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tada.mvl.ui.navigation.Destinations

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

    LaunchedEffect(cameraState.isMoving) {
        if (!cameraState.isMoving) {
            val pos = cameraState.position.target
            vm.updateCamera(pos.latitude, pos.longitude)
            vm.updateAqi(pos.latitude, pos.longitude)
        }
    }
    LaunchedEffect(Unit) {
        vm.navigateToBook.collect {
            navController.navigate(Destinations.BookResult.route)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState
        ) {
            Marker(
                state = MarkerState(position = cameraState.position.target)
            )
        }
        MapContent(
            aqi = aqi,
            slotAName = slotA?.nickname ?: slotA?.name,
            slotAAqi = slotA?.aqi,
            slotBName = slotB?.nickname ?: slotB?.name,
            slotBAqi = slotB?.aqi,
            buttonText = buttonText,
            loading = loading,
            onAClick = {
                if (slotA != null)
                    navController.navigate("detail/A")
            },
            onBClick = {
                if (slotB != null)
                    navController.navigate("detail/B")
            },
            onButtonClick = {
                val center = cameraState.position.target
                vm.onVClicked(center.latitude, center.longitude)
            },
            onHistoryClick = {
                navController.navigate("history")
            }
        )

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

                LocationRow(
                    title = "A",
                    location = slotA?.nickname ?: slotA?.name,
                    aqi = slotA?.aqi,
                    onClick = {
                        if (slotA != null)
                            navController.navigate("detail/A")
                    }
                )

                HorizontalDivider()

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
                    }
                )
                {
                    Text(buttonText)
                }

            }
        }

        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                vm.clearError()
            }
        }

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

@Composable
fun MapContent(
    aqi: Int?,
    slotAName: String?,
    slotAAqi: Int?,
    slotBName: String?,
    slotBAqi: Int?,
    buttonText: String,
    loading: Boolean,
    onAClick: () -> Unit,
    onBClick: () -> Unit,
    onButtonClick: () -> Unit,
    onHistoryClick: () -> Unit,
    showFakeMap: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {

        if (showFakeMap) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text("Google Map Preview", style = MaterialTheme.typography.titleMedium)
            }
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "AQI: ${aqi ?: "--"}",
                modifier = Modifier.padding(12.dp)
            )
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(Modifier.padding(20.dp)) {

                LocationRow(
                    title = "A",
                    location = slotAName,
                    aqi = slotAAqi,
                    onClick = onAClick
                )

                HorizontalDivider()
                
                LocationRow(
                    title = "B",
                    location = slotBName,
                    aqi = slotBAqi,
                    onClick = onBClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onButtonClick) {
                    Text(buttonText)
                }
            }
        }

        if (loading) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        FloatingActionButton(
            onClick = onHistoryClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.History, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreview() {

    MapContent(
        aqi = 120,
        slotAName = "Home",
        slotAAqi = 110,
        slotBName = "Office",
        slotBAqi = 95,
        buttonText = "Book",
        loading = false,
        onAClick = {},
        onBClick = {},
        onButtonClick = {},
        onHistoryClick = {},
        showFakeMap = true
    )
}


