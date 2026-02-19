package com.tada.mvl.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tada.mvl.data.model.BookResponse
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

                            Divider()

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

                            Divider()

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


