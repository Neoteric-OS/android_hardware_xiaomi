/*
 * SPDX-FileCopyrightText: Neoteric OS
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.dolby.xiaomi.ui

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_BASS
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_DIALOGUE
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_ENABLE
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_HP_VIRTUALIZER
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_IEQ
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_PROFILE
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_SPK_VIRTUALIZER
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_STEREO
import co.aospa.dolby.xiaomi.DolbyConstants.Companion.PREF_VOLUME
import co.aospa.dolby.xiaomi.DolbyController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DolbyUiState(
    val dsOn: Boolean = false,
    val profile: Int = 0,
    val isOnSpeaker: Boolean = true,
    val presetName: String = "",
    val ieq: Int = 0,
    val speakerVirt: Boolean = false,
    val headphoneVirt: Boolean = false,
    val stereo: Int = 0,
    val dialogue: Int = 0,
    val bass: Boolean = false,
    val volumeLeveler: Boolean = false,
) {
    val profileEnabled get() = dsOn && profile != -1
    val headphoneEnabled get() = profileEnabled && !isOnSpeaker
}

class DolbyViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()
    private val controller = DolbyController.getInstance(application)
    private val audioManager = application.getSystemService(AudioManager::class.java)!!
    private val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(application)

    private val _state = MutableStateFlow(DolbyUiState())
    val state: StateFlow<DolbyUiState> = _state.asStateFlow()

    private val deviceCallback =
        object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<AudioDeviceInfo>) = refresh()

            override fun onAudioDevicesRemoved(removedDevices: Array<AudioDeviceInfo>) = refresh()
        }

    init {
        audioManager.registerAudioDeviceCallback(deviceCallback, Handler(Looper.getMainLooper()))
        refresh()
    }

    override fun onCleared() {
        audioManager.unregisterAudioDeviceCallback(deviceCallback)
    }

    fun refresh() {
        val profile = controller.profile
        val device = audioManager.getDevicesForAttributes(ATTRIBUTES_MEDIA).firstOrNull()
        _state.value =
            DolbyUiState(
                dsOn = controller.dsOn,
                profile = profile,
                isOnSpeaker = device == null || device.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
                presetName = controller.getPresetName(),
                ieq = controller.getIeqPreset(profile),
                speakerVirt = controller.getSpeakerVirtEnabled(profile),
                headphoneVirt = controller.getHeadphoneVirtEnabled(profile),
                stereo = controller.getStereoWideningAmount(profile),
                dialogue = controller.getDialogueEnhancerAmount(profile),
                bass = controller.getBassEnhancerEnabled(profile),
                volumeLeveler = controller.getVolumeLevelerEnabled(profile),
            )
    }

    fun setDsOn(on: Boolean) {
        controller.dsOn = on
        defaultPrefs.edit().putBoolean(PREF_ENABLE, on).apply()
        refresh()
    }

    fun setProfile(profile: Int) {
        controller.profile = profile
        defaultPrefs.edit().putString(PREF_PROFILE, profile.toString()).apply()
        refresh()
    }

    fun setIeq(value: Int) = update(PREF_IEQ, value) { controller.setIeqPreset(it) }

    fun setDialogue(value: Int) =
        update(PREF_DIALOGUE, value) { controller.setDialogueEnhancerAmount(it) }

    fun setStereo(value: Int) =
        update(PREF_STEREO, value) { controller.setStereoWideningAmount(it) }

    fun setSpeakerVirt(on: Boolean) =
        update(PREF_SPK_VIRTUALIZER, on) { controller.setSpeakerVirtEnabled(it) }

    fun setHeadphoneVirt(on: Boolean) =
        update(PREF_HP_VIRTUALIZER, on) { controller.setHeadphoneVirtEnabled(it) }

    fun setBass(on: Boolean) = update(PREF_BASS, on) { controller.setBassEnhancerEnabled(it) }

    fun setVolumeLeveler(on: Boolean) =
        update(PREF_VOLUME, on) { controller.setVolumeLevelerEnabled(it) }

    fun resetProfile() {
        controller.resetProfileSpecificSettings()
        refresh()
    }

    // List values are stored as strings and switches as booleans, as restoreSettings() reads them.
    private fun update(key: String, value: Int, apply: (Int) -> Unit) {
        apply(value)
        profilePrefs().edit().putString(key, value.toString()).apply()
        refresh()
    }

    private fun update(key: String, value: Boolean, apply: (Boolean) -> Unit) {
        apply(value)
        profilePrefs().edit().putBoolean(key, value).apply()
        refresh()
    }

    private fun profilePrefs() =
        context.getSharedPreferences("profile_${controller.profile}", Context.MODE_PRIVATE)

    private companion object {
        val ATTRIBUTES_MEDIA: AudioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build()
    }
}
