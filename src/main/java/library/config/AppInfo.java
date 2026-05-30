package library.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central application metadata loaded from {@code application.properties}.
 */
public final class AppInfo {

    private static final Properties PROPS = load();

    public static final String NAME = prop("app.name", "SmartLibrary");
    public static final String VERSION = prop("app.version", "1.0.0");
    public static final String VENDOR = prop("app.vendor", "SmartLibrary");
    public static final String VERSION_LABEL = "v" + VERSION;

    private AppInfo() {}

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = AppInfo.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException ignored) {
            // use defaults below
        }
        return p;
    }

    private static String prop(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue).trim();
    }
}
