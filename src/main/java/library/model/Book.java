package library.model;

import java.util.Objects;

/**
 * Represents a single book in the library catalog.
 * Each book tracks title, author, genre, and the number of copies available.
 */
public class Book {

    private String bookId;
    private String title;
    private String author;
    private String genre;
    private int quantity;

    public Book(String bookId, String title, String author, String genre, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = Math.max(0, quantity);
    }

    // Convenience constructor when genre isn't specified
    public Book(String bookId, String title, String author, int quantity) {
        this(bookId, title, author, "General", quantity);
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }

    public boolean isAvailable() { return quantity > 0; }
    public void setAvailable(boolean available) {
        if (!available) {
            this.quantity = 0;
        } else if (this.quantity == 0) {
            this.quantity = 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book other = (Book) o;
        return bookId.equalsIgnoreCase(other.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("Book[id=%s, title='%s', author='%s', genre='%s', quantity=%d]",
                bookId, title, author, genre, quantity);
    }
}
