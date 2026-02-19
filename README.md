📱 MVVM AQI Booking App

A modern Android application built with MVVM architecture, Jetpack Compose, and a mocked backend to simulate production-ready behavior.

🏗 Architecture

The app follows MVVM (Model–View–ViewModel) with unidirectional data flow.

UI → ViewModel → Repository → API
Repository → ViewModel → UI

Layers

UI Layer

Jetpack Compose

Stateless composables observing ViewModel state

ViewModel Layer

Manages UI state

Business logic coordination

Kotlin Coroutines + StateFlow

Repository Layer

Single source of truth

In-memory caching

API abstraction

Network Layer

Retrofit

OkHttp

MockInterceptor (simulated backend)

🛠 Tech Stack

Jetpack Compose

Hilt (Dependency Injection)

Retrofit + OkHttp

Kotlin Coroutines + StateFlow

AAC ViewModel

Google Maps Compose

Repository Pattern

In-memory cache

Unit Testing

📱 Screens
1️⃣ Map Screen

Centered marker

AQI display

Set Location A / B

Book button

Internet check

Loading indicator

2️⃣ Detail Screen

Location name

AQI

Coordinates

Optional nickname (max 20 chars)

Card-based UI

3️⃣ Book Result Screen

Location A & B

Price

Mocked POST /books

4️⃣ History Screen

Mocked GET /books?year=YYYY&month=MM

Total booking count

Total price

Booking list

Click booking to repopulate A & B

🔄 Mocking Strategy

All network calls are intercepted using MockInterceptor.

To switch to real backend:

Remove MockInterceptor

Update base URL

Replace DTO mappings if needed

No business logic changes required.

🧠 Caching Strategy

AQI responses cached in-memory

Cache key = coordinates rounded to 3 decimal places

Prevents redundant API calls

Example:

(lat, lon) → rounded → cache key

🌐 Network Handling

Connectivity check before API calls

Loading indicators during operations

Graceful error handling

🧪 Testing

Unit tests for repository

Mocked Retrofit responses

Success & error cases covered