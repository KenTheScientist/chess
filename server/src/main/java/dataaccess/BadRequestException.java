package dataaccess;

/**
 * Indicates that a username was already taken
 */
public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }

    public BadRequestException() {

    }
}
