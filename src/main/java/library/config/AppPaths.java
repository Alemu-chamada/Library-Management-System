package library.config;

import java.io.File;

/**
 * Resolves the persistent data directory for CSV storage.
 */
public final class AppPaths {

    private AppPaths() {}

    /**
     * In development: may resolve to a relative {@code data/} folder when {@code app.dataDir} is set.
     * When packaged with jpackage: typically {@code $APPDIR/data} via {@code -Dapp.dataDir}.
     * Otherwise uses OS-specific persistent folders under APPDATA or the user home directory.
     */
    public static String getDataDirectory() {
        String prop = System.getProperty("app.dataDir");
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return appData + File.separator + "SmartLibrary" + File.separator + "data";
            }
        }
        return System.getProperty("user.home") + File.separator + ".smartlibrary" + File.separator + "data";
    }

    /** Log directory next to application data (e.g. %APPDATA%/SmartLibrary/logs). */
    public static String getLogDirectory() {
        String dataDir = getDataDirectory();
        File parent = new File(dataDir).getParentFile();
        if (parent != null) {
            return parent.getAbsolutePath() + File.separator + "logs";
        }
        return System.getProperty("user.home") + File.separator + ".smartlibrary" + File.separator + "logs";
    }
}
