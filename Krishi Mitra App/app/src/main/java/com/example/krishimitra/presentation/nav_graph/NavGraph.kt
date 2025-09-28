package com.example.krishimitra.presentation.nav_graph


import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.krishimitra.R
import com.example.krishimitra.presentation.assistant_screen.AssistantScreen
import com.example.krishimitra.presentation.assistant_screen.AssistantScreenViewModel
import com.example.krishimitra.presentation.auth_screen.AuthScreen
import com.example.krishimitra.presentation.auth_screen.AuthViewModel
import com.example.krishimitra.presentation.buy_sell_screen.BuySellScreen
import com.example.krishimitra.presentation.buy_sell_screen.BuySellScreenViewModel
import com.example.krishimitra.presentation.community_screen.ComunityMainScreen
import com.example.krishimitra.presentation.community_screen.StateCommunityScreen
import com.example.krishimitra.presentation.disease_prediction_screen.DiseasePredictionScreen
import com.example.krishimitra.presentation.disease_prediction_screen.DiseasePredictionViewModel
import com.example.krishimitra.presentation.feedback_screen.FeedbackScreen
import com.example.krishimitra.presentation.feedback_screen.FeedbackScreenViewModel
import com.example.krishimitra.presentation.fertilizer_recommendation.FertilizerRecommendationScreen
import com.example.krishimitra.presentation.home_screen.HomeScreen
import com.example.krishimitra.presentation.home_screen.HomeScreenViewModel
import com.example.krishimitra.presentation.mandi_screen.MandiScreen
import com.example.krishimitra.presentation.mandi_screen.MandiScreenViewModel
import com.example.krishimitra.presentation.notification_screen.NotificationScreen
import com.example.krishimitra.presentation.notification_screen.NotificationScreenViewModel
import com.example.krishimitra.presentation.profile_screen.ProfileScreen
import com.example.krishimitra.presentation.profile_screen.ProfileScreenViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: Activity
) {


    val firebaseAuth = FirebaseAuth.getInstance()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    val shouldShowBottomBar =
        currentDestination?.hierarchy?.any { it.hasRoute(Routes.HomeScreen::class) } == true
                || currentDestination?.hierarchy?.any { it.hasRoute(Routes.BuySellScreen::class) } == true
                || currentDestination?.hierarchy?.any { it.hasRoute(Routes.MandiScreen::class) } == true
                || currentDestination?.hierarchy?.any { it.hasRoute(Routes.ProfileScreen::class) } == true


    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {


            if (firebaseAuth.uid != null && shouldShowBottomBar) {

                CustomizedBottomAppBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }

        },
        floatingActionButton = {
            if (navController.currentDestination?.hierarchy?.any { it.hasRoute(Routes.HomeScreen::class) } == true) {

                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AssistantScreen()) {
                            launchSingleTop = true
                        }
                    },
                    containerColor = colorResource(id = R.color.slight_dark_green),
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sharp_support_agent_24),
                        contentDescription = "Chat bot agent"
                    )
                }
            }
        }
    ) { innerpadding ->
        NavHost(
            navController = navController,
            startDestination = if (firebaseAuth.uid == null) Routes.AuthScreen else Routes.HomeScreen
        ) {
            composable<Routes.AuthScreen> {
                val authViewModel = hiltViewModel<AuthViewModel>()
                AuthScreen(
                    state = authViewModel.state.collectAsStateWithLifecycle().value,
                    changeLanguage = authViewModel::changeLanguage,
                    signIn = authViewModel::signIn,
                    signUp = authViewModel::signUp,
                    moveToHomeScreen = {
                        navController.navigate(Routes.HomeScreen) {
                            popUpTo(0) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    getLocation = authViewModel::getLocation,
                    onEnableLocationPermission = {
                        authViewModel.onEnableLocationPermission(activity)
                    },
                    errorFlow = authViewModel.error
                )
            }
            composable<Routes.HomeScreen> {
                val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
                HomeScreen(
                    state = homeScreenViewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = homeScreenViewModel::onEvent,
                    moveToMandiScreen = {
                        navController.navigate(Routes.MandiScreen) { launchSingleTop = true }
                    },
                    moveToDiseasePredictionScreen = { uri ->
                        navController.navigate(Routes.DiseasePredictionScreen(uri.toString())) {
                            launchSingleTop = true
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    moveToKrishiBazar = {
                        navController.navigate(Routes.BuySellScreen) {
                            launchSingleTop = true
                        }
                    },
                    moveToNotificationScreen = {
                        navController.navigate(Routes.NotificationScreen) {
                            launchSingleTop = true
                        }
                    },
                    moveToFertilizerRecommendationScreen = {
                        navController.navigate(Routes.FertilizerRecommendationScreen) {
                            launchSingleTop = true
                        }
                    }
                )

            }
            composable<Routes.ProfileScreen> {
                val profileScreenViewModel = hiltViewModel<ProfileScreenViewModel>()
                ProfileScreen(
                    logOut = {
                        firebaseAuth.signOut()
                        navController.navigate(Routes.AuthScreen) {
                            popUpTo(0) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    state = profileScreenViewModel.state.collectAsStateWithLifecycle().value,
                    moveToFeedbackScreen = {
                        navController.navigate(Routes.FeedbackScreen)
                    }
                )
            }
            composable<Routes.BuySellScreen> {
                val buySellViewModel = hiltViewModel<BuySellScreenViewModel>()
                BuySellScreen(
                    buyScreenState = buySellViewModel.buyScreenState.collectAsStateWithLifecycle().value,
                    onEvent = buySellViewModel::onEvent,
                    sellScreenState = buySellViewModel.sellScreenState.collectAsStateWithLifecycle().value,
                    event = buySellViewModel.event,
                    scrollBahavior = scrollBehavior
                )
            }

            composable<Routes.MandiScreen> {
                val mandiViewModel = hiltViewModel<MandiScreenViewModel>()
                MandiScreen(
                    state = mandiViewModel.state.collectAsStateWithLifecycle().value,
                    mandiPrice = mandiViewModel.pagingData.collectAsLazyPagingItems(),
                    onEvent = mandiViewModel::onEvent,
                    scrollBehavior = scrollBehavior
                )
            }
            composable<Routes.DiseasePredictionScreen> {
                val diseasePredictionViewModel = hiltViewModel<DiseasePredictionViewModel>()
                val data = it.toRoute<Routes.DiseasePredictionScreen>()
                DiseasePredictionScreen(
                    imageUri = data.imageUri?.toUri(),
                    moveBackToScreen = {
                        navController.popBackStack()
                    },
                    state = diseasePredictionViewModel.state.collectAsStateWithLifecycle().value,
                    event = diseasePredictionViewModel.event,
                    onEvent = diseasePredictionViewModel::onEvent
                )
            }

            composable<Routes.CommunityMainScreen> {
                ComunityMainScreen(
                    moveToMessageScreen = { name ->
                        navController.navigate(Routes.StateCommunityScreen(name)) {

                            launchSingleTop = true

                        }
                    }
                )
            }
            composable<Routes.StateCommunityScreen> {
                val data = it.toRoute<Routes.StateCommunityScreen>()
                StateCommunityScreen(
                    state = data.state,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable<Routes.AssistantScreen> {
                val assistantViewModel = hiltViewModel<AssistantScreenViewModel>()

                AssistantScreen(
                    onEvent = assistantViewModel::onEvent,
                    state = assistantViewModel.state.collectAsStateWithLifecycle().value,
                    moveBackToHomeScreen = {
                        navController.popBackStack()
                    },
                    event = assistantViewModel.event
                )

            }

            composable<Routes.NotificationScreen>(
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "app://krishimitra.com/notifications?title={title}&body={body}&imageUrl={imageUrl}&webLink={webLink}"
                        action = Intent.ACTION_VIEW

                    }
                )
            ) {
                val notificationViewModel = hiltViewModel<NotificationScreenViewModel>()
                NotificationScreen(
                    moveBackToHomeScreen = {
                        navController.popBackStack()
                    },
                    state = notificationViewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = notificationViewModel::onEvent
                )
            }
            composable<Routes.FeedbackScreen> {
                val feedbackScreenViewModel = hiltViewModel<FeedbackScreenViewModel>()
                FeedbackScreen(
                    state = feedbackScreenViewModel.state.collectAsStateWithLifecycle().value,
                    onEvent = feedbackScreenViewModel::onEvent,
                    event = feedbackScreenViewModel.event,
                    snackbarHostState = snackbarHostState,
                    moveBackToProfileScreen = {
                        navController.popBackStack()
                    }
                )
            }
            composable<Routes.FertilizerRecommendationScreen> {
                FertilizerRecommendationScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }

}


@Composable
fun CustomizedBottomAppBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    BottomAppBar(
        modifier = Modifier
            .height(100.dp),
        containerColor = colorResource(id = R.color.light_green)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarInfo.bottomBarList.forEach { bottomBarInfo ->
                val isSelected =
                    currentDestination?.hierarchy?.any { it.hasRoute(bottomBarInfo.route::class) } == true

                IconButton(
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(bottomBarInfo.route) {
                                popUpTo(Routes.HomeScreen) { saveState = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = if (isSelected) colorResource(
                            id = R.color.slight_dark_green
                        ) else Color.Black
                    )

                ) {
                    Icon(
                        imageVector = bottomBarInfo.icon,
                        contentDescription = bottomBarInfo.name
                    )

                }
            }

        }
    }
}


