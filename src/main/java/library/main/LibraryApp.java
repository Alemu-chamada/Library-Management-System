package library.main;

import library.gui.dashboard.LibrarySwingApp;
import library.util.DataStoreInitializer;
import library.util.StartupErrorHandler;

/**
 * Application entry point — launches the desktop GUI with startup safeguards.
 */
public final class LibraryApp {

    private LibraryApp() {}

    public static void main(String[] args) {
        StartupErrorHandler.install();
        try {
            DataStoreInitializer.ensureDataFiles();
            LibrarySwingApp.launch();
        } catch (Throwable t) {
            StartupErrorHandler.handle(t);
            System.exit(1);
        }
    }
}
