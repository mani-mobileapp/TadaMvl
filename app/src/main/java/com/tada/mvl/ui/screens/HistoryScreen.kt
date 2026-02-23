package com.tada.mvl.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val totalCount = history.size
    val totalPrice = history.sumOf { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(20.dp)
    ) {

        Text(
            text = "Booking History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
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
                        style = MaterialTheme.typography.headlineSmall
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
                        style = MaterialTheme.typography.headlineSmall
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

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(book) },
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "A :",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = book.a.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "AQI : ${book.a.aqi}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (!book.a.nickname.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nickname : ${book.a.nickname}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "B :",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = book.b.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "AQI : ${book.b.aqi}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (!book.b.nickname.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nickname : ${book.b.nickname}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider()

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "₹ ${book.price}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }
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
            .systemBarsPadding()
            .padding(20.dp)
    ) {

        Text(
            text = "Booking History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
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
                        style = MaterialTheme.typography.headlineSmall
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
                        style = MaterialTheme.typography.headlineSmall
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

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(book) },
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("A :", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(book.a.name, style = MaterialTheme.typography.titleMedium)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "AQI : ${book.a.aqi}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (!book.a.nickname.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nickname : ${book.a.nickname}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("B :", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(book.b.name, style = MaterialTheme.typography.titleMedium)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "AQI : ${book.b.aqi}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (!book.b.nickname.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nickname : ${book.b.nickname}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.labelLarge
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "₹ ${book.price}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }
    }
}


