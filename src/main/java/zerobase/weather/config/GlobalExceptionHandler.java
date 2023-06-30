package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//전역적 예외처리(모든 Controller 단을 대상으로 하여 예외가 발생한 것을 잡아줌)
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //서버의 문제 처리.
    @ExceptionHandler({Exception.class}) //모든 Exception에 동작.
    public Exception handleAllException() {
    System.out.println("error from GlobalExceptionHandler");
    return new Exception();
    }
}
