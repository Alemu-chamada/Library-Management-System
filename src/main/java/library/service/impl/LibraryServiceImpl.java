package library.service.impl;

import library.exception.*;
import library.io.Storage;
import library.model.AuthAccount;
import library.model.*;
import library.service.LibraryService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Main implementation of the library business logic.
 * Keeps in-memory lists synced with the CSV-based Storage layer.
 */
public class LibraryServiceImpl implements LibraryService {

    private final Storage storage;
    private final List<Book> books;
    private final List<User> users;
    private final List<Transaction> transactions;
    private final List<RemovedItem> removedItems;
    private final List<AuthAccount> authAccounts;

    public LibraryServiceImpl(Storage storage) {
        this.storage = storage;
        this.books = storage.loadBooks();
        this.users = storage.loadUsers();
        this.transactions = storage.loadTransactions();
        this.removedItems = storage.loadRemovedItems();
        this.authAccounts = storage.loadAuthAccounts();
        normalizeTransactionData();
        reconcileBookAvailability();
    }

    // ─── Book operations ────────────────────────────────────────────────

    @Override
    public void addBook(Book book) throws InvalidInputException {
        validateNotBlank(book.getBookId(), "Book ID");
        validateNotBlank(book.getTitle(), "Title");
        validateNotBlank(book.getAuthor(), "Author");
        if (book.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be a positive number.");
        }

        if (findBookById(book.getBookId()) != null) {
            throw new InvalidInputException(
                    "A book with ID '" + book.getBookId() + "' already exists.");
        }
        books.add(book);
        storage.saveBooks(books);
    }

