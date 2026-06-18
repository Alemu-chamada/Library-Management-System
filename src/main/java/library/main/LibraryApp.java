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
            javax.swing.SwingUtilities.invokeLater(() -> {
                LibrarySwingApp app = new LibrarySwingApp();
                app.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                app.setVisible(true);
            });
        } catch (Throwable t) {
            StartupErrorHandler.handle(t);
            System.exit(1);
        }
    }
}
