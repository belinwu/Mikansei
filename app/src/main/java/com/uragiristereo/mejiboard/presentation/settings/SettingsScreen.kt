package com.uragiristereo.mejiboard.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.uragiristereo.mejiboard.presentation.common.composable.product.ProductSetSystemBarsColor
import com.uragiristereo.mejiboard.presentation.settings.preference.BottomSheetPreference
import com.uragiristereo.mejiboard.presentation.settings.preference.LocalIconPadding
import com.uragiristereo.mejiboard.presentation.settings.preference.rememberBottomSheetPreferenceState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val bottomSheetPreferenceState = rememberBottomSheetPreferenceState(
        onItemSelected = viewModel::setBottomSheetPreferenceState,
    )

    BackHandler(
        enabled = bottomSheetPreferenceState.sheetState.isVisible,
        onBack = {
            scope.launch {
                bottomSheetPreferenceState.sheetState.hide()
            }
        },
    )

    ProductSetSystemBarsColor(
        navigationBarColor = when (bottomSheetPreferenceState.sheetState.targetValue) {
            ModalBottomSheetValue.Expanded -> Color.Transparent
            else -> MaterialTheme.colors.background.copy(alpha = 0.4f)
        },
    )

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                onNavigateBack = onNavigateBack,
                onMoreClick = { /*TODO*/ },
            )
        },
        modifier = modifier.statusBarsPadding(),
    ) { innerPadding ->
        CompositionLocalProvider(
            values = arrayOf(
                LocalIconPadding provides true,
            ),
            content = {
                SettingsColumn(
                    bottomSheetPreferenceState = bottomSheetPreferenceState,
                    contentPadding = innerPadding,
                    viewModel = viewModel,
                )
            },
        )
    }

    BottomSheetPreference(bottomSheetPreferenceState)
}
