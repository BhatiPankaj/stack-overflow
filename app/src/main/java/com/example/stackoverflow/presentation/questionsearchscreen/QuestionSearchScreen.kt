package com.example.stackoverflow.presentation.questionsearchscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.stackoverflow.R
import com.example.stackoverflow.data.room.entity.SearchHistoryEntity
import com.example.stackoverflow.domain.utils.Result
import com.example.stackoverflow.presentation.questionlist.QuestionCard

@Composable
fun QuestionSearchScreen(
    viewModel: QuestionSearchViewModel = hiltViewModel(),
    navController: NavController
) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val result by viewModel.questionsErrorFlow.collectAsState(viewModel.lastResult)
    val questionList by viewModel.searchedList.collectAsState()
    var showSearchHistory by remember { mutableStateOf(false) }
    val searchedHistoryList by viewModel.searchedHistoryList.collectAsState(null)

    LaunchedEffect(result) {
        viewModel.lastResult = result
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = PaddingValues(8.dp))
                .offset(y = 70.dp)
        ) {
            when (result) {
                is Result.Failure -> {
                    Text(
                        (result as Result.Failure).errorMessage,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is Result.Empty -> {
                    Text("No question found", modifier = Modifier.align(Alignment.Center))
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

                null -> Spacer(modifier = Modifier.size(0.dp))
            }
        }
        Column(
            modifier = Modifier
                .defaultMinSize()
                .background(color = Color.White)
                .fillMaxWidth()
        ) {
            TextField(
                value = query,
                onValueChange = { searchQuery ->
                    query = searchQuery
                },
                label = if (!isFocused) {
                    {
                        Text(text = "Search..")
                    }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if(isFocused) {
                            showSearchHistory = true
                        }
                    },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.search(query)
                        showSearchHistory = false
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                trailingIcon = if(query.isNotEmpty()) {
                    {
                        IconButton(onClick = {
                            query = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Icon"
                            )
                        }
                    }
                } else null,
                maxLines = 2
            )

            if (showSearchHistory && !searchedHistoryList.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .background(color = Color.White)
                        .fillMaxWidth()
                ) {
                    LazyColumn {
                        items(searchedHistoryList!!.size) { index ->
                            SearchedHistoryCard(
                                searchedHistoryList!![index],
                                onClear = { viewModel.deletedSearchedHistory(searchedHistoryList!![index]) }) {
                                showSearchHistory = false
                                query = searchedHistoryList!![index].searchedText
                                viewModel.search(query)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchedHistoryCard(
    searchedHistory: SearchHistoryEntity,
    onClear: () -> Unit,
    onSearch: () -> Unit
) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.clickable { onSearch() }, verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.history_search),
                contentDescription = "searched icon",
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(18.dp))
            Text(text = searchedHistory.searchedText)
        }
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "searched text",
            modifier = Modifier.clickable { onClear() })
    }
}