package com.khangmoihocit.learn.helpers;

import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.Resources.MessageResource;
import com.khangmoihocit.learn.modules.users.resources.LoginResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<MessageResource> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        MessageResource messageResource = MessageResource.builder().message("Uncategories error").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageResource);
    }

    //bắt lỗi từ @Valid
    //return 1 list error
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleValidException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error->{
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResource errorResource = ErrorResource.builder()
                .message("Có vấn đề xảy ra trong quá trình xử lý dữ liệu.")
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorResource, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
