/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.geq.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.aospa.dolby.xiaomi.R
import co.aospa.dolby.xiaomi.ui.CategoryItem
import com.android.settingslib.spa.widget.ui.Category

@Composable
fun EqualizerScreen(
    viewModel: EqualizerViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Category(title = stringResource(R.string.dolby_geq_preset)) {
            // Pill like the intelligent EQ; the buttons row brings its own 8dp side inset.
            CategoryItem(
                enabled = true,
                shape = CircleShape,
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                PresetSelector(viewModel = viewModel)
            }
        }
        Category(title = stringResource(R.string.dolby_geq_slider_label_gain)) {
            CategoryItem(enabled = true) {
                EqualizerBands(viewModel = viewModel)
            }
        }
        PresetActions(viewModel = viewModel)
    }
}
