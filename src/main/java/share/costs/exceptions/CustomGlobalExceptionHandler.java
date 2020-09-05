package share.costs.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(HttpBadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Error> HttpBadRequestException(final HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.BAD_REQUEST.value());
    return ResponseEntity.ok(new Error().setErrorName("Custom Error").setErrorDescription("Custom description"));
  }

  @ExceptionHandler(HttpUnauthorizedException.class)
  public void httpUnauthorizedException(final HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.UNAUTHORIZED.value());
  }

  @ExceptionHandler(HttpForbiddenException.class)
  public void httpForbiddenException(final HttpServletResponse response) throws IOException {
    response.sendError(HttpStatus.FORBIDDEN.value());
  }

}
