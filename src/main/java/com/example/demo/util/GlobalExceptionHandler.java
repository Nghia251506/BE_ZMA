package com.example.demo.util;

import com.example.demo.Dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi khi không tìm thấy dữ liệu
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 2. Bắt lỗi xác thực/quyền truy cập (Cực kỳ quan trọng cho cái verify-zalo)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(401, "Thông tin xác thực không chính xác!", System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // 3. Bắt tất cả các lỗi hệ thống còn lại
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Lỗi hệ thống: " + ex.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // 4. Bắt lỗi Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
                403,
                "Bạn không có quyền hạn để thực hiện thao tác này!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
