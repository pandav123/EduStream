package com.example.edustream.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.edustream.features.auth.ui.login.LoginScreen
import com.example.edustream.features.auth.ui.register.RegisterScreen
import com.example.edustream.features.certificates.ui.CertificateScreen
import com.example.edustream.features.home.ui.HomeScreen
import com.example.edustream.features.marketplace.ui.detail.CourseDetailScreen
import com.example.edustream.features.marketplace.ui.list.CourseListScreen
import com.example.edustream.features.marketplace.ui.mycourses.MyCoursesScreen
import com.example.edustream.features.marketplace.ui.search.SearchScreen
import com.example.edustream.features.payments.ui.history.PaymentHistoryScreen
import com.example.edustream.features.payments.ui.subscriptions.SubscriptionScreen
import com.example.edustream.features.player.ui.PlayerScreen
import com.example.edustream.features.profile.ui.ProfileScreen
import com.example.edustream.features.settings.ui.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object HomeRoute

@Serializable
object SearchRoute

@Serializable
object MyCoursesRoute

@Serializable
object CourseListRoute

@Serializable
data class CourseDetailRoute(val courseId: String)

@Serializable
data class PlayerRoute(val videoUrl: String, val lectureId: String)

@Serializable
object PaymentHistoryRoute

@Serializable
object SubscriptionRoute

@Serializable
object CertificateRoute

@Serializable
object ProfileRoute

@Serializable
object SettingsRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduStreamNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoute,
        modifier = modifier
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(RegisterRoute) },
                onLoginSuccess = { 
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true } }
                }
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToCourseList = { category ->
                    navController.navigate(CourseListRoute)
                },
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(CourseDetailRoute(courseId))
                },
                onNavigateToPlayer = { videoUrl, lectureId ->
                    navController.navigate(PlayerRoute(videoUrl, lectureId))
                }
            )
        }
        composable<SearchRoute> {
            SearchScreen(
                onNavigateToDetail = { courseId ->
                    navController.navigate(CourseDetailRoute(courseId))
                }
            )
        }
        composable<MyCoursesRoute> {
            MyCoursesScreen(
                onNavigateToDetail = { courseId ->
                    navController.navigate(CourseDetailRoute(courseId))
                }
            )
        }
        composable<CourseListRoute> {
            CourseListScreen(
                onNavigateToDetail = { courseId ->
                    navController.navigate(CourseDetailRoute(courseId))
                }
            )
        }
        composable<CourseDetailRoute> {
            CourseDetailScreen(
                onBackClick = { navController.popBackStack() },
                onEnrollClick = {
                    // For demo: immediately navigate to My Courses or process purchase
                    navController.navigate(MyCoursesRoute)
                },
                onLectureClick = { courseId, lectureId ->
                    navController.navigate(PlayerRoute("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", lectureId))
                }
            )
        }
        composable<PlayerRoute> { backStackEntry ->
            val route: PlayerRoute = backStackEntry.toRoute()
            PlayerScreen(
                videoUrl = route.videoUrl,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<PaymentHistoryRoute> {
            PaymentHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<SubscriptionRoute> {
            SubscriptionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<CertificateRoute> {
            CertificateScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<ProfileRoute> {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToMyCourses = { navController.navigate(MyCoursesRoute) },
                onNavigateToSubscriptions = { navController.navigate(SubscriptionRoute) },
                onNavigateToHistory = { navController.navigate(PaymentHistoryRoute) },
                onNavigateToCertificates = { navController.navigate(CertificateRoute) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
