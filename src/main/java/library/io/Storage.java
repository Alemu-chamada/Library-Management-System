package library.io;

import library.model.AuthAccount;
import library.model.Book;
import library.model.RemovedItem;
import library.model.Transaction;
import library.model.User;

import java.util.List;

/**
 * Abstraction for data persistence.
 * Decouples the service layer from the actual storage mechanism —
 * currently backed by CSV files, but could be swapped for a database later.
 */
public interface Storage {

    void saveBooks(List<Book> books);
    List<Book> loadBooks();

    void saveUsers(List<User> users);
    List<User> loadUsers();

    void saveTransactions(List<Transaction> transactions);
    List<Transaction> loadTransactions();

    void saveRemovedItems(List<RemovedItem> items);
    List<RemovedItem> loadRemovedItems();

    void saveAuthAccounts(List<AuthAccount> accounts);
    List<AuthAccount> loadAuthAccounts();
}
