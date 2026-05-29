package library.main;

/**
 * Official entry point for the packaged SmartLibrary application.
 * Delegates directly to LibraryApp which handles GUI / console mode dispatch.
 *
 * Main class used by jpackage: library.main.Main
 */
public class Main {

    public static void main(String[] args) {
        LibraryApp.main(args);
    }
}
