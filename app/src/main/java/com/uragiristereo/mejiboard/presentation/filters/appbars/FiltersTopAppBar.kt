package com.uragiristereo.mejiboard.presentation.filters.appbars

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.uragiristereo.mejiboard.R
import com.uragiristereo.mejiboard.presentation.common.composable.product.ProductTopAppBar

@Composable
fun FiltersTopAppBar(
    onNavigateBack: () -> Unit,
    onSelectAll: () -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    ProductTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.filters_label))
        },
        actions = {
            IconButton(
                onClick = { dropdownExpanded = !dropdownExpanded },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.more_vert),
                        contentDescription = null,
                    )
                },
            )

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = {
                    dropdownExpanded = false
                },
                content = {
                    DropdownMenuItem(
                        onClick = {
                            dropdownExpanded = false
                            onSelectAll()
                        },
                        content = {
                            Text(text = stringResource(id = R.string.select_all))
                        },
                    )
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                    )
                },
            )
        }
    )
}
