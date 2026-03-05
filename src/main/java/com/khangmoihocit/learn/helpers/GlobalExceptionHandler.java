package com.khangmoihocit.learn.helpers;

import com.khangmoihocit.learn.Resources.ApiResource;
import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.Resources.MessageResource;
import com.khangmoihocit.learn.modules.users.resources.LoginResource;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j(topic = "EXCEPTION GLOBAL")
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<MessageResource> handlingRuntimeException(RuntimeException exception) {
        log.error("Exception: ", exception);
        MessageResource messageResource = MessageResource.builder().message("Uncategories error").build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messageResource);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResource> handlingAppException(AppException exception){
        ApiResource response = ApiResource.error("ERROR", exception.getMessage(), HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Có vấn đề xảy ra trong quá trình xử lý dữ liệu.");
        response.put("status", HttpStatus.UNPROCESSABLE_ENTITY);
        response.put("timestamp", LocalDateTime.now());
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResource> handlingDataIntegrityViolationException(DataIntegrityViolationException exception){
        ApiResource response = ApiResource.error("DATA_CONFLICT", "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc hệ thống.", HttpStatus.BAD_REQUEST);
        log.error("DataIntegrityViolationException: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
