package com.tetrasoft.shorturl.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Created by Eresh Gorantla on 12/Jun/2019
 **/

@JsonPropertyOrder(value = {"result", "uid", "fieldName", "errorKey", "errorMessage", "errorParameters"})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestFault {

	private String result;

	private String uid;

	private String fieldName;

	private String errorKey;

	@JsonProperty("message")
	private String errorMessage;

	private Object[] errorParameters;

	@JsonIgnore
	private HttpStatus httpStatus;

}
