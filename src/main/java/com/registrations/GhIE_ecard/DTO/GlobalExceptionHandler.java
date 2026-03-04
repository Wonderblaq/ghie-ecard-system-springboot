package com.registrations.GhIE_ecard.DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch Login Errors (Wrong password/username)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleLoginErrors(BadCredentialsException ex){
        ErrorResponse error = new ErrorResponse("Invalid Username or Password", 401);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // Catch Custom Runtime Errors (Like your "User not Found" error)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeErrors(RuntimeException ex){
        // We use ex.getMessage() to show the actual error message
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 400);
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    // Catch ALL other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalErrors(Exception ex){
        ErrorResponse error = new ErrorResponse("An unexpected error occurred on the GhIE server", 500);
        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
