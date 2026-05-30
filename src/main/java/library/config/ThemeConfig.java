package library.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Theme metadata loaded from {@code themes/theme.properties}.
 */
public final class ThemeConfig {

    private static final Properties PROPS = load();

    public static final String DEFAULT_THEME = prop("theme.default", "light");
    public static final String APP_FONT = prop("theme.font", "Segoe UI");

    private ThemeConfig() {}

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = ThemeConfig.class.getResourceAsStream("/themes/theme.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException ignored) {
            // defaults apply
        }
        return p;
    }

    private static String prop(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue).trim();
    }
}
