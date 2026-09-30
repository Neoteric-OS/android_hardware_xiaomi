/*
 * Copyright (C) 2023-24 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import co.aospa.dolby.xiaomi.geq.EqualizerActivity
import co.aospa.dolby.xiaomi.ui.DolbyScreen
import co.aospa.dolby.xiaomi.ui.DolbyViewModel
import com.android.settingslib.spa.framework.theme.SettingsTheme
import com.android.settingslib.spa.widget.scaffold.RegularScaffold

class DolbyActivity : ComponentActivity() {

    private val viewModel: DolbyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsTheme {
                RegularScaffold(title = stringResource(R.string.dolby_title)) {
                    DolbyScreen(
                        viewModel = viewModel,
                        onOpenEqualizer = {
                            startActivity(Intent(this, EqualizerActivity::class.java))
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
