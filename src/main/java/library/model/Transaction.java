package library.model;

import java.time.LocalDate;

/**
 * Records a single borrow/return event.
 * A transaction is "active" (book still borrowed) when returnDate is null.
 */
public class Transaction {

    private String transactionId;
    private String bookId;
    private String bookName;
    private String userId;
    private String userName;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public Transaction(String transactionId, String bookId, String userId,
                       LocalDate issueDate, LocalDate returnDate) {
        this(transactionId, bookId, "", userId, "", issueDate, returnDate);
    }

    public Transaction(String transactionId, String bookId, String bookName,
                       String userId, String userName, LocalDate issueDate,
                       LocalDate returnDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.bookName = bookName == null ? "" : bookName;
        this.userId = userId;
        this.userName = userName == null ? "" : userName;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String id) { this.transactionId = id; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName == null ? "" : bookName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName == null ? "" : userName; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    /** Quick check — is this book still out on loan? */
    public boolean isActive() {
        return returnDate == null;
    }

    public String getStatus() {
        return isActive() ? "ACTIVE" : "Returned";
    }

    @Override
    public String toString() {
        String status = isActive() ? "ACTIVE" : "returned " + returnDate;
        return String.format("Transaction[%s: book=%s, user=%s, issued=%s, %s]",
                transactionId, bookName.isBlank() ? bookId : bookName,
                userName.isBlank() ? userId : userName, issueDate, status);
    }
}
