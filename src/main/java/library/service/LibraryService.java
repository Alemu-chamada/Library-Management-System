package library.service;

import library.exception.*;
import library.model.AuthAccount;
import library.model.Book;
import library.model.RemovedItem;
import library.model.Transaction;
import library.model.User;

import java.util.List;

/**
 * Core business logic contract for the library system.
 * Any GUI or CLI client interacts with the library through this.
 */
public interface LibraryService {

    void addBook(Book book) throws InvalidInputException;
    void removeBook(String bookId) throws BookNotFoundException, BookNotAvailableException;

    void registerUser(User user) throws InvalidInputException;
    void removeUser(String userId) throws UserNotFoundException, BookNotAvailableException;

    void issueBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException, BookNotAvailableException;

    AuthAccount registerAccount(AuthAccount account) throws InvalidInputException;
    AuthAccount authenticate(String username, String password) throws InvalidInputException;
    AuthAccount findAccountByUsername(String username);
    List<AuthAccount> getAllAuthAccounts();

    void returnBook(String bookId, String userId)
            throws BookNotFoundException, UserNotFoundException;

    List<Book> getAllBooks();
    List<User> getAllUsers();
    List<Transaction> getAllTransactions();
    List<RemovedItem> getRemovedItems();

    List<Book> searchBooks(String keyword);
    String getCurrentBorrower(String bookId);
    boolean hasActiveBooks(String userId);
    List<Transaction> getTransactionsByUser(String userId);
    List<Transaction> getTransactionsByBook(String bookId);
}
