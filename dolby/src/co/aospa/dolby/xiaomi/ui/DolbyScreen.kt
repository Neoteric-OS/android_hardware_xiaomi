/*
 * SPDX-FileCopyrightText: Neoteric OS
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.ui

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.annotation.ArrayRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.aospa.dolby.xiaomi.R
import com.android.settingslib.spa.framework.theme.SettingsSpace
import com.android.settingslib.spa.widget.preference.MainSwitchPreference
import com.android.settingslib.spa.widget.preference.Preference
import com.android.settingslib.spa.widget.preference.PreferenceModel
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.ui.Category
import com.android.settingslib.spa.widget.ui.SettingsIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DolbyScreen(viewModel: DolbyViewModel, onOpenEqualizer: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val profileNames = stringArrayResource(R.array.dolby_profile_entries)
    val profileValues = intValues(R.array.dolby_profile_values)
    val headphonesHint = stringResource(R.string.dolby_connect_headphones)

    Column(Modifier.fillMaxWidth()) {
        MainSwitchPreference(
            switchModel(stringResource(R.string.dolby_enable), { state.dsOn }) {
                viewModel.setDsOn(it)
            }
        )

        Category(title = stringResource(R.string.dolby_profile_title)) {
            ProfileGrid(
                names = profileNames,
                values = profileValues,
                selected = state.profile,
                enabled = state.dsOn,
                onSelect = viewModel::setProfile,
            )
        }

        Category(title = stringResource(R.string.dolby_ieq)) {
            // Pill with an 8dp visual inset so its curve is concentric with the buttons'. The
            // buttons' 48dp touch target already adds 4dp above and below their 40dp height.
            CategoryItem(
                enabled = state.profileEnabled,
                shape = CircleShape,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            ) {
                ConnectedChoice(
                    labels = stringArrayResource(R.array.dolby_ieq_entries),
                    values = intValues(R.array.dolby_ieq_values),
                    selected = state.ieq,
                    enabled = state.profileEnabled,
                    onSelect = viewModel::setIeq,
                )
            }
        }

        Category(title = stringResource(R.string.dolby_category_settings)) {
            Preference(
                object : PreferenceModel {
                    override val title = stringResource(R.string.dolby_preset)
                    override val summary = { state.presetName }
                    override val icon =
                        @Composable { SettingsIcon(imageVector = Icons.Rounded.GraphicEq) }
                    override val enabled = { state.profileEnabled }
                    override val onClick = onOpenEqualizer
                }
            )
            SwitchPreference(
                switchModel(
                    stringResource(R.string.dolby_spk_virtualizer),
                    { state.speakerVirt },
                    enabled = { state.profileEnabled },
                ) {
                    viewModel.setSpeakerVirt(it)
                }
            )
            CategoryItem(enabled = state.profileEnabled) {
                ItemTitle(stringResource(R.string.dolby_dialogue_enhancer))
                ConnectedChoice(
                    labels = stringArrayResource(R.array.dolby_dialogue_entries),
                    values = intValues(R.array.dolby_dialogue_values),
                    selected = state.dialogue,
                    enabled = state.profileEnabled,
                    onSelect = viewModel::setDialogue,
                )
            }
            SwitchPreference(
                switchModel(
                    stringResource(R.string.dolby_volume_leveler),
                    { state.volumeLeveler },
                    enabled = { state.profileEnabled },
                ) {
                    viewModel.setVolumeLeveler(it)
                }
            )
        }

        Category(title = stringResource(R.string.dolby_category_headphones)) {
            SwitchPreference(
                switchModel(
                    stringResource(R.string.dolby_hp_virtualizer),
                    { state.headphoneVirt },
                    summary = { if (state.isOnSpeaker) headphonesHint else "" },
                    enabled = { state.headphoneEnabled },
                ) {
                    viewModel.setHeadphoneVirt(it)
                }
            )
            val stereoEnabled = state.headphoneEnabled && state.headphoneVirt
            CategoryItem(enabled = stereoEnabled) {
                ItemTitle(stringResource(R.string.dolby_stereo_widening))
                ConnectedChoice(
                    labels = stringArrayResource(R.array.dolby_stereo_entries),
                    values = intValues(R.array.dolby_stereo_values),
                    selected = state.stereo,
                    enabled = stereoEnabled,
                    onSelect = viewModel::setStereo,
                )
            }
            SwitchPreference(
                switchModel(
                    stringResource(R.string.dolby_bass_enhancer),
                    { state.bass },
                    summary = { if (state.isOnSpeaker) headphonesHint else "" },
                    enabled = { state.headphoneEnabled },
                ) {
                    viewModel.setBass(it)
                }
            )
        }

        ActionRow {
            ActionButton(
                text = stringResource(R.string.dolby_reset_profile),
                icon = Icons.Rounded.RestartAlt,
                style = ActionStyle.Tonal,
                enabled = state.profileEnabled,
                modifier = Modifier.weight(1f),
                onClick = {
                    viewModel.resetProfile()
                    val name = profileNames.getOrNull(profileValues.indexOf(state.profile))
                    Toast.makeText(
                            context,
                            context.getString(R.string.dolby_reset_profile_toast, name),
                            Toast.LENGTH_SHORT,
                        )
                        .show()
                },
            )
        }
    }
}

@Composable
private fun ProfileGrid(
    names: Array<String>,
    values: List<Int>,
    selected: Int,
    enabled: Boolean,
    onSelect: (Int) -> Unit,
) {
    // Same inset on every side as the gap between cards.
    CategoryItem(enabled = enabled, contentPadding = PaddingValues(CardGap)) {
        Column(verticalArrangement = Arrangement.spacedBy(CardGap)) {
            values.indices.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(CardGap)) {
                    row.forEach { i ->
                        ProfileCard(
                            name = names[i],
                            icon = profileIcon(values[i]),
                            selected = values[i] == selected,
                            enabled = enabled,
                            onClick = { onSelect(values[i]) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileCard(
    name: String,
    icon: ImageVector,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val corner by
        animateDpAsState(
            if (selected) 32.dp else 16.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
            label = "corner",
        )
    val container by
        animateColorAsState(
            if (selected) colors.primaryContainer else colors.surfaceContainerHigh,
            MaterialTheme.motionScheme.defaultEffectsSpec(),
            label = "container",
        )
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(corner),
        color = container,
        contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
        modifier = modifier.height(112.dp),
    ) {
        Column(
            modifier = Modifier.padding(SettingsSpace.small1),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier =
                    Modifier.size(40.dp)
                        .background(
                            color =
                                if (selected) colors.primary else colors.surfaceContainerHighest,
                            shape =
                                if (selected) MaterialShapes.Cookie9Sided.toShape()
                                else CircleShape,
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) colors.onPrimary else colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private val CardGap = 8.dp

@Composable
private fun intValues(@ArrayRes id: Int) = stringArrayResource(id).map { it.toInt() }

private fun profileIcon(profile: Int) =
    when (profile) {
        1 -> Icons.Rounded.Movie
        2 -> Icons.Rounded.MusicNote
        8 -> Icons.Rounded.RecordVoiceOver
        else -> Icons.Rounded.AutoAwesome
    }

private fun switchModel(
    title: String,
    checked: () -> Boolean,
    summary: () -> String = { "" },
    enabled: () -> Boolean = { true },
    onChange: (Boolean) -> Unit,
) =
    object : SwitchPreferenceModel {
        override val title = title
        override val summary = summary
        override val checked = checked
        override val changeable = enabled
        override val onCheckedChange = onChange
    }
