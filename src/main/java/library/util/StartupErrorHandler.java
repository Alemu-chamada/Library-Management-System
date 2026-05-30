package library.util;

import library.config.AppPaths;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Global startup/runtime error reporting — prevents silent EXE exits.
 */
public final class StartupErrorHandler {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static volatile boolean installed;

    private StartupErrorHandler() {}

    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> handle(error));
    }

    public static void handle(Throwable error) {
        if (error == null) {
            return;
        }
        log("Unhandled startup/runtime error", error);
        showDialog(error);
    }

    public static void log(String message, Throwable error) {
        StringWriter sw = new StringWriter();
        sw.append('[').append(LocalDateTime.now().format(TS)).append("] ").append(message).append('\n');
        if (error != null) {
            error.printStackTrace(new PrintWriter(sw));
        }
        String text = sw.toString();
        System.err.print(text);

        try {
            Path logDir = Path.of(AppPaths.getLogDirectory());
            Files.createDirectories(logDir);
            Path logFile = logDir.resolve("error.log");
            Files.writeString(logFile, text + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // console output is the fallback
        }
    }

    private static void showDialog(Throwable error) {
        Runnable show = () -> JOptionPane.showMessageDialog(null,
                "SmartLibrary could not start.\n\n"
                        + error.getMessage()
                        + "\n\nDetails were written to:\n"
                        + AppPaths.getLogDirectory() + "\\error.log",
                "SmartLibrary — Startup Error",
                JOptionPane.ERROR_MESSAGE);

        if (SwingUtilities.isEventDispatchThread()) {
            show.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(show);
            } catch (Exception ignored) {
                // best effort
            }
        }
    }
}
