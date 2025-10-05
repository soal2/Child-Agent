package com.example.child_agent_back.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.example.child_agent_back.model.ResponseResult;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理文件大小超出限制的异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseResult<?>> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ResponseResult.fail(413, "文件大小超出限制"));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseResult<?>> handleGlobalException(Exception exc) {
        // 在实际项目中，应该记录异常日志
        // log.error("系统异常：", exc);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseResult.fail(500, "系统内部错误：" + exc.getMessage()));
    }
}