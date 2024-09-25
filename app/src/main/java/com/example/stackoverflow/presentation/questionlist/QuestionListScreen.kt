package com.example.stackoverflow.presentation.questionlist

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.domain.utils.Result
import com.example.stackoverflow.presentation.util.NavDestinations.QUESTION_DETAIL_SCREEN
import com.example.stackoverflow.presentation.util.NavDestinations.QUESTION_SEARCH_SCREEN
import com.example.stackoverflow.presentation.webview.WebViewActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionListScreen(
    viewModel: QuestionListViewModel = hiltViewModel(),
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val result by viewModel.questionsErrorFlow.collectAsState(initial = viewModel.lastResult)
    val questionList by viewModel.questionListFlow.collectAsState(null)

    LaunchedEffect(result) {
        viewModel.lastResult = result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stack Overflow") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(QUESTION_SEARCH_SCREEN)
                    }) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValue)
        ) {
            when (result) {
                is Result.Failure -> {
                    LaunchedEffect(result) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = (result as Result.Failure).errorMessage,
                                actionLabel = "Dismiss"
                            )
                        }
                    }
                }

                Result.Empty -> {
                    LaunchedEffect(result) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "No data available",
                                actionLabel = "Dismiss"
                            )
                        }
                    }
                }

                is Result.Success -> {
                    LazyColumn {
                        questionList?.let {
                            items(count = it.size) { index ->
                                QuestionCard(questionList!![index], navController)
                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillParentMaxWidth(),  // Padding on sides for proper alignment
                                    thickness = 1.dp,
                                    color = Color.LightGray // Grey color for the separator
                                )
                            }
                        }
                    }
                }

                Result.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun QuestionCard(question: QuestionEntity, navController: NavController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable {
                navController.navigate("$QUESTION_DETAIL_SCREEN/${Uri.encode(Gson().toJson(question))}")
            },
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row {
                question.tags.forEach { tag ->
                    Text(
                        text = tag,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GlideImage(
                    model = question.owner.profileImageLink,
                    contentDescription = "Cat Image",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .clickable {
                            val intent = Intent(context, WebViewActivity::class.java)
                            intent.putExtra("profile_url", question.owner.profileLink)
                            context.startActivity(intent)
                        },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Asked by ${question.owner.name}"
                )
            }
        }
    }
}

@Composable
fun LinkText(link: String) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append("LINK")

        // Add annotation for the link
        addStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            ),
            start = 6,
            end = 16
        )

        addStringAnnotation(
            tag = "URL",
            annotation = link,
            start = 6,
            end = 16
        )
    }

    // ClickableText to handle the link
    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    // Use intent to open the link in the browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}