    @Override
    public void removeBook(String bookId) throws BookNotFoundException, BookNotAvailableException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with ID '" + bookId + "'.");
        }
        boolean hasActiveLoans = transactions.stream()
                .anyMatch(t -> t.getBookId().equalsIgnoreCase(bookId) && t.isActive());
        if (hasActiveLoans) {
            throw new BookNotAvailableException(
                    "Cannot remove '" + book.getTitle() + "' — there are still active loans.");
        }

        // Record the removal before actually deleting
        removedItems.add(new RemovedItem(
                "BOOK", book.getBookId(),
                book.getTitle(), LocalDateTime.now()));
        storage.saveRemovedItems(removedItems);

        books.remove(book);
        storage.saveBooks(books);
    }

    // ─── User operations ────────────────────────────────────────────────

    @Override
    public void registerUser(User user) throws InvalidInputException {
        validateNotBlank(user.getUserId(), "User ID");
        validateNotBlank(user.getName(), "Name");

        if (findUserById(user.getUserId()) != null) {
            throw new InvalidInputException(
                    "A user with ID '" + user.getUserId() + "' already exists.");
        }
        users.add(user);
        storage.saveUsers(users);
    }

    @Override
    public AuthAccount registerAccount(AuthAccount account) throws InvalidInputException {
        validateNotBlank(account.getUsername(), "Username");
        validateNotBlank(account.getPasswordHash(), "Password");
        validateNotBlank(account.getRole(), "Role");

        if (findAccountByUsername(account.getUsername()) != null) {
            throw new InvalidInputException(
                    "An account with username '" + account.getUsername() + "' already exists.");
        }

        String normalizedPasswordHash = hashPassword(account.getPasswordHash());
        AuthAccount stored = new AuthAccount(
                account.getUsername().trim(),
                account.getFullName(),
                account.getEmail(),
                account.getPhone(),
                account.getRole().trim(),
                normalizedPasswordHash);

        authAccounts.add(stored);
        storage.saveAuthAccounts(authAccounts);
        return stored;
    }

    @Override
    public AuthAccount authenticate(String username, String password) throws InvalidInputException {
        validateNotBlank(username, "Username");
        validateNotBlank(password, "Password");

        AuthAccount account = findAccountByUsername(username);
        if (account == null) {
            throw new InvalidInputException("Invalid username or password.");
        }

        String candidateHash = hashPassword(password);
        if (!candidateHash.equals(account.getPasswordHash())) {
            throw new InvalidInputException("Invalid username or password.");
        }

        return account;
    }

    @Override
    public AuthAccount findAccountByUsername(String username) {
        if (username == null) return null;
        for (AuthAccount account : authAccounts) {
            if (account.getUsername().equalsIgnoreCase(username.trim())) {
                return account;
            }
        }
        return null;
    }

    @Override
    public List<AuthAccount> getAllAuthAccounts() {
        return new ArrayList<>(authAccounts);
    }

    @Override
    public void removeUser(String userId) throws UserNotFoundException, BookNotAvailableException {
        User user = findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("No user found with ID '" + userId + "'.");
        }

        boolean hasActiveLoans = transactions.stream()
                .anyMatch(t -> t.getUserId().equalsIgnoreCase(userId) && t.isActive());
        if (hasActiveLoans) {
            throw new BookNotAvailableException(
                    "Cannot remove '" + user.getName() + "' — they still have books on loan.");
        }

        removedItems.add(new RemovedItem(
                "USER", user.getUserId(),
                user.getName(), LocalDateTime.now()));
        storage.saveRemovedItems(removedItems);

        users.remove(user);
        storage.saveUsers(users);
    }

    // ─── Issue / Return ─────────────────────────────────────────────────

    @Override
    public void issueBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException, BookNotAvailableException {

        Book book = findBookById(bookId);
        if (book == null) throw new BookNotFoundException("Book '" + bookId + "' not found.");

        User user = findUserById(userId);
        if (user == null) throw new UserNotFoundException("User '" + userId + "' not found.");

        if (hasActiveBooks(user.getUserId())) {
            throw new BookNotAvailableException(
                    "This user already has an active borrowed book.\n"
                    + "Please return the current book before borrowing another.");
        }

        if (book.getQuantity() <= 0) {
            throw new BookNotAvailableException(
                    "'" + book.getTitle() + "' has no available copies right now.");
        }

        book.setQuantity(book.getQuantity() - 1);
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        transactions.add(new Transaction(txnId, book.getBookId(), book.getTitle(),
                user.getUserId(), user.getName(), LocalDate.now(), null));

        storage.saveBooks(books);
        storage.saveTransactions(transactions);
    }

    @Override
    public void returnBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException {

        Book book = findBookById(bookId);
        if (book == null) throw new BookNotFoundException("Book '" + bookId + "' not found.");
        if (findUserById(userId) == null) throw new UserNotFoundException("User '" + userId + "' not found.");

        // Find the open transaction for this book+user pair
        Transaction active = null;
        for (Transaction t : transactions) {
            if (t.getBookId().equalsIgnoreCase(bookId)
                    && t.getUserId().equalsIgnoreCase(userId)
                    && t.isActive()) {
                active = t;
                break;
            }
        }

        if (active == null) {
            throw new BookNotFoundException(
                    "No active loan found for book '" + bookId + "' by user '" + userId + "'.");
        }

        active.setReturnDate(LocalDate.now());
        book.setQuantity(book.getQuantity() + 1);

        storage.saveBooks(books);
        storage.saveTransactions(transactions);
    }

    // ─── Queries ────────────────────────────────────────────────────────

    @Override
    public List<Book> getAllBooks() { return new ArrayList<>(books); }

    @Override
    public List<User> getAllUsers() { return new ArrayList<>(users); }

    @Override
    public List<Transaction> getAllTransactions() { return new ArrayList<>(transactions); }

    @Override
    public List<RemovedItem> getRemovedItems() { return new ArrayList<>(removedItems); }

    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllBooks();
        String lower = keyword.toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase().contains(lower)
                    || b.getAuthor().toLowerCase().contains(lower)
                    || b.getBookId().toLowerCase().contains(lower)) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public String getCurrentBorrower(String bookId) {
        for (Transaction t : transactions) {
            if (t.getBookId().equalsIgnoreCase(bookId) && t.isActive()) {
                User u = findUserById(t.getUserId());
                if (u != null) return u.getName();
                return t.getUserName().isBlank() ? t.getUserId() : t.getUserName();
            }
        }
        return null;
    }

    @Override
    public List<Transaction> getTransactionsByUser(String userId) {
        List<Transaction> res = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getUserId().equalsIgnoreCase(userId)) {
                res.add(t);
            }
        }
        return res;
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    @Override
    public boolean hasActiveBooks(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        return transactions.stream()
                .anyMatch(t -> t.getUserId().equalsIgnoreCase(userId) && t.isActive());
    }

    @Override
    public List<Transaction> getTransactionsByBook(String bookId) {
        List<Transaction> res = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getBookId().equalsIgnoreCase(bookId)) {
                res.add(t);
            }
        }
        return res;
    }

    private Book findBookById(String id) {
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    private User findUserById(String id) {
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    private void validateNotBlank(String value, String field) throws InvalidInputException {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(field + " cannot be empty.");
        }
    }

    private String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.trim().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Password hashing unavailable", e);
        }
    }

    private void normalizeTransactionData() {
        boolean changed = false;
        for (Transaction t : transactions) {
            Book book = findBookById(t.getBookId());
            if (book != null && t.getBookName().isBlank()) {
                t.setBookName(book.getTitle());
                changed = true;
            }
            User user = findUserById(t.getUserId());
            if (user != null && t.getUserName().isBlank()) {
                t.setUserName(user.getName());
                changed = true;
            }
        }
        if (changed) {
            storage.saveTransactions(transactions);
        }
    }

    private void reconcileBookAvailability() {
        boolean changed = false;
        for (Book book : books) {
            if (book.getQuantity() < 0) {
                book.setQuantity(0);
                changed = true;
            }
        }
        if (changed) {
            storage.saveBooks(books);
        }
    }
}
