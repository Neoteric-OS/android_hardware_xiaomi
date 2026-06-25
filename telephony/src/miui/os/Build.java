package miui.os;

public class Build {
    public static String DEVICE = get("ro.product.mod_device", android.os.Build.DEVICE);
    public static boolean IS_GLOBAL_BUILD = isGlobalBuild();
    public static boolean IS_INTERNATIONAL_BUILD = IS_GLOBAL_BUILD;
    public static boolean IS_ALPHA_BUILD = false;
    public static boolean IS_TABLET = false;
    public static boolean IS_MIUI_LITE_VERSION = false;
    public static boolean IS_STABLE_VERSION = isStableVersion();
    public static boolean IS_DEVELOPMENT_VERSION = !IS_STABLE_VERSION;

    private static boolean isGlobalBuild() {
        String region = get("ro.miui.build.region", "");
        String modDevice = get("ro.product.mod_device", DEVICE);

        return "global".equalsIgnoreCase(region)
                || modDevice.toLowerCase().contains("global")
                || modDevice.toLowerCase().endsWith("_eea");
    }

    private static boolean isStableVersion() {
        String incremental = get("ro.build.version.incremental", "");

        return incremental.startsWith("OS") || incremental.startsWith("V");
    }

    private static String get(String key, String defaultValue) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            return (String) systemProperties
                    .getMethod("get", String.class, String.class)
                    .invoke(null, key, defaultValue);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return defaultValue;
        }
    }
}
