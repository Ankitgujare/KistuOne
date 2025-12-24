# KitsuOne 🦊

**KitsuOne** is a modern, native Android application for streaming anime, tracking watchlists, and discovering new series. Built with the latest Android technologies, it offers a seamless and immersive experience for anime enthusiasts.

## 🚀 Features

- **Immersive User Interface**: A sleek, dark-themed UI built entirely with **Jetpack Compose** for smooth animations and responsive design.
- **Anime Discovery**: Browse trending, top airing, upcoming, and most popular anime.
- **Streaming**: Watch episodes directly within the app using **ExoPlayer** (Media3) with support for:
  - HLS Streaming
  - Multi-language Audio (Dub/Sub)
  - Subtitles/Captions
- **Search & Filter**: Powerful search functionality with filters for genres, seasons, and formats.
- **Watchlist**: Keep track of what you're watching (or plan to watch) locally using **Room Database**.
- **Schedule**: View airing schedules for the week.
- **Authentication**: Secure login and signup powered by **Firebase Auth**.
- **Data Source**: Powered by a custom `hianime-API` for fetching anime data.

## 🛠️ Tech Stack & Architecture

KitsuOne follows modern Android development best practices:

- **Language**: Kotlin
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) with Kotlinx Serialization
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Video Player**: [Media3 (ExoPlayer)](https://developer.android.com/media/media3)
- **Async Processing**: Kotlin Coroutines & Flow

### Architecture Overview

```mermaid
graph TD
    UI[UI Layer (Screens & Composables)] --> VM[ViewModel]
    VM --> Rep[Repository]
    Rep --> Remote[Remote Data Source (Retrofit)]
    Rep --> Local[Local Data Source (Room)]
    Remote --> API[HiAnime API]
    Local --> DB[Device Database]
```

## 📂 Project Structure

- `ui/`: Contains all Compose screens (`home`, `details`, `player`, `search`, etc.) and UI components.
- `data/`: Repositories, API interfaces, and Data Models.
- `di/`: Hilt Dependency Injection modules.
- `model/`: Data classes representing Anime entities.

## 🤝 Contribution

We welcome contributions from the community! Whether it's fixing bugs, adding new features, or improving documentation, your help is appreciated.

### How to Contribute

1.  **Fork** the repository.
2.  Create a new branch for your feature or fix: `git checkout -b feature/amazing-feature`.
3.  **Commit** your changes: `git commit -m 'Add some amazing feature'`.
4.  **Push** to the branch: `git push origin feature/amazing-feature`.
5.  Open a **Pull Request**.

### Guidelines

- Follow the existing code style (Kotlin coding conventions).
- Ensure your code builds without errors.
- If adding a new feature, please describe it clearly in your PR.

## 📄 License

[Add License Here, e.g., MIT, Apache 2.0]

---
*Your Anime Journey Begins with KitsuOne.*
