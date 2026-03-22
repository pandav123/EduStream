EduStream is a feature-rich, production-grade Android application designed for seamless online learning. Built using Jetpack Compose and Clean Architecture, it offers a high-performance experience for browsing courses, streaming high-quality video content, and managing subscriptions.

🚀 Key Features
•Adaptive Video Streaming: Powered by Media3 ExoPlayer, supporting lifecycle-aware playback, custom UI controls, and resource optimization.
•Smart Course Discovery: Real-time search and category filtering using Paging 3 for smooth scrolling through large datasets.
•Offline-First Architecture: Local persistence with Room Database ensures users can access their enrolled courses and subscription status without an active internet connection.
•Subscription Management: Simulated Google Play Billing integration for monthly/yearly plans with local synchronization.
•Modern Authentication: Secure onboarding with type-safe navigation and persistent user state.
•Profile & Progress Tracking: Personal dashboard for tracking certificates, payment history, and learning progress.


🛠 Tech Stack
•UI: Jetpack Compose (100% Declarative UI)
•Architecture: MVVM + Repository Pattern + Clean Architecture
•Dependency Injection: Hilt
•Networking: Retrofit & OkHttp
•Database: Room (Local Persistence)
•Media: Media3 ExoPlayer
•Navigation: Jetpack Navigation (Type-Safe Routes)
•Asynchronous Work: Kotlin Coroutines & SharedFlow/StateFlow
•Image Loading: Coil
