/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.geq.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import co.aospa.dolby.xiaomi.R
import co.aospa.dolby.xiaomi.ui.ActionButton
import co.aospa.dolby.xiaomi.ui.ActionRow
import co.aospa.dolby.xiaomi.ui.ActionStyle

@Composable
fun PresetSelector(viewModel: EqualizerViewModel) {
    val presets by viewModel.presets.collectAsState()
    val currentPreset by viewModel.preset.collectAsState()

    // A preset not in the list (e.g. "Custom" after editing a built-in one) gets its own chip.
    val chips = if (presets.any { it.name == currentPreset.name }) {
        presets
    } else {
        listOf(currentPreset) + presets
    }

    // Chips at the intelligent EQ button height; the vertical inset is on the card.
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        items(chips, key = { it.name }) { preset ->
            val selected = preset.name == currentPreset.name
            FilterChip(
                selected = selected,
                onClick = { if (!selected) viewModel.setPreset(preset) },
                label = { Text(text = preset.name, maxLines = 1) },
                shape = CircleShape,
                modifier = Modifier.heightIn(min = 40.dp),
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    }
}

/** Page level actions shown after the gain bands. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PresetActions(viewModel: EqualizerViewModel) {
    val currentPreset by viewModel.preset.collectAsState()
    var showNewPresetDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showRenamePresetDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    ActionRow {
        ActionButton(
            text = stringResource(id = R.string.dolby_geq_new_preset),
            icon = ImageVector.vectorResource(id = R.drawable.save_as_24px),
            style = ActionStyle.Filled,
            modifier = Modifier.weight(1f),
            onClick = { showNewPresetDialog = true },
        )
        ActionButton(
            text = stringResource(id = R.string.dolby_geq_reset_gains),
            icon = ImageVector.vectorResource(id = R.drawable.reset_settings_24px),
            style = ActionStyle.Outlined,
            modifier = Modifier.weight(1f),
            onClick = {
                if (currentPreset.isUserDefined) {
                    showResetConfirmDialog = true
                } else {
                    viewModel.reset()
                }
            },
        )
        if (currentPreset.isUserDefined) {
            TooltipIconButton(
                icon = Icons.Rounded.Edit,
                text = stringResource(id = R.string.dolby_geq_rename_preset),
                onClick = { showRenamePresetDialog = true },
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            )
            TooltipIconButton(
                icon = Icons.Rounded.Delete,
                text = stringResource(id = R.string.dolby_geq_delete_preset),
                onClick = { showDeleteConfirmDialog = true },
                modifier = Modifier.size(IconButtonDefaults.mediumContainerSize()),
            )
        }
    }

    if (showNewPresetDialog) {
        PresetNameDialog(
            title = stringResource(id = R.string.dolby_geq_new_preset),
            onPresetNameSet = {
                return@PresetNameDialog viewModel.createNewPreset(name = it)
            },
            onDismissDialog = { showNewPresetDialog = false }
        )
    }
    if (showResetConfirmDialog) {
        ConfirmationDialog(
            text = stringResource(id = R.string.dolby_geq_reset_gains_prompt),
            onConfirm = { viewModel.reset() },
            onDismiss = { showResetConfirmDialog = false }
        )
    }
    if (showRenamePresetDialog) {
        PresetNameDialog(
            title = stringResource(id = R.string.dolby_geq_rename_preset),
            presetName = currentPreset.name,
            onPresetNameSet = {
                return@PresetNameDialog viewModel.renamePreset(
                    preset = currentPreset,
                    name = it
                )
            },
            onDismissDialog = { showRenamePresetDialog = false }
        )
    }
    if (showDeleteConfirmDialog) {
        ConfirmationDialog(
            text = stringResource(id = R.string.dolby_geq_delete_preset_prompt),
            onConfirm = { viewModel.deletePreset(currentPreset) },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}
