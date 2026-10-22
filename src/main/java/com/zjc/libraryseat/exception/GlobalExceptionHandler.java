package com.zjc.libraryseat.exception;

import com.zjc.libraryseat.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> errorResponse(String message) {
        return ResponseEntity.status(400).body(new ErrorResponse("400", message));
    }
    //业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
       return errorResponse(ex.getMessage());
    }
    //请求异常
    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            DateTimeParseException.class}
    )
    public ResponseEntity<ErrorResponse> handleBadRequset(Exception ex) {
return  errorResponse("请求参数有误,请再检查一下吧~");
    }
    //路径不存在
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse("404", "请求的路径不存在哦~"));
    }
    //处理剩下的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex)
    {
        log.error("未预期的异常",ex);
        return ResponseEntity.status(500).body(new ErrorResponse("500","系统开小差咯,等会再来吧~"));
    }
}
