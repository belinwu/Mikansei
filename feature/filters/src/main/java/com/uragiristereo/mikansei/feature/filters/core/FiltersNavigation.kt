package com.uragiristereo.mikansei.feature.filters.core

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.github.uragiristereo.safer.compose.navigation.compose.composable
import com.uragiristereo.mikansei.core.ui.navigation.MainRoute
import com.uragiristereo.mikansei.feature.filters.FiltersScreen

fun NavGraphBuilder.filtersRoute(
    navController: NavHostController,
) {
    composable(
        route = MainRoute.Filters,
        content = {
            FiltersScreen(
                onNavigateBack = navController::navigateUp,
            )
        },
    )
}
