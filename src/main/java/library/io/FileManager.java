package library.io;

import library.exception.FileHandlingException;
import library.model.AuthAccount;
import library.model.Book;
import library.model.RemovedItem;
import library.model.Transaction;
import library.model.User;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-based implementation of Storage.
 * Each entity type gets its own file under data/ with a header row.
 */
public class FileManager implements Storage {

    // In development: resolves to relative "data/" folder next to the project root.
    // When packaged with jpackage: resolves to $APPDIR/data (set via -Dapp.dataDir=$APPDIR/data).
    private static final String DATA_DIR =
            System.getProperty("app.dataDir", "data");
    private static final String BOOKS_FILE = DATA_DIR + "/books.csv";
    private static final String USERS_FILE = DATA_DIR + "/users.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String REMOVED_FILE = DATA_DIR + "/removed_items.csv";
    private static final String AUTH_FILE = DATA_DIR + "/users_auth.csv";

    public FileManager() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ─── Books ──────────────────────────────────────────────────────────

    @Override
    public void saveBooks(List<Book> books) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            w.write("bookId,title,author,genre,quantity,available");
            w.newLine();
            for (Book b : books) {
                w.write(String.join(",",
                        csv(b.getBookId()),
                        csv(b.getTitle()),
                        csv(b.getAuthor()),
                        csv(b.getGenre()),
                        String.valueOf(b.getQuantity()),
                        String.valueOf(b.isAvailable())));
                w.newLine();
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not save books to " + BOOKS_FILE, e);
        }
    }

    @Override
    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (!file.exists()) return books;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (header) { header = false; if (line.startsWith("bookId")) continue; }

                String[] p = parseCsvLine(line);
                if (p.length >= 6) {
                    int quantity = parseInteger(p[4].trim(), 1);
                    books.add(new Book(p[0].trim(), p[1].trim(), p[2].trim(),
                            p[3].trim(), quantity));
                } else if (p.length == 5) {
                    int quantity = Boolean.parseBoolean(p[4].trim()) ? 1 : 0;
                    books.add(new Book(p[0].trim(), p[1].trim(), p[2].trim(),
                            p[3].trim(), quantity));
                } else if (p.length == 4) {
                    // backward compat with older book file format
                    books.add(new Book(p[0].trim(), p[1].trim(), p[2].trim(),
                            "General", 1));
                } else {
                    System.err.println("[FileManager] Skipping bad book line: " + line);
                }
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not read " + BOOKS_FILE, e);
        }
        return books;
    }

    // ─── Users ──────────────────────────────────────────────────────────

    @Override
    public void saveUsers(List<User> users) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(USERS_FILE))) {
            w.write("userId,name,email");
            w.newLine();
            for (User u : users) {
                w.write(u.getUserId() + "," + u.getName() + "," + u.getEmail());
                w.newLine();
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not save users to " + USERS_FILE, e);
        }
    }

    @Override
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (header) { header = false; if (line.startsWith("userId")) continue; }

                String[] p = line.split(",", 3);
                if (p.length >= 2) {
                    String email = (p.length == 3) ? p[2].trim() : "";
                    users.add(new User(p[0].trim(), p[1].trim(), email));
                } else {
                    System.err.println("[FileManager] Skipping bad user line: " + line);
                }
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not read " + USERS_FILE, e);
        }
        return users;
    }

    // ─── Transactions ───────────────────────────────────────────────────

    @Override
    public void saveTransactions(List<Transaction> txns) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            w.write("transactionId,bookId,bookName,userId,userName,issueDate,returnDate,status");
            w.newLine();
            for (Transaction t : txns) {
                String ret = t.getReturnDate() != null ? t.getReturnDate().toString() : "";
                w.write(String.join(",",
                        csv(t.getTransactionId()),
                        csv(t.getBookId()),
                        csv(t.getBookName()),
                        csv(t.getUserId()),
                        csv(t.getUserName()),
                        csv(t.getIssueDate().toString()),
                        csv(ret),
                        csv(t.getStatus())));
                w.newLine();
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not save transactions to " + TRANSACTIONS_FILE, e);
        }
    }

    @Override
    public List<Transaction> loadTransactions() {
        List<Transaction> txns = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return txns;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (header) { header = false; if (line.startsWith("transactionId")) continue; }

                String[] p = parseCsvLine(line);
                if (p.length >= 8) {
                    try {
                        LocalDate issued = LocalDate.parse(p[5].trim());
                        LocalDate returned = parseReturnDate(p[6]);
                        txns.add(new Transaction(p[0].trim(), p[1].trim(),
                                p[2].trim(), p[3].trim(), p[4].trim(), issued, returned));
                    } catch (DateTimeParseException ex) {
                        System.err.println("[FileManager] Bad date in txn: " + line);
                    }
                } else if (p.length == 5) {
                    try {
                        LocalDate issued = LocalDate.parse(p[3].trim());
                        LocalDate returned = parseReturnDate(p[4]);
                        txns.add(new Transaction(p[0].trim(), p[1].trim(),
                                p[2].trim(), issued, returned));
                    } catch (DateTimeParseException ex) {
                        System.err.println("[FileManager] Bad date in txn: " + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not read " + TRANSACTIONS_FILE, e);
        }
        return txns;
    }

    private LocalDate parseReturnDate(String value) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty() || v.equalsIgnoreCase("null") || v.equals("-") || v.equals("—")) {
            return null;
        }
        return LocalDate.parse(v);
    }

    private String csv(String value) {
        String v = value == null ? "" : value;
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    private int parseInteger(String value, int defaultValue) {
        try {
            return Math.max(0, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        parts.add(current.toString());
        return parts.toArray(new String[0]);
    }

    // ─── Removed Items ──────────────────────────────────────────────────

    @Override
    public void saveRemovedItems(List<RemovedItem> items) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(REMOVED_FILE))) {
            w.write("itemType,itemId,itemName,removedAt");
            w.newLine();
            for (RemovedItem ri : items) {
                w.write(ri.getItemType() + "," + ri.getItemId() + ","
                        + ri.getItemName() + "," + ri.getRemovedAt().toString());
                w.newLine();
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not save removed items to " + REMOVED_FILE, e);
        }
    }

    @Override
    public List<RemovedItem> loadRemovedItems() {
        List<RemovedItem> items = new ArrayList<>();
        File file = new File(REMOVED_FILE);
        if (!file.exists()) return items;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (header) { header = false; if (line.startsWith("itemType")) continue; }

                String[] p = line.split(",", 4);
                if (p.length >= 4) {
                    try {
                        LocalDateTime when = LocalDateTime.parse(p[3].trim());
                        items.add(new RemovedItem(p[0].trim(), p[1].trim(), p[2].trim(), when));
                    } catch (Exception ex) {
                        System.err.println("[FileManager] Skipping bad removed-item line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not read " + REMOVED_FILE, e);
        }
        return items;
    }

    @Override
    public void saveAuthAccounts(List<AuthAccount> accounts) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(AUTH_FILE))) {
            w.write("username,fullName,email,phone,role,passwordHash");
            w.newLine();
            for (AuthAccount account : accounts) {
                w.write(String.join(",",
                        csv(account.getUsername()),
                        csv(account.getFullName()),
                        csv(account.getEmail()),
                        csv(account.getPhone()),
                        csv(account.getRole()),
                        csv(account.getPasswordHash())));
                w.newLine();
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not save auth accounts to " + AUTH_FILE, e);
        }
    }

    @Override
    public List<AuthAccount> loadAuthAccounts() {
        List<AuthAccount> accounts = new ArrayList<>();
        File file = new File(AUTH_FILE);
        if (!file.exists()) return accounts;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (header) { header = false; if (line.startsWith("username")) continue; }

                String[] p = parseCsvLine(line);
                if (p.length >= 6) {
                    accounts.add(new AuthAccount(
                            p[0].trim(),
                            p[1].trim(),
                            p[2].trim(),
                            p[3].trim(),
                            p[4].trim(),
                            p[5].trim()));
                } else {
                    System.err.println("[FileManager] Skipping bad auth line: " + line);
                }
            }
        } catch (IOException e) {
            throw new FileHandlingException("Could not read " + AUTH_FILE, e);
        }
        return accounts;
    }
}
