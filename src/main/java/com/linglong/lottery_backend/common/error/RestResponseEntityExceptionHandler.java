package com.linglong.lottery_backend.common.error;

import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Description
 *
 * @author yixun.xing
 * @since 18 三月 2019
 */
@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result handleCommonException(Exception ex) {
        logger.error("公共异常处理：{}", ex);
        //GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage());
        return new Result(StatusCode.ERROR.getCode(), ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        //logger.error("400 Status Code", ex);
        logger.error(ex.getMessage());
        //final BindingResult result = ex.getBindingResult();
        //final GenericResponse bodyOfResponse = new GenericResponse(result.getAllErrors(), "Invalid" + result.getObjectName());
        Result result = new Result(StatusCode.ERROR.getCode(), ex.getMessage());
        return handleExceptionInternal(ex, result, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Value("${message.userNotFound}")
    private String userNotFound;
    // 404

    @ExceptionHandler({UserNotFoundException.class})
    public Result handleUserNotFound(final RuntimeException ex) {
        logger.error(ex.getMessage());
        return new Result(StatusCode.NOTFOUND_ERROR.getCode(), "UserNotFound");
    }

    @ExceptionHandler(NotCaptchaException.class)
    public Result captchaNotFound(RuntimeException ex) {
        logger.error(ex.getMessage());
        //GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage());
        return new Result(StatusCode.NOTFOUND_ERROR.getCode(), ex.getMessage(), 1);
    }

    @ExceptionHandler(IllegalCaptchaException.class)
    public Result captchaError(RuntimeException ex) {
        logger.error(ex.getMessage());
        //GenericResponse bodyOfResponse = new GenericResponse(ex.getMessage());
        return new Result(StatusCode.INPUT_ERROR.getCode(), ex.getMessage());
    }

    @Value("${message.emptyParameter}")
    private String emptyParameter;

    // 404
    @ExceptionHandler({IllegalArgumentException.class})
    public Result handleEmptyParameter(final RuntimeException ex, final WebRequest request) {
        logger.error(ex.getLocalizedMessage());
        //logger.error("500 Status Code", ex);
        //final GenericResponse bodyOfResponse = new GenericResponse(emptyParameter, "Parameter cannot be null");
        return new Result(StatusCode.ERROR.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MobileFormatException.class})
    public Result handleMobileFormatError(final RuntimeException ex, final WebRequest request) {
        logger.error(ex.getMessage());
        return new Result(StatusCode.ERROR.getCode(), ex.getMessage());
    }

    @ExceptionHandler({IdCardDifferencException.class})
    public Result handleIdCardError(final RuntimeException ex) {
        logger.error("请使用本身身份证号码进行绑定{}", ex.getMessage());
        return new Result(StatusCode.CHECK_ERROR.getCode(), ex.getMessage(), 2);
    }

    @ExceptionHandler({IllegalBankcardInfoException.class})
    public Result handleIllegalBankcardInfoError(final RuntimeException ex) {
        logger.error("银行账户信息失败{}", ex.getMessage());
        return new Result(StatusCode.CHECK_ERROR.getCode(), ex.getMessage(), 4);
    }

    @ExceptionHandler({IllegalIdCardException.class})
    public Result handleIllegalIdCardError(final RuntimeException ex) {
        logger.error("身份证号有误{}", ex.getMessage());
        return new Result(StatusCode.CHECK_ERROR.getCode(), ex.getMessage(), 3);
    }

    @ExceptionHandler({RepetitionBindingException.class})
    public Result handleRepetitionError(final RuntimeException ex) {
        logger.error("请不要重复绑定卡号{}", ex.getMessage());
        return new Result(StatusCode.CHECK_ERROR.getCode(), ex.getMessage(), 1);
    }

    @ExceptionHandler({LoginException.class})
    public Result handleLoginError(final RuntimeException ex) {
        logger.error(ex.getMessage());
        return new Result(StatusCode.LOGIN_ERROR.getCode(), ex.getMessage());
    }

    @ExceptionHandler({CashierException.class})
    public Result handleCashierError(final RuntimeException ex){
        return new Result(StatusCode.ERROR.getCode(),ex.getMessage());
    }
}
