package com.example.edustream.ui.navigation

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.edustream.features.admin.ui.AddCourseScreen
import com.example.edustream.features.admin.ui.AddQuizScreen
import com.example.edustream.features.admin.ui.AdminDashboardScreen
import com.example.edustream.features.admin.ui.ManageContentScreen
import com.example.edustream.features.auth.ui.login.LoginScreen
import com.example.edustream.features.auth.ui.login.LoginViewModel
import com.example.edustream.features.auth.ui.register.RegisterScreen
import com.example.edustream.features.certificates.ui.CertificateScreen
import com.example.edustream.features.home.ui.HomeScreen
import com.example.edustream.features.marketplace.ui.detail.CourseDetailScreen
import com.example.edustream.features.marketplace.ui.list.CourseListScreen
import com.example.edustream.features.marketplace.ui.mycourses.MyCoursesScreen
import com.example.edustream.features.marketplace.ui.search.SearchScreen
import com.example.edustream.features.marketplace.ui.quiz.QuizScreen
import com.example.edustream.features.payments.ui.history.PaymentHistoryScreen
import com.example.edustream.features.payments.ui.subscriptions.SubscriptionScreen
import com.example.edustream.features.player.ui.PlayerScreen
import com.example.edustream.features.profile.ui.ProfileScreen
import com.example.edustream.features.profile.ui.ProfileViewModel
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
data class QuizRoute(val quizId: String)

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

@Serializable
object AdminDashboardRoute

@Serializable
object AddCourseRoute

@Serializable
data class ManageContentRoute(val courseId: String)

@Serializable
data class AddQuizRoute(val courseId: String)

@Serializable
data class PdfViewerRoute(val pdfUrl: String, val title: String)

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
            val loginViewModel: LoginViewModel = hiltViewModel()
            val isAdmin by loginViewModel.isAdmin.collectAsState()
            
            LoginScreen(
                onNavigateToRegister = { navController.navigate(RegisterRoute) },
                onLoginSuccess = { 
                    if (isAdmin) {
                        navController.navigate(AdminDashboardRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    } else {
                        navController.navigate(HomeRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                },
                viewModel = loginViewModel
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
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val isAdmin by profileViewModel.isAdmin.collectAsState()
            
            LaunchedEffect(isAdmin) {
                if (isAdmin == true) {
                    navController.navigate(AdminDashboardRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            }

            if (isAdmin == false) {
                HomeScreen(
                    onNavigateToCourseList = { _ -> navController.navigate(CourseListRoute) },
                    onNavigateToCourseDetail = { courseId -> navController.navigate(CourseDetailRoute(courseId)) },
                    onNavigateToPlayer = { videoUrl, lectureId -> navController.navigate(PlayerRoute(videoUrl, lectureId)) }
                )
            }
        }
        
        composable<SearchRoute> { SearchScreen(onNavigateToDetail = { navController.navigate(CourseDetailRoute(it)) }) }
        composable<MyCoursesRoute> { MyCoursesScreen(onNavigateToDetail = { navController.navigate(CourseDetailRoute(it)) }) }
        composable<CourseListRoute> { CourseListScreen(onNavigateToDetail = { navController.navigate(CourseDetailRoute(it)) }) }
        composable<CourseDetailRoute> {
            CourseDetailScreen(
                onBackClick = { navController.popBackStack() },
                onEnrollClick = { navController.navigate(MyCoursesRoute) },
                onLectureClick = { _, lid, vurl -> navController.navigate(PlayerRoute(vurl, lid)) },
                onQuizClick = { navController.navigate(QuizRoute(it)) },
                onNoteClick = { _, url -> navController.navigate(PdfViewerRoute(url, "Study Note")) }
            )
        }
        composable<PlayerRoute> { backStackEntry ->
            val route: PlayerRoute = backStackEntry.toRoute()
            PlayerScreen(videoUrl = route.videoUrl, onBackClick = { navController.popBackStack() })
        }
        composable<QuizRoute> { QuizScreen(onBackClick = { navController.popBackStack() }) }
        composable<PaymentHistoryRoute> { PaymentHistoryScreen(onBackClick = { navController.popBackStack() }) }
        composable<SubscriptionRoute> { SubscriptionScreen(onBackClick = { navController.popBackStack() }) }
        composable<CertificateRoute> { CertificateScreen(onBackClick = { navController.popBackStack() }) }
        composable<ProfileRoute> {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToMyCourses = { navController.navigate(MyCoursesRoute) },
                onNavigateToSubscriptions = { navController.navigate(SubscriptionRoute) },
                onNavigateToHistory = { navController.navigate(PaymentHistoryRoute) },
                onNavigateToCertificates = { navController.navigate(CertificateRoute) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
                onNavigateToAdmin = { navController.navigate(AdminDashboardRoute) },
                onLogout = { navController.navigate(LoginRoute) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable<SettingsRoute> { SettingsScreen(onBackClick = { navController.popBackStack() }) }
        
        composable<PdfViewerRoute> { backStackEntry ->
            val route: PdfViewerRoute = backStackEntry.toRoute()
            PdfViewerScreen(
                url = route.pdfUrl,
                title = route.title,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- ADMIN ROUTES ---
        composable<AdminDashboardRoute> {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val isAdmin by profileViewModel.isAdmin.collectAsState()
            
            LaunchedEffect(isAdmin) {
                if (isAdmin == false) {
                    navController.navigate(HomeRoute) {
                        popUpTo(AdminDashboardRoute) { inclusive = true }
                    }
                }
            }

            if (isAdmin == true) {
                AdminDashboardScreen(
                    onBackClick = { navController.popBackStack() },
                    onAddCourseClick = { navController.navigate(AddCourseRoute) },
                    onManageContent = { navController.navigate(ManageContentRoute(it)) },
                    onNavigateToProfile = { navController.navigate(ProfileRoute) }
                )
            }
        }
        composable<AddCourseRoute> { AddCourseScreen(onBackClick = { navController.popBackStack() }) }
        composable<ManageContentRoute> { backStackEntry ->
            val route: ManageContentRoute = backStackEntry.toRoute()
            ManageContentScreen(
                courseId = route.courseId,
                onBackClick = { navController.popBackStack() },
                onAddQuizClick = { navController.navigate(AddQuizRoute(it)) }
            )
        }
        composable<AddQuizRoute> { backStackEntry ->
            val route: AddQuizRoute = backStackEntry.toRoute()
            AddQuizScreen(courseId = route.courseId, onBackClick = { navController.popBackStack() })
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    url: String,
    title: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    // Google Docs Viewer allows viewing PDFs without downloading
                    val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$url"
                    loadUrl(googleDocsUrl)
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
