# WeatherApp
WeatherApp is a modern Android application designed to provide accurate and up-to-date weather forecasts for any location.

## Key Features
- **Current Weather Conditions**: Displays the current temperature, weather description, precipitation probability, humidity, and wind speed for the selected location.
- **Hourly Forecast**: A detailed weather forecast for the next 24 hours, showing temperature and weather conditions updated at regular intervals.
- **Daily Forecast**: A 5-day weather forecast providing maximum and minimum temperatures, as well as overall weather conditions for each day.
- **Search Functionality**: Users can search for weather information by city name.
- **Location-Based Weather**: Automatically fetches the weather for the user's current location using GPS.
- **Elegant UI**: Clean and intuitive interface with visual weather icons and smooth navigation.

## Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose, Navigation Component, RecyclerView
- **Architecture**: MVVM, Clean Architecture, StateFlow
- **Multithreading**: Coroutines
- **Dependency Injection**: Hilt
- **Networking**: Retrofit, Glide
- **Permissions**: Runtime permissions handling for location access

## How to Use
1. Clone the repository to your local machine.
2. Obtain an API key from [OpenWeatherMap](https://openweathermap.org/).
3. Add the API key to your `gradle.properties` file as:
   apikey=YOUR_API_KEY
