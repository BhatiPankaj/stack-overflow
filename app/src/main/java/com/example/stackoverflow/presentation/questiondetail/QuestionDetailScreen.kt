package com.example.stackoverflow.presentation.questiondetail

import android.annotation.SuppressLint
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.domain.utils.Result
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QuestionDetailScreen(
    question: QuestionEntity?,
    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val answers by viewModel.getAnswersFlow(questionId = question?.id ?: 0).collectAsState(null)
    val result by viewModel.getErrorFlow().collectAsState(Result.Loading)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stack Overflow") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValue)
        ) {
            if (question != null) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = question.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
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

                            Result.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }

                            is Result.Success -> {
                                LaunchedEffect(result) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = (result as Result.Success<String>).data,
                                            actionLabel = "Dismiss"
                                        )
                                    }
                                }
                                if (answers != null) {
                                    ShowAnswersBody(answers!!)
                                } else {
                                    Text(
                                        "No answer available",
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text("No Question Details Available", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ShowAnswersBody(answers: List<AnswerEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(answers.size) { index ->
            answers[index].body?.let {
                Spacer(Modifier.height(16.dp))
                HtmlText(it)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun HtmlText(html: String) {
    AndroidView(
        factory = { context ->
            // try to check we can use some other view for it.
            TextView(context).apply {
                text = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
            }
        }
    )
}
