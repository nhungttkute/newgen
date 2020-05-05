package com.newgen.am.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.newgen.am.common.ErrorMessage;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final String STATUS_ERROR = "error";

	private class Validation {
		private String param;
		private String message;

		public String getParam() {
			return param;
		}

		public void setParam(String param) {
			this.param = param;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	private Map<String, Object> generateResponseBody(String status, String errMsg, List<Validation> validation) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status);
		body.put("errMsg", errMsg);
		if (validation != null) {
			body.put("validation", validation);
		}
		return body;
	}

	// error handle for @Valid
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        //Get all errors
        List<Validation> validation = new ArrayList<CustomGlobalExceptionHandler.Validation>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
        	Validation val = new Validation();
        	val.setParam(((FieldError) error).getField());
        	val.setMessage(error.getDefaultMessage());
        	validation.add(val);
        });
        
        return new ResponseEntity<>(generateResponseBody(STATUS_ERROR, "Invalid request.", validation), headers, HttpStatus.BAD_REQUEST);

    }

	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<Object> handleCustomExceptionHandler(CustomException ex) {
		return new ResponseEntity<>(generateResponseBody(STATUS_ERROR, ex.getMessage(), null), ex.getHttpStatus());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public final ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
		return new ResponseEntity<>(generateResponseBody(STATUS_ERROR, ex.getMessage(), null), HttpStatus.FORBIDDEN);
	}
}
