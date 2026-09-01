package vendor.xiaomi.hw.touchfeature;

@VintfStability
interface ITouchFeature {
  int getModeCurValueString(int touchId, int mode);
  int getModeValues(int touchId, int mode);
  int getTouchModeCurValue(int touchId, int mode);
  int getTouchModeDefValue(int touchId, int mode);
  int getTouchModeMaxValue(int touchId, int mode);
  int getTouchModeMinValue(int touchId, int mode);
  boolean resetTouchMode(int touchId, int mode);
  int setEdgeMode(int touchId, int mode, int length, in int[] value);
  void setTouchMode(int touchId, int mode, int value);
}
