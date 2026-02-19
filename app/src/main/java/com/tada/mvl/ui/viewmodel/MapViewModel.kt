package com.tada.mvl.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tada.mvl.data.model.BookResponse
import com.tada.mvl.data.model.LocationInfo
import com.tada.mvl.data.repository.MvlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.Calendar

@HiltViewModel
class MapViewModel @Inject constructor(private val repo: MvlRepository) : ViewModel() {

    private val _cameraLatLon = MutableStateFlow(Pair(37.5665, 126.9780))

    private val _currentAqi = MutableStateFlow<Int?>(null)
    val currentAqi: StateFlow<Int?> = _currentAqi.asStateFlow()

    private val _slotA = MutableStateFlow<LocationInfo?>(null)
    val slotA: StateFlow<LocationInfo?> = _slotA.asStateFlow()

    private val _slotB = MutableStateFlow<LocationInfo?>(null)
    val slotB: StateFlow<LocationInfo?> = _slotB.asStateFlow()

    private val _bookResponse = MutableStateFlow<BookResponse?>(null)
    val bookResponse: StateFlow<BookResponse?> = _bookResponse.asStateFlow()
    private val _navigateToBook = MutableSharedFlow<Unit>()
    val navigateToBook = _navigateToBook.asSharedFlow()


    private val _history = MutableStateFlow<List<BookResponse>>(emptyList())
    val history: StateFlow<List<BookResponse>> = _history.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _buttonState = MutableStateFlow("Set A")
    val buttonState: StateFlow<String> = _buttonState.asStateFlow()

    fun updateCamera(lat: Double, lon: Double) {
        _cameraLatLon.value = Pair(lat, lon)
    }

    fun updateAqi(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val aqi = repo.fetchAqi(lat, lon)
                _currentAqi.value = aqi
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun setAFromCamera(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val loc = repo.fetchLocation(lat, lon)
                _slotA.value = loc
                updateButtonState()
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun setBFromCamera(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val loc = repo.fetchLocation(lat, lon)
                _slotB.value = loc
                updateButtonState()
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun setNickname(which: String, nickname: String) {
        viewModelScope.launch {
            try {
                val target = if (which == "A") _slotA.value else _slotB.value
                target?.let {
                    repo.setNicknameFor(it.latitude, it.longitude, nickname)
                    val updated = it.copy(nickname = nickname)
                    if (which == "A") _slotA.value = updated else _slotB.value = updated
                }
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }

    fun onVClicked(lat: Double, lon: Double) {
        when (_buttonState.value) {
            "Set A" -> setAFromCamera(lat, lon)
            "Set B" -> setBFromCamera(lat, lon)
            "Book" -> book()
        }
    }

    private fun updateButtonState() {
        _buttonState.value = when {
            _slotA.value == null -> "Set A"
            _slotB.value == null -> "Set B"
            else -> "Book"
        }
    }

    fun book() {
        val a = _slotA.value
        val b = _slotB.value
        if (a == null || b == null) return

        viewModelScope.launch {
            try {
                _loading.value = true
                val resp = repo.postBook(a, b)

                _bookResponse.value = resp

                _history.value += resp

                _navigateToBook.emit(Unit)

            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun fetchHistoryForCurrentMonth() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val cal = Calendar.getInstance()
                val y = cal.get(Calendar.YEAR)
                val m = cal.get(Calendar.MONTH) + 1
                val list = repo.getBooks(y, m)

                // ✅ Merge instead of replace
                _history.value = (_history.value + list).distinctBy { it.id }

            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun setFromHistory(book: BookResponse) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val aRefreshed = repo.refreshAqi(book.a)
                val bRefreshed = repo.refreshAqi(book.b)
                _slotA.value = aRefreshed
                _slotB.value = bRefreshed
                updateButtonState()
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
