package sparta.drawmydaily_backend.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sparta.drawmydaily_backend.controller.response.ResponseDto;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDto<?> handleValidationExceptions(MethodArgumentNotValidException exception){
        String errorMessage = exception.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        return ResponseDto.fail("BAD_REQUEST", errorMessage);
    } //예외처리, request가 잘못들어오면 출력
}
