package com.uragiristereo.mikansei.feature.home.collections.core

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.github.uragiristereo.safer.compose.navigation.animation.composable
import com.uragiristereo.mikansei.core.ui.LocalNavigationRailPadding
import com.uragiristereo.mikansei.core.ui.navigation.HomeRoute
import com.uragiristereo.mikansei.feature.home.collections.CollectionsScreen

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.collectionsRoute() {
    composable(
        route = HomeRoute.Collections,
        content = {
            CollectionsScreen(
                modifier = Modifier.padding(start = LocalNavigationRailPadding.current),
            )
        },
    )
}