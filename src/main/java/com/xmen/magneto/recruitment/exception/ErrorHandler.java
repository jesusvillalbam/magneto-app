package com.xmen.magneto.recruitment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorDto> handleException(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        var error = new ErrorDto(ex.getMessage());
        return Mono.just(error);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorDto> handleBusinessException(RuntimeException ex) {
        log.error(ex.getMessage());
        var error = new ErrorDto(ex.getMessage());
        return Mono.just(error);
    }

    @ExceptionHandler(HumanNotMutantException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<ErrorDto> handleHumanNotMutantException(RuntimeException ex) {
        log.error(ex.getMessage());
        var error = new ErrorDto(ex.getMessage());
        return Mono.just(error);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ErrorDto> handleNotFoundException(RuntimeException ex) {
        log.error(ex.getMessage());
        var error = new ErrorDto(ex.getMessage());
        return Mono.just(error);
    }
}
