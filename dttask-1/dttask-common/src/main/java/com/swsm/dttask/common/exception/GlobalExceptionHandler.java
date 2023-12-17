package com.swsm.dttask.common.exception;

import com.swsm.dttask.common.result.Result;
import com.swsm.dttask.common.util.Exceptions;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 统一处理异常
     */
    @ExceptionHandler({Exception.class})
    public <T> Result<T> handleException(Exception paramException) {
        log.error("请求出现异常", paramException);
        if (paramException instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) paramException;
            String key = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            return Result.buildFailure(key, null);
        } else if (paramException instanceof MethodArgumentTypeMismatchException) {
            return Result.buildFailure("error url param", null);
        } else if (paramException instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) paramException;
            return Result.buildFailure(exception.getMethod() + " not support ", null);
        } else if (paramException instanceof BindException) {
            BindException bindException = (BindException) paramException;
            List<String> resList = new ArrayList<>();
            for (ObjectError allError : bindException.getAllErrors()) {
                resList.add(allError.getDefaultMessage());
            }
            return Result.buildFailure(resList.stream().collect(Collectors.joining(";")));
        } else if (paramException instanceof BusinessException) {
            BusinessException businessException = (BusinessException) paramException;
            return Result.buildFailure(businessException.getMessage(), null);
        } else {
            return Result.buildFailure(Exceptions.getStackTraceAsString(paramException), null);
        }
    }
}
