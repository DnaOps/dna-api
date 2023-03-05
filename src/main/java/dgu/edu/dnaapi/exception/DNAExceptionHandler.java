package dgu.edu.dnaapi.exception;

import dgu.edu.dnaapi.domain.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DNAExceptionHandler {

    @ExceptionHandler(DNACustomException.class)
    public ResponseEntity<Message> handleEmailDuplication(DNACustomException exception) {
        log.error("Handle DNACustomException", exception);
        Message message = Message.builder()
                .data(null)
                .apiStatus(new ApiStatus(exception.getErrorCode(), exception.getMessage()))
                .build();
        return new ResponseEntity<>(message, HttpStatus.valueOf(exception.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Message> handleException(Exception ex){
        log.error("handleException",ex);
        Message message = Message.builder()
                .data(null)
                .apiStatus(new ApiStatus(DnaStatusCode.INTER_SERVER_ERROR, ex.getMessage()))
                .build();
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
