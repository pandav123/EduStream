package com.example.edustream.features.marketplace.ui.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.marketplace.data.local.entities.NoteEntity
import com.example.edustream.features.marketplace.data.local.entities.QuizEntity
import com.example.edustream.ui.common.UiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    onBackClick: () -> Unit,
    onEnrollClick: () -> Unit,
    onLectureClick: (String, String, String) -> Unit, // courseId, lectureId, videoUrl
    onQuizClick: (String) -> Unit, // quizId
    onNoteClick: (String, String) -> Unit, // noteId, pdfUrl
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showPaymentDialog) {
        val course = (uiState as? UiState.Success)?.data?.course
        PaymentAppsDialog(
            amount = course?.price?.toString() ?: "0.0",
            onDismissRequest = { showPaymentDialog = false },
            onAppSelected = { packageName ->
                showPaymentDialog = false
                launchUpiIntent(
                    context = context,
                    packageName = packageName,
                    amount = course?.price?.toString() ?: "0.0",
                    onSuccess = {
                        viewModel.purchaseCourse()
                        onEnrollClick()
                    }
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    val course = state.data.course
                    val lectures = state.data.lectures
                    val quizzes = state.data.quizzes
                    val notes = state.data.notes
                    val isPurchased = course?.isPurchased == true

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            AsyncImage(
                                model = course?.thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = course?.title ?: "",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Category: ${course?.category}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = course?.description ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${course?.price}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isPurchased) {
                                        Button(
                                            onClick = { /* Already enrolled */ },
                                            modifier = Modifier.height(48.dp),
                                            enabled = false,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                        ) {
                                            Text("Enrolled")
                                        }
                                    } else {
                                        Button(
                                            onClick = { showPaymentDialog = true },
                                            modifier = Modifier.height(48.dp)
                                        ) {
                                            Text("Enroll Now")
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Curriculum",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        items(lectures) { lecture ->
                            LectureItem(
                                lecture = lecture,
                                isPurchased = isPurchased,
                                onClick = {
                                    if (isPurchased || lecture.isPreview) {
                                        onLectureClick(lecture.courseId, lecture.lectureId, lecture.videoUrl)
                                    } else {
                                        Toast.makeText(context, "Please enroll to watch this lecture", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }

                        if (quizzes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Quizzes",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            items(quizzes) { quiz ->
                                QuizItem(
                                    quiz = quiz,
                                    isPurchased = isPurchased,
                                    onClick = {
                                        if (isPurchased) {
                                            onQuizClick(quiz.quizId)
                                        } else {
                                            Toast.makeText(context, "Please enroll to take this quiz", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }

                        if (notes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Study Notes",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            items(notes) { note ->
                                NoteItem(
                                    note = note,
                                    isPurchased = isPurchased,
                                    progress = downloadProgress[note.noteId],
                                    onClick = {
                                        if (isPurchased) {
                                            onNoteClick(note.noteId, note.pdfUrl)
                                        } else {
                                            Toast.makeText(context, "Please enroll to access notes", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun LectureItem(
    lecture: LectureEntity,
    isPurchased: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (isPurchased || lecture.isPreview) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = lecture.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${lecture.duration / 60}m ${lecture.duration % 60}s",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (lecture.isPreview) {
            SuggestionChip(
                onClick = {},
                label = { Text("Preview", style = MaterialTheme.typography.labelSmall) }
            )
        } else if (!isPurchased) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun QuizItem(
    quiz: QuizEntity,
    isPurchased: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Quiz,
            contentDescription = null,
            tint = if (isPurchased) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${quiz.totalQuestions} Questions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        if (!isPurchased) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun NoteItem(
    note: NoteEntity,
    isPurchased: Boolean,
    progress: DownloadProgress?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = if (isPurchased) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1.0f)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (progress != null && progress.status == DownloadManager.STATUS_RUNNING) {
                val animatedProgress by animateFloatAsState(targetValue = progress.progress, label = "progress")
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                )
            } else {
                val dateStr = remember(note.createdAt) {
                    if (note.createdAt > 0) {
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(note.createdAt))
                    } else "PDF"
                }
                Text(
                    text = if (progress?.status == DownloadManager.STATUS_SUCCESSFUL) "Downloaded • ${note.fileSize}" else "PDF • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (progress?.status == DownloadManager.STATUS_SUCCESSFUL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }
        
        if (!isPurchased) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        } else {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "View",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAppsDialog(
    amount: String,
    onDismissRequest: () -> Unit,
    onAppSelected: (String?) -> Unit
) {
    val context = LocalContext.current
    val upiApps = remember { getInstalledUpiApps(context) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Pay ₹$amount",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a payment app to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            if (upiApps.isEmpty()) {
                Text(
                    text = "No UPI apps found on this device.",
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = { onAppSelected(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Complete with Dummy Payment")
                }
            } else {
                upiApps.forEach { app ->
                    PaymentAppItem(
                        name = app.name,
                        icon = app.icon,
                        onClick = { onAppSelected(app.packageName) }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                PaymentAppItem(
                    name = "Other UPI Apps / Dummy",
                    icon = null,
                    onClick = { onAppSelected(null) }
                )
            }
        }
    }
}

@Composable
fun PaymentAppItem(
    name: String,
    icon: android.graphics.drawable.Drawable?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Image(
                bitmap = icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
    }
}

data class UpiAppInfo(
    val name: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable
)

private fun getInstalledUpiApps(context: Context): List<UpiAppInfo> {
    val upiUri = Uri.parse("upi://pay")
    val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
    val packageManager = context.packageManager
    
    val resolveInfos = packageManager.queryIntentActivities(upiIntent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfos.map {
        UpiAppInfo(
            name = it.loadLabel(packageManager).toString(),
            packageName = it.activityInfo.packageName,
            icon = it.loadIcon(packageManager)
        )
    }
}

private fun launchUpiIntent(
    context: Context,
    packageName: String?,
    amount: String,
    onSuccess: () -> Unit
) {
    if (packageName == null) {
        // Fallback for demo or no apps
        Toast.makeText(context, "Simulating successful payment...", Toast.LENGTH_SHORT).show()
        onSuccess()
        return
    }

    val upiUri = Uri.Builder()
        .scheme("upi")
        .authority("pay")
        .appendQueryParameter("pa", "merchant@upi") // Replace with real VPA
        .appendQueryParameter("pn", "EduStream")
        .appendQueryParameter("tn", "Course Purchase")
        .appendQueryParameter("am", amount)
        .appendQueryParameter("cu", "INR")
        .build()

    val intent = Intent(Intent.ACTION_VIEW, upiUri)
    intent.setPackage(packageName)
    
    try {
        context.startActivity(intent)
        onSuccess()
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open $packageName", Toast.LENGTH_SHORT).show()
    }
}
