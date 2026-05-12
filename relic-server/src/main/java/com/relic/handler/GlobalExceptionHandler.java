package com.relic.handler;

import com.relic.constant.MessageConstant;
import com.relic.exception.BaseException;
import com.relic.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class  GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    @ExceptionHandler
    public Result exceptionHandler(DataIntegrityViolationException ex){
        log.error("数据完整性异常: {}", ex.getMessage());
        String msg = ex.getMessage();
        if (msg != null && msg.contains("cannot be null")) {
            return Result.error("请填写完整的信息，必填字段不能为空");
        }
        return Result.error("数据保存失败，请检查输入信息是否完整");
    }

    @ExceptionHandler
    public Result exceptionHandler(MaxUploadSizeExceededException ex){
        log.error("文件大小超出限制: {}", ex.getMessage());
        return Result.error("文件大小超出限制，单文件最大 10MB");
    }

    @ExceptionHandler
    public Result exceptionHandler(MultipartException ex){
        log.error("文件上传异常: {}", ex.getMessage());
        return Result.error("文件上传失败: " + ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(IllegalArgumentException ex){
        log.error("参数校验异常: {}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(RuntimeException ex){
        log.error("运行时异常: {}", ex.getMessage(), ex);
        return Result.error(ex.getMessage());
    }
}
