package com.uragiristereo.mikansei.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.uragiristereo.safer.compose.navigation.core.NavRoute
import com.github.uragiristereo.safer.compose.navigation.core.navigate
import com.github.uragiristereo.safer.compose.navigation.core.route
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.uragiristereo.mikansei.core.model.danbooru.Post
import com.uragiristereo.mikansei.core.model.danbooru.ShareOption
import com.uragiristereo.mikansei.core.preferences.model.ThemePreference
import com.uragiristereo.mikansei.core.product.component.ProductModalBottomSheetLayout
import com.uragiristereo.mikansei.core.product.theme.MikanseiTheme
import com.uragiristereo.mikansei.core.product.theme.Theme
import com.uragiristereo.mikansei.core.resources.R
import com.uragiristereo.mikansei.core.ui.LocalLambdaOnDownload
import com.uragiristereo.mikansei.core.ui.LocalLambdaOnShare
import com.uragiristereo.mikansei.core.ui.LocalMainScaffoldPadding
import com.uragiristereo.mikansei.core.ui.LocalScaffoldState
import com.uragiristereo.mikansei.core.ui.LocalScrollToTopChannel
import com.uragiristereo.mikansei.core.ui.LocalSnackbarHostState
import com.uragiristereo.mikansei.core.ui.LocalWindowSizeHorizontal
import com.uragiristereo.mikansei.core.ui.LocalWindowSizeVertical
import com.uragiristereo.mikansei.core.ui.WindowSize
import com.uragiristereo.mikansei.core.ui.composable.DimensionSubcomposeLayout
import com.uragiristereo.mikansei.core.ui.composable.RailScaffold
import com.uragiristereo.mikansei.core.ui.composable.SetSystemBarsColors
import com.uragiristereo.mikansei.core.ui.extension.backgroundElevation
import com.uragiristereo.mikansei.core.ui.extension.copy
import com.uragiristereo.mikansei.core.ui.modalbottomsheet.navigator.LocalBottomSheetNavigator
import com.uragiristereo.mikansei.core.ui.modalbottomsheet.navigator.rememberBottomSheetNavigator
import com.uragiristereo.mikansei.core.ui.navigation.HomeRoute
import com.uragiristereo.mikansei.core.ui.navigation.HomeRoutesString
import com.uragiristereo.mikansei.core.ui.navigation.MainRoute
import com.uragiristereo.mikansei.core.ui.navigation.NestedNavigationRoutes
import com.uragiristereo.mikansei.core.ui.rememberWindowSizeHorizontal
import com.uragiristereo.mikansei.core.ui.rememberWindowSizeVertical
import com.uragiristereo.mikansei.ui.appbars.MainBottomNavigationBar
import com.uragiristereo.mikansei.ui.appbars.MainNavigationRail
import com.uragiristereo.mikansei.ui.core.ShareDownloadDialog
import com.uragiristereo.mikansei.ui.navgraphs.BottomNavGraph
import com.uragiristereo.mikansei.ui.navgraphs.MainNavGraph
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val bottomNavController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val backStack by navController.currentBackStack.collectAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val previousRoute by remember {
        derivedStateOf {
            val backStackWithoutNested = backStack.filter {
                it.destination.route !in NestedNavigationRoutes
            }

            runCatching {
                backStackWithoutNested[backStackWithoutNested.size - 2].destination.route
            }.getOrElse {
                null
            }
        }
    }

    val preferences by viewModel.preferences.collectAsState()

    val notificationPermissionState = rememberPermissionState(
        permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> android.Manifest.permission.POST_NOTIFICATIONS
            else -> android.Manifest.permission.INTERNET
        },
        onPermissionResult = { isGranted ->
            if (!isGranted) {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message = context.getText(R.string.download_permission_denied).toString())
                }
            }

            viewModel.selectedPost?.let {
                viewModel.downloadPost(context, it)
            }
        }
    )

    val lambdaOnDownload = { post: Post ->
        if (!notificationPermissionState.status.isGranted || notificationPermissionState.status.shouldShowRationale) {
            viewModel.selectedPost = post
            notificationPermissionState.launchPermissionRequest()
        } else {
            viewModel.downloadPost(context, post)
        }
    }

    val lambdaOnShare = { post: Post, shareOption: ShareOption ->
        viewModel.sharePost(context, post, shareOption)
    }

    val lambdaOnRequestScrollToTop: () -> Unit = {
        scope.launch {
            viewModel.scrollToTopChannel.send(UUID.randomUUID().toString())
        }
    }

    BackHandler(
        enabled = viewModel.confirmExit && currentRoute == MainRoute.Home::class.route,
        onBack = remember {
            {
                scope.launch {
                    viewModel.confirmExit = false

                    scaffoldState.snackbarHostState.showSnackbar(
                        message = context.resources.getString(
                            /* id = */
                            R.string.press_back_again_to_exit,
                            /* ...formatArgs = */
                            context.resources.getString(R.string.app_name),
                        ),
                    )

                    viewModel.confirmExit = true
                }
            }
        },
    )

    BackHandler(
        enabled = !viewModel.confirmExit && currentRoute == MainRoute.Home::class.route,
        onBack = {
            (context as Activity).finishAffinity()
        },
    )

    MikanseiTheme(
        theme = when (preferences.theme) {
            ThemePreference.LIGHT -> Theme.LIGHT
            ThemePreference.DARK -> Theme.DARK
            else -> Theme.SYSTEM
        },
        blackTheme = preferences.blackTheme,
        monetEnabled = preferences.monetEnabled,
    ) {
        CompositionLocalProvider(
            values = arrayOf(
                LocalLambdaOnDownload provides lambdaOnDownload,
                LocalLambdaOnShare provides lambdaOnShare,
                LocalScrollToTopChannel provides viewModel.scrollToTopChannel,
                LocalWindowSizeHorizontal provides rememberWindowSizeHorizontal(),
                LocalWindowSizeVertical provides rememberWindowSizeVertical(),
                LocalBottomSheetNavigator provides rememberBottomSheetNavigator(navController = bottomNavController),
                LocalScaffoldState provides scaffoldState,
                LocalSnackbarHostState provides scaffoldState.snackbarHostState,
            ),
        ) {
            SetSystemBarsColors(Color.Transparent)

            if (viewModel.shareDialogVisible) {
                ShareDownloadDialog(
                    downloadState = viewModel.downloadState,
                    onCancelClick = viewModel::cancelShare,
                )
            }

            val lambdaOnNavigate = { route: NavRoute ->
                navController.navigate(route) {
                    popUpTo(id = navController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    restoreState = true
                    launchSingleTop = true
                }
            }

            val lambdaOnNavigateSearch: () -> Unit = {
                navController.navigate(
                    route = MainRoute.Search(tags = viewModel.currentTags)
                )
            }

            val navigationBarsVisible = when {
                currentRoute in HomeRoutesString -> true
                currentRoute == null -> true
                else -> false
            }

            Surface {
                ProductModalBottomSheetLayout(
                    sheetState = LocalBottomSheetNavigator.current.bottomSheetState,
                    sheetContent = {
                        BottomNavGraph(mainNavController = navController)
                    }
                ) {
                    if (LocalWindowSizeHorizontal.current == WindowSize.COMPACT) {
                        Scaffold(
                            bottomBar = {
                                AnimatedVisibility(
                                    visible = navigationBarsVisible,
                                    enter = slideInVertically(initialOffsetY = { it }),
                                    exit = slideOutVertically(targetOffsetY = { it }),
                                ) {
                                    MainBottomNavigationBar(
                                        currentRoute = currentRoute,
                                        previousRoute = previousRoute,
                                        onNavigate = lambdaOnNavigate,
                                        onNavigateSearch = lambdaOnNavigateSearch,
                                        onRequestScrollToTop = lambdaOnRequestScrollToTop,
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            content = { innerPadding ->
                                CompositionLocalProvider(LocalMainScaffoldPadding provides innerPadding) {
                                    MainNavGraph(navController = navController)
                                }
                            },
                        )
                    } else {
                        RailScaffold(
                            startBar = {
                                AnimatedVisibility(
                                    visible = navigationBarsVisible,
                                    enter = slideInHorizontally(initialOffsetX = { -it }),
                                    exit = slideOutHorizontally(targetOffsetX = { -it }),
                                ) {
                                    DimensionSubcomposeLayout {
                                        LaunchedEffect(key1 = size.width) {
                                            viewModel.navigationRailPadding = size.width
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .background(MaterialTheme.colors.background.backgroundElevation())
                                        ) {
                                            Spacer(
                                                modifier = Modifier
                                                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Start))
                                                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Start)),
                                            )

                                            MainNavigationRail(
                                                currentRoute = currentRoute,
                                                previousRoute = previousRoute,
                                                onNavigate = lambdaOnNavigate,
                                                onNavigateSearch = lambdaOnNavigateSearch,
                                                onRequestScrollToTop = lambdaOnRequestScrollToTop,
                                            )

                                            Divider(
                                                modifier = Modifier
                                                    .width(1.dp)
                                                    .fillMaxHeight()
                                                    .statusBarsPadding(),
                                            )
                                        }
                                    }
                                }
                            },
                            content = { innerPadding ->
                                CompositionLocalProvider(
                                    values = arrayOf(
                                        LocalMainScaffoldPadding provides innerPadding.copy(start = viewModel.navigationRailPadding),
                                    ),
                                ) {
                                    Box {
                                        Spacer(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(viewModel.navigationRailPadding)
                                                .background(Color.Black)
                                                .align(Alignment.CenterStart),
                                        )

                                        MainNavGraph(navController = navController)

                                        if (currentRoute !in listOf(MainRoute.Image::class.route, HomeRoute.Posts::class.route)) {
                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .windowInsetsStartWidth(WindowInsets.displayCutout)
                                                    .background(Color.Black)
                                                    .align(Alignment.CenterStart),
                                            )

                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .windowInsetsEndWidth(WindowInsets.displayCutout)
                                                    .background(Color.Black)
                                                    .align(Alignment.CenterEnd),
                                            )
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
