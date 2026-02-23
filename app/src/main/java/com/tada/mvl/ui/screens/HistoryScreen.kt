package com.tada.mvl.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun HistoryScreen(
    vm: MapViewModel,
    onSelect: (BookResponse) -> Unit
) {
    val history by vm.history.collectAsState()
    val loading by vm.loading.collectAsState()

    LaunchedEffect(Unit) {
        vm.fetchHistoryForCurrentMonth()
    }

    HistoryContent(
        history = history,
        loading = loading,
        onSelect = onSelect
    )
}

@Composable
fun HistoryContent(
    history: List<BookResponse>,
    loading: Boolean,
    onSelect: (BookResponse) -> Unit
) {

    val totalCount = history.size
    val totalPrice = history.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "Total Bookings",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = totalCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Price",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "₹ $totalPrice",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {

                items(history) { book ->

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(book) },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            InfoRow("A", book.a.name)

                            Spacer(modifier = Modifier.height(12.dp))

                            InfoRow("B", book.b.name)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Price",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "₹ ${book.price}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {

    val fakeList = listOf(
        BookResponse(
            id = "1",
            a = LocationInfo(
                latitude = 13.0827,
                longitude = 80.2707,
                aqi = 120,
                name = "Chennai",
                nickname = "Home"
            ),
            b = LocationInfo(
                latitude = 12.9716,
                longitude = 77.5946,
                aqi = 90,
                name = "Bangalore",
                nickname = null
            ),
            price = 450.0
        ),
        BookResponse(
            id = "2",
            a = LocationInfo(
                latitude = 17.3850,
                longitude = 78.4867,
                aqi = 80,
                name = "Hyderabad",
                nickname = null
            ),
            b = LocationInfo(
                latitude = 19.0760,
                longitude = 72.8777,
                aqi = 150,
                name = "Mumbai",
                nickname = "Trip"
            ),
            price = 600.0
        )
    )

    HistoryContent(
        history = fakeList,
        loading = false,
        onSelect = {}
    )
}