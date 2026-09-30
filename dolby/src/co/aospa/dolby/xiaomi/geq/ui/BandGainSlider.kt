/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.geq.ui

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalSlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.aospa.dolby.xiaomi.geq.data.BandGain

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BandGainSlider(
    bandGain: BandGain,
    onValueChangeFinished: (Int) -> Unit
) {
    // Gain is -100..100 in the backend and -10..10 dB in the UI.
    val state = remember { SliderState(value = bandGain.gain / 10f, valueRange = -10f..10f) }
    state.onValueChangeFinished = { onValueChangeFinished((state.value * 10f).toInt()) }

    // Glide to gains changed from outside, e.g. when switching presets.
    val spec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    LaunchedEffect(bandGain.gain) {
        val target = bandGain.gain / 10f
        if (state.isDragging || state.value == target) return@LaunchedEffect
        animate(state.value, target, animationSpec = spec) { value, _ -> state.value = value }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(48.dp)
    ) {
        SliderText("%.1f".format(state.value))
        VerticalSlider(
            state = state,
            reverseDirection = true,
            track = { SliderDefaults.CenteredTrack(sliderState = it) },
            modifier = Modifier
                .height(SLIDER_HEIGHT)
                .padding(vertical = 8.dp)
        )
        SliderText(
            with(bandGain.band) {
                if (this >= 1000) {
                    "${this / 1000}k"
                } else {
                    "$this"
                }
            }
        )
    }
}

@Composable
fun SliderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

internal val SLIDER_HEIGHT = 360.dp
