package pl.materus.ghrepo.exception;

public class WrongHeaderException extends RuntimeException {
    public WrongHeaderException(String message) {
        super(message);
    }
}
