package com.example.stackoverflow.presentation.questiondetail

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.domain.utils.Result
import com.example.stackoverflow.presentation.webview.WebViewActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QuestionDetailScreen(
    question: QuestionEntity,
    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val answers by viewModel.getAnswers(questionId = question.id).collectAsState(null)
    val result by viewModel.getErrorFlow().collectAsState(Result.Loading)

    LaunchedEffect(Unit) {
        viewModel.fetchAnswers(questionId = question.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stack Overflow") },
                modifier = Modifier.shadow(12.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValue)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = question.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
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

                        is Result.Empty -> {
                            LaunchedEffect(result) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "No data available",
                                        actionLabel = "Dismiss"
                                    )
                                }
                            }
                        }

                        is Result.Loading -> {
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
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ShowAnswersBody(answers: List<AnswerEntity>) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(answers.size) { index ->
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "upvote icon")
                    Text(text = answers[index].score.toString())
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "down vote icon")
                }
                Column {
                    answers[index].body?.let {
                        Spacer(Modifier.height(16.dp))
                        HtmlText(it)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        GlideImage(
                            model = answers[index].owner.profileImageLink,
                            contentDescription = "Cat Image",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val intent = Intent(context, WebViewActivity::class.java)
                                    intent.putExtra("profile_url", answers[index].owner.profileLink)
                                    context.startActivity(intent)
                                },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Answered by ${answers[index].owner.name}"
                        )
                    }
                    if(index < answers.size - 1 ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun HtmlText(html: String) {
    val spannedText = Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    val annotatedString = remember(spannedText) {
        buildAnnotatedString {
            append(spannedText.toString())
        }
    }
    Text(text = annotatedString)
}
