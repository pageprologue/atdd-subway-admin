package nextstep.subway.line.application.exception;

public class LineNotFoundException extends RuntimeException {
    public LineNotFoundException() {
    }

    public LineNotFoundException(String message) {
        super(message);
    }

    public static LineNotFoundException error(String message) {
        return new LineNotFoundException(message);
    }
}
