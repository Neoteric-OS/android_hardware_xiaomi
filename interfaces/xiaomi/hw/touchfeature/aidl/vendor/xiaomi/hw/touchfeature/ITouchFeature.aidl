/*
 * Copyright (C) 2026 Neoteric OS
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package vendor.xiaomi.hw.touchfeature;

/*
 * Declaration order is the binder transaction order, and this interface has to
 * line up with the stock odm HAL (vendor.xiaomi.hw.touchfeature-service), which
 * names its methods differently:
 *
 *   1 getModeCurValue        6 getTouchEvent           11 getModeWhiteList
 *   2 getModeDefaultValue    7 modeReset               12 registerCallback
 *   3 getModeMaxValue        8 setModeLongValue        13 unregisterCallback
 *   4 getModeMinValue        9 setModeValue            14 setModePackageName
 *   5 getModeValue          10 getModeCurValueString
 *
 * Only setEdgeMode (8) and setTouchMode (9) are actually used, so the rest are
 * kept as-is to avoid reshuffling transaction ids.
 */
@VintfStability
interface ITouchFeature {
  int getModeCurValueString(int touchId, int mode);
  int getModeValues(int touchId, int mode);
  int getTouchModeCurValue(int touchId, int mode);
  int getTouchModeDefValue(int touchId, int mode);
  int getTouchModeMaxValue(int touchId, int mode);
  int getTouchModeMinValue(int touchId, int mode);
  boolean resetTouchMode(int touchId, int mode);
  // setModeLongValue(touchId, mode, length, value) -> 0 on success
  int setEdgeMode(int touchId, int mode, int length, in int[] value);
  void setTouchMode(int touchId, int mode, int value);
}
