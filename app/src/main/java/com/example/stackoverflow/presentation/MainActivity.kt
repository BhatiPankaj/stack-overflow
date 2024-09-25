package com.example.stackoverflow.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stackoverflow.data.room.entity.QuestionEntity
import com.example.stackoverflow.presentation.questiondetail.QuestionDetailScreen
import com.example.stackoverflow.presentation.questionlist.QuestionListScreen
import com.example.stackoverflow.presentation.questionsearchscreen.QuestionSearchScreen
import com.example.stackoverflow.presentation.theme.StackOverflowTheme
import com.example.stackoverflow.presentation.util.NavDestinations
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackOverflowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StackOverflowApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StackOverflowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavDestinations.QUESTION_LIST_SCREEN
    ) {
        composable(NavDestinations.QUESTION_LIST_SCREEN) {
            QuestionListScreen(navController = navController)
        }
        composable(NavDestinations.QUESTION_DETAIL_SCREEN + "/{question}",
            arguments = listOf(
                navArgument("question") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val question = backStackEntry.arguments?.getString("question")
            question?.let {
                QuestionDetailScreen(Gson().fromJson(question, QuestionEntity::class.java))
            }
        }
        composable(NavDestinations.QUESTION_SEARCH_SCREEN) {
            QuestionSearchScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StackOverflowTheme {
        StackOverflowApp()
    }
}