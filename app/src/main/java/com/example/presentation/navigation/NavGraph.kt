package com.example.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.MovieDatabase
import com.example.data.repository.MovieRepositoryImpl
import com.example.presentation.aimatch.AiMatchScreen
import com.example.presentation.aimatch.AiMatchViewModel
import com.example.presentation.detail.DetailScreen
import com.example.presentation.detail.DetailViewModel
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.HomeViewModel
import com.example.presentation.search.SearchScreen
import com.example.presentation.search.SearchViewModel
import com.example.presentation.watchlist.WatchlistScreen
import com.example.presentation.watchlist.WatchlistViewModel
import com.example.ui.theme.CinemaRed

sealed class BottomNavRoute(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    object Home : BottomNavRoute("home", "Home", Icons.Filled.Movie, Icons.Outlined.Movie)
    object Search : BottomNavRoute("search", "Discover", Icons.Filled.Search, Icons.Outlined.Search)
    object Watchlist : BottomNavRoute("watchlist", "Vault", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object AiMatch : BottomNavRoute("aimatch", "CineBot", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
}

@Composable
fun MainAppNavigation(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current.applicationContext
    val repository = MovieRepositoryImpl(database = MovieDatabase.getDatabase(context))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavRoute.Home,
        BottomNavRoute.Search,
        BottomNavRoute.Watchlist,
        BottomNavRoute.AiMatch
    )

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = CinemaRed
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.activeIcon else item.inactiveIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(text = item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CinemaRed,
                                selectedTextColor = CinemaRed,
                                indicatorColor = CinemaRed.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavRoute.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(BottomNavRoute.Home.route) {
                val homeViewModel: HomeViewModel = viewModel {
                    HomeViewModel(repository)
                }
                HomeScreen(
                    viewModel = homeViewModel,
                    onMovieClick = { movieId -> navController.navigate("detail/$movieId") },
                    onSearchClick = { navController.navigate(BottomNavRoute.Search.route) },
                    onAiMatchClick = { navController.navigate(BottomNavRoute.AiMatch.route) }
                )
            }

            composable(BottomNavRoute.Search.route) {
                val searchViewModel: SearchViewModel = viewModel {
                    SearchViewModel(repository)
                }
                SearchScreen(
                    viewModel = searchViewModel,
                    onMovieClick = { movieId -> navController.navigate("detail/$movieId") }
                )
            }

            composable(BottomNavRoute.Watchlist.route) {
                val watchlistViewModel: WatchlistViewModel = viewModel {
                    WatchlistViewModel(repository)
                }
                WatchlistScreen(
                    viewModel = watchlistViewModel,
                    onMovieClick = { movieId -> navController.navigate("detail/$movieId") }
                )
            }

            composable(BottomNavRoute.AiMatch.route) {
                val aiViewModel: AiMatchViewModel = viewModel {
                    AiMatchViewModel(repository)
                }
                AiMatchScreen(
                    viewModel = aiViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "detail/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: 1
                val detailViewModel: DetailViewModel = viewModel {
                    DetailViewModel(movieId = movieId, repository = repository)
                }
                DetailScreen(
                    viewModel = detailViewModel,
                    onBackClick = { navController.popBackStack() },
                    onMovieClick = { targetMovieId -> navController.navigate("detail/$targetMovieId") }
                )
            }
        }
    }
}
