/*
 * SPDX-FileCopyrightText: Neoteric OS
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.settingslib.spa.framework.theme.SettingsSpace

/** Single-select connected button group, the expressive replacement for segmented buttons. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ConnectedChoice(
    labels: Array<String>,
    values: List<Int>,
    selected: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        values.forEachIndexed { i, value ->
            ToggleButton(
                checked = value == selected,
                onCheckedChange = { onSelect(value) },
                enabled = enabled,
                shapes =
                    when (i) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        values.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
            ) {
                Text(
                    text = labels[i],
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}

internal enum class ActionStyle { Filled, Tonal, Outlined }

/** Medium expressive button for page level actions, laid out as in the M3 medium button spec. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    style: ActionStyle,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val size = ButtonDefaults.MediumContainerHeight
    val shapes = ButtonDefaults.shapesFor(size)
    val padding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true)
    val content: @Composable RowScope.() -> Unit = {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
        Text(
            text = text,
            style = ButtonDefaults.textStyleFor(size),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    val sized = modifier.heightIn(min = size)
    when (style) {
        ActionStyle.Filled ->
            Button(onClick, shapes, sized, enabled, contentPadding = padding, content = content)
        ActionStyle.Tonal ->
            FilledTonalButton(
                onClick,
                shapes,
                sized,
                enabled,
                contentPadding = padding,
                content = content,
            )
        ActionStyle.Outlined ->
            OutlinedButton(
                onClick,
                shapes,
                sized,
                enabled,
                contentPadding = padding,
                content = content,
            )
    }
}

/** Row of page level actions below the last category, with the category side inset. */
@Composable
internal fun ActionRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = SettingsSpace.small1, vertical = SettingsSpace.small4),
        horizontalArrangement = Arrangement.spacedBy(SettingsSpace.extraSmall4),
        content = content,
    )
}
