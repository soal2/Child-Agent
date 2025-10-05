package com.example.child_agent_back.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    private int code;
    private String message;
    private T data;

    // 成功响应的静态方法
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "success", data);
    }

    // 失败响应的静态方法
    public static <T> ResponseResult<T> fail(int code, String message) {
        return new ResponseResult<>(code, message, null);
    }
}