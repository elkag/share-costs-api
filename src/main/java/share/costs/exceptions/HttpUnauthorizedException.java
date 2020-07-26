package share.costs.exceptions;

public class HttpUnauthorizedException extends RuntimeException {

    public HttpUnauthorizedException(String message) {
        super(message);
    }
}
