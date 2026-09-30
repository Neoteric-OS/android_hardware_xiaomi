/*
 * SPDX-FileCopyrightText: Neoteric OS
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import com.android.settingslib.spa.framework.theme.SettingsDimension
import com.android.settingslib.spa.framework.theme.SettingsShape
import com.android.settingslib.spa.framework.theme.SettingsSpace

/** A row inside a [Category] that matches the SPA preference surface. */
@Composable
internal fun CategoryItem(
    enabled: Boolean,
    shape: Shape = SettingsShape.CornerExtraSmall2,
    contentPadding: PaddingValues =
        PaddingValues(
            start = SettingsDimension.itemPaddingStart,
            end = SettingsDimension.itemPaddingEnd,
            top = SettingsDimension.itemPaddingVertical,
            bottom = SettingsDimension.itemPaddingVertical,
        ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = shape,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.alpha(if (enabled) 1f else 0.38f).padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(SettingsSpace.extraSmall6),
            content = content,
        )
    }
}

@Composable
internal fun ItemTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}
