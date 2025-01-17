package nextstep.subway.line.application.exception;

public class InvalidDistanceException extends RuntimeException {
    public InvalidDistanceException() {
        super();
    }

    public InvalidDistanceException(String message) {
        super(message);
    }

    public static InvalidDistanceException error(String message) {
        return new InvalidDistanceException(message);
    }
}
