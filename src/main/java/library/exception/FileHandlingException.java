package library.exception;

// RuntimeException — wraps IOException so the service layer doesn't deal with raw I/O errors
public class FileHandlingException extends RuntimeException {
    public FileHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}

