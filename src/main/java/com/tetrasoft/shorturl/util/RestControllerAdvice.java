package com.tetrasoft.shorturl.util;

import com.tetrasoft.shorturl.exception.RestFault;
import com.tetrasoft.shorturl.rest.common.RestApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice(basePackages = "com.tetrasoft.shorturl")
@RequestMapping(produces = "application/json")
public class RestControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<RestFault> handleException(Exception e) {
        RestFault restFault = new RestFault();
        restFault.setErrorMessage("Unexpected error occurred");
        return new ResponseEntity(restFault, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RestApiException.class})
    protected ResponseEntity<RestFault> handleARestException(RestApiException e) {
        RestFault restFault = e.getFaultInfo();
        HttpStatus httpStatus = restFault.getHttpStatus() != null ? restFault.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity(restFault, null, httpStatus);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<RestFault> handleConflict(MethodArgumentNotValidException ex,
                                                       HttpServletRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors = fieldErrors.stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .collect(Collectors.toList());
        RestFault fault = new RestFault();
        if (CollectionUtils.isNotEmpty(fieldErrors)) {
            FieldError error = fieldErrors.get(0);
            String errorMessage;
            if (StringUtils.contains(error.getDefaultMessage(), "{0}")) {
                errorMessage = MessageFormat.format(error.getDefaultMessage(),
                        new Object[]{error.getRejectedValue().toString()});
            } else {
                errorMessage = error.getDefaultMessage();
            }
            fault.setFieldName(error.getField());
            fault.setErrorMessage(errorMessage);
        }

        int status = HttpStatus.BAD_REQUEST.value();
        return new ResponseEntity(fault, null, HttpStatus.valueOf(status));
    }


}
