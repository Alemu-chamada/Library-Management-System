package library.gui.controller;

import library.exception.*;
import library.io.FileManager;
import library.io.Storage;
import library.model.*;
import library.service.LibraryService;
import library.service.impl.LibraryServiceImpl;

import java.util.List;

/**
 * Thin bridge between the GUI and the service layer.
 * Keeps view code clean by handling the wiring here.
 */
public class LibraryController {

    private final LibraryService service;

    public LibraryController() {
        Storage storage = new FileManager();
        this.service = new LibraryServiceImpl(storage);
    }

    public List<Book> getAllBooks() { return service.getAllBooks(); }
    public List<User> getAllUsers() { return service.getAllUsers(); }
    public List<Transaction> getAllTransactions() { return service.getAllTransactions(); }
    public List<RemovedItem> getRemovedItems() { return service.getRemovedItems(); }

    public void addBook(Book book) throws InvalidInputException {
        service.addBook(book);
    }

    public void removeBook(String bookId) throws BookNotFoundException, BookNotAvailableException {
        service.removeBook(bookId);
    }

    public void registerUser(User user) throws InvalidInputException {
        service.registerUser(user);
    }

    public void removeUser(String userId) throws UserNotFoundException, BookNotAvailableException {
        service.removeUser(userId);
    }

    public void issueBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException, BookNotAvailableException {
        service.issueBook(bookId, userId);
    }

    public void returnBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException {
        service.returnBook(bookId, userId);
    }

    public List<Book> searchBooks(String keyword) { return service.searchBooks(keyword); }
    public String getCurrentBorrower(String bookId) { return service.getCurrentBorrower(bookId); }
    public List<Transaction> getTransactionsByUser(String userId) { return service.getTransactionsByUser(userId); }
    public List<Transaction> getTransactionsByBook(String bookId) { return service.getTransactionsByBook(bookId); }

    public boolean hasActiveBooks(String userId) {
        return service.hasActiveBooks(userId);
    }

    public AuthAccount registerAccount(AuthAccount account) throws InvalidInputException {
        return service.registerAccount(account);
    }

    public AuthAccount authenticate(String username, String password) throws InvalidInputException {
        return service.authenticate(username, password);
    }

    public AuthAccount findAccountByUsername(String username) {
        return service.findAccountByUsername(username);
    }

    public List<AuthAccount> getAllAuthAccounts() {
        return service.getAllAuthAccounts();
    }
}
