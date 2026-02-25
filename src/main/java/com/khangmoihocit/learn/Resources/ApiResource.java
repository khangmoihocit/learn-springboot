package com.khangmoihocit.learn.Resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResource<T> {
    boolean success;
    String message;
    T data;
    HttpStatus status;
    LocalDateTime timestamp;
    ErrorResource error;

    @Data
    @lombok.Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResource{
        String code;
        String message;
        String detail;
    }

    private ApiResource(){
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Builder<T> builder(){
        return new Builder<>();
    }

    public static class Builder<T>{
        private final ApiResource<T> resource;

        private Builder(){
            this.resource = new ApiResource<>();
        }

        public Builder<T> success(boolean success){
            this.resource.success = success;
            return this;
        }

        public Builder<T> message(String message){
            this.resource.message = message;
            return this;
        }

        public Builder<T> data(T data){
            this.resource.data = data
                  ;
            return this;
        }

        public Builder<T> status(HttpStatus status){
            this.resource.status = status;
            return this;
        }

        public Builder<T> error(ErrorResource error){
            this.resource.error = error;
            return this;
        }

        public ApiResource<T> build(){
            return resource;
        }
    }

    public static <T> ApiResource<T> ok(T data, String message){
        return ApiResource.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .status(HttpStatus.OK)
                .build();
    }

    public static <T> ApiResource<T> message(String message, HttpStatus status){
        return ApiResource.<T>builder()
                .success(true)
                .message(message)
                .status(status)
                .build();
    }

    public static <T> ApiResource<T> error(String code, String message, HttpStatus status){
        return ApiResource.<T>builder()
                .success(false)
                .error(ErrorResource.builder().code(code).message(message).build())
                .status(status)
                .build();
    }
}
