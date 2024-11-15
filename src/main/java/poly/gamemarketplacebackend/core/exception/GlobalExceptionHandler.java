package poly.gamemarketplacebackend.core.exception;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import poly.gamemarketplacebackend.core.constant.ResponseObject;

import java.sql.SQLException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseObject<?>> handleCustomException(CustomException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(ex.getStatus())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseObject<?>> handleSQLException(SQLException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("SQL exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ResponseObject<?>> handleNullPointerException(NullPointerException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Null pointer exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<?>> handleException(Exception ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseObject<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Illegal argument exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseObject<?>> handleRuntimeException(RuntimeException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Runtime exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ResponseEntity<ResponseObject<?>> handleSignatureVerificationException(SignatureVerificationException ex) {
        ResponseObject<?> response = ResponseObject.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Signature verification exception: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
