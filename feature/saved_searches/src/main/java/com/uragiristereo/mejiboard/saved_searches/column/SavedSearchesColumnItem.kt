package com.uragiristereo.mejiboard.saved_searches.column

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.uragiristereo.mejiboard.core.resources.R
import com.uragiristereo.mejiboard.core.ui.extension.backgroundElevation

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SavedSearchesColumnItem(
    text: String,
    isEven: Boolean,
    selectionMode: Boolean,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val combinedInteractionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when {
                        isEven -> MaterialTheme.colors.background
                            .backgroundElevation(1.dp)

                        else -> MaterialTheme.colors.background
                    }
                )
                .background(
                    color = when {
                        selected -> MaterialTheme.colors.onBackground
                            .copy(alpha = 0.2f)
                            .compositeOver(MaterialTheme.colors.background)

                        else -> Color.Unspecified
                    }
                )
                .let {
                    when {
                        selectionMode -> it
                            .pointerInput(key1 = selected) {
                                detectTapGestures {
                                    onSelectedChange(!selected)
                                }
                            }

                        else -> it
                            .combinedClickable(
                                interactionSource = combinedInteractionSource,
                                indication = LocalIndication.current,
                                onClick = {
                                    onExpandedChange(!expanded)
                                },
                                onLongClick = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                                    onLongClick()
                                },
                            )
                    }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 620.dp)
                    .padding(
                        vertical = 4.dp,
                        horizontal = 16.dp,
                    ),
            ) {
                Icon(
                    painter = painterResource(
                        id = when {
                            selectionMode && selected -> R.drawable.check_box
                            selectionMode && !selected -> R.drawable.check_box_outline_blank
                            else -> R.drawable.search
                        },
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 32.dp),
                )

                Text(
                    text = text,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                )

                IconButton(
                    enabled = !selectionMode,
                    interactionSource = combinedInteractionSource,
                    onClick = {
                        onExpandedChange(!expanded)
                    },
                    content = {
                        Icon(
                            painter = painterResource(
                                id = when {
                                    expanded -> R.drawable.expand_less
                                    else -> R.drawable.expand_more
                                },
                            ),
                            contentDescription = null,
                        )
                    },
                )
            }
        }

        Divider()
    }
}