package library.main;

import library.util.StartupErrorHandler;

/**
 * JAR / jpackage entry point. Delegates to {@link LibraryApp}.
 */
public final class Main {

    private Main() {}

    public static void main(String[] args) {
        StartupErrorHandler.install();
        try {
            LibraryApp.main(args);
        } catch (Throwable t) {
            StartupErrorHandler.handle(t);
            System.exit(1);
        }
    }
}
