package com.tada.mvl.ui.navigation

sealed class Destinations(val route: String) {
    object Map : Destinations("map")
    object Detail : Destinations("detail/{which}") {
        fun create(which: String) = "detail/$which"
    }
    object BookResult : Destinations("book_result")
    object History : Destinations("history")
}
