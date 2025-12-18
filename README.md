# 🦊 KitsuOne: The Ultimate Anime Experience

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white) ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![Status](https://img.shields.io/badge/Status-Active%20Dev-orange?style=for-the-badge)

**Experience anime like never before.** KitsuOne is a cutting-edge, native Android streaming application crafted with **Jetpack Compose** and **Kotlin**. It redefines mobile anime watching with a sleek, cinema-grade interface and powerful performance.

> 🚧 **Project Status**: Currently under active development. Join us on the journey to build the perfect anime app!

---

## 🔥 Why KitsuOne?

*   **🎬 Cinema in Your Pocket**: A fully custom-built video player with intuitive gesture controls (double-tap seek) and auto-subtitle support.
*   **🎨 Stunning Design**: A premium, immersive dark-themed UI that puts the artwork front and center. Built 100% with Material 3.
*   **🚀 Blazing Fast**: Powered by modern Android architecture (MVVM + Coroutines) for silky smooth navigation and zero lag.
*   **📅 Stay Updated**: Integrated release schedule so you never miss a new episode of your favorite airing shows.
*   **🔍 Power Search**: Find exactly what you want instantly with our smart search engine, featuring dynamic filters and debouncing.

## ✨ Key Features

-   **Seamless Streaming**: High-quality playback with multiple server options.
-   **Smart Library**: Track your watching progress and manage your watchlist locally.
-   **Deep Dive Details**: Full cast & crew info, voice actors, and relation graphs for every show.
-   **No Distractions**: Pure, ad-free viewing experience designed for fans, by fans.

## 🛠️ Built With Modern Tech

*   **Language**: Kotlin 100%
*   **UI**: Jetpack Compose (Material Design 3)
*   **Architecture**: MVVM (Clean Architecture principles)
*   **Network**: Retrofit + OkHttp
*   **Media**: AndroidX Media3 (ExoPlayer)
*   **DI**: Hilt / Manual DI pattern
*   **Async**: Coroutines & Flows

## 🚀 Getting Started

### 1. Backend Setup (Hianime API)
KitsuOne uses a powerful Node.js backend.
```bash
cd hianime-API
npm install
npm start
# Server runs on port 3030 🟢
```

### 2. Android Setup
1.  Clone this repository.
2.  Open in **Android Studio**.
3.  **Config**: Update `AppContainer.kt` with your local IP to connect to the backend (e.g., `http://192.168.1.X:3030/`).
4.  **Run**: Hit play and enjoy!

## 🤝 Contribution

We welcome contributions! Whether it's fixing bugs, adding new features, or improving documentation, feel free to open a Pull Request.

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---
*Made with ❤️ for the Anime Community*
