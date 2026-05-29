package library.main;

import library.exception.*;
import library.io.FileManager;
import library.io.Storage;
import library.model.*;
import library.service.LibraryService;
import library.service.impl.LibraryServiceImpl;
import library.gui.LibrarySwingApp;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point — launches GUI by default, or console mode with --console flag.
 */
public class LibraryApp {

    private static LibraryService service;
    private static Scanner scanner;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--console")) {
            runConsole();
        } else {
            LibrarySwingApp.launch();
        }
    }

    private static void runConsole() {
        Storage storage = new FileManager();
        service = new LibraryServiceImpl(storage);
        scanner = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM - Console Mode");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = readInt();
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> registerUser();
                    case 3 -> issueBook();
                    case 4 -> returnBook();
                    case 5 -> viewBooks();
                    case 6 -> viewUsers();
                    case 7 -> viewTransactions();
                    case 8 -> searchBooks();
                    case 9 -> deleteBook();
                    case 10 -> deleteUser();
                    case 11 -> viewRemovedItems();
                    case 0 -> { running = false; System.out.println("\nGoodbye!"); }
                    default -> System.out.println("Pick a valid option.");
                }
            } catch (InvalidInputException e) {
                System.out.println("Bad input: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("  1. Add Book          6. View Users");
        System.out.println("  2. Register User     7. View Transactions");
        System.out.println("  3. Issue Book        8. Search Books");
        System.out.println("  4. Return Book       9. Delete Book");
        System.out.println("  5. View Books       10. Delete User");
        System.out.println("                      11. Removed Items");
        System.out.println("  0. Exit");
        System.out.print("Choice: ");
    }

    private static int readInt() throws InvalidInputException {
        String input = scanner.nextLine().trim();
        try { return Integer.parseInt(input); }
        catch (NumberFormatException e) {
            throw new InvalidInputException("'" + input + "' is not a number.");
        }
    }

    private static String prompt(String label) throws InvalidInputException {
        System.out.print("  " + label + ": ");
        String val = scanner.nextLine().trim();
        if (val.isEmpty()) throw new InvalidInputException(label + " cannot be empty.");
        return val;
    }

    private static String promptOptional(String label) {
        System.out.print("  " + label + " (optional): ");
        return scanner.nextLine().trim();
    }

    private static void addBook() {
        System.out.println("\n-- Add Book --");
        try {
            String id = prompt("Book ID");
            String title = prompt("Title");
            String author = prompt("Author");
            String genre = promptOptional("Genre");
            if (genre.isEmpty()) genre = "General";
            String qtyStr = prompt("Quantity");
            int quantity = Integer.parseInt(qtyStr);
            service.addBook(new Book(id, title, author, genre, quantity));
            System.out.println("  Added successfully.");
        } catch (InvalidInputException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void registerUser() {
        System.out.println("\n-- Register User --");
        try {
            String id = prompt("User ID");
            String name = prompt("Name");
            String email = promptOptional("Email");
            service.registerUser(new User(id, name, email));
            System.out.println("  Registered successfully.");
        } catch (InvalidInputException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void issueBook() {
        System.out.println("\n-- Issue Book --");
        try {
            service.issueBook(prompt("Book ID"), prompt("User ID"));
            System.out.println("  Book issued.");
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void returnBook() {
        System.out.println("\n-- Return Book --");
        try {
            service.returnBook(prompt("Book ID"), prompt("User ID"));
            System.out.println("  Book returned.");
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void deleteBook() {
        System.out.println("\n-- Delete Book --");
        try {
            String id = prompt("Book ID to delete");
            service.removeBook(id);
            System.out.println("  Book removed and logged to history.");
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void deleteUser() {
        System.out.println("\n-- Delete User --");
        try {
            String id = prompt("User ID to delete");
            service.removeUser(id);
            System.out.println("  User removed and logged to history.");
        } catch (Exception e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static void viewBooks() {
        System.out.println("\n-- All Books --");
        List<Book> books = service.getAllBooks();
        if (books.isEmpty()) { System.out.println("  (none)"); return; }
        System.out.printf("  %-8s %-22s %-16s %-12s %-8s%n", "ID", "Title", "Author", "Genre", "Avail");
        System.out.println("  " + "-".repeat(68));
        for (Book b : books) {
            System.out.printf("  %-8s %-22s %-16s %-12s %-8s%n",
                    b.getBookId(), trunc(b.getTitle(), 22), trunc(b.getAuthor(), 16),
                    trunc(b.getGenre(), 12), b.isAvailable() ? "Yes" : "No");
        }
    }

    private static void viewUsers() {
        System.out.println("\n-- All Users --");
        List<User> users = service.getAllUsers();
        if (users.isEmpty()) { System.out.println("  (none)"); return; }
        System.out.printf("  %-10s %-20s %-24s%n", "ID", "Name", "Email");
        System.out.println("  " + "-".repeat(56));
        for (User u : users)
            System.out.printf("  %-10s %-20s %-24s%n", u.getUserId(), u.getName(),
                    u.getEmail().isEmpty() ? "-" : u.getEmail());
    }

    private static void viewTransactions() {
        System.out.println("\n-- Transactions --");
        List<Transaction> txns = service.getAllTransactions();
        if (txns.isEmpty()) { System.out.println("  (none)"); return; }
        for (Transaction t : txns) System.out.println("  " + t);
    }

    private static void viewRemovedItems() {
        System.out.println("\n-- Removed Items History --");
        List<RemovedItem> items = service.getRemovedItems();
        if (items.isEmpty()) { System.out.println("  (none)"); return; }
        for (RemovedItem ri : items) System.out.println("  " + ri);
    }

    private static void searchBooks() {
        System.out.println("\n-- Search --");
        try {
            List<Book> results = service.searchBooks(prompt("Keyword"));
            if (results.isEmpty()) System.out.println("  No results.");
            else { System.out.println("  Found " + results.size() + ":"); results.forEach(b -> System.out.println("    " + b)); }
        } catch (InvalidInputException e) { System.out.println("  Error: " + e.getMessage()); }
    }

    private static String trunc(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
