package com.tetrasoft.shorturl.rest.common;

import com.tetrasoft.shorturl.exception.ApplicationException;
import com.tetrasoft.shorturl.exception.RestFault;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;

/**
 * Created by Eresh Gorantla on 12/Jun/2019
 **/

public class RestApiException extends Exception {

	private RestFault fault;

	public RestApiException() {
		super();
	}

	public RestApiException(String message, RestFault wsFault) {
		super(message);
		Validate.notNull(wsFault, "Fault cannot be null.");
		this.fault = wsFault;
	}

	public RestApiException(String message, Exception e) {
		super(message);
		this.fault = createUnexpectedExceptionFault(e);
	}

	public RestFault getFaultInfo() {
		return this.fault;
	}

	public static RestFault createApplicationExceptionFault(ApplicationException ae) {
		RestFault fault = new RestFault();
		fault.setErrorMessage(ae.getMessage());
		fault.setResult("Application Error");
		fault.setHttpStatus(ae.getStatus());
		return fault;
	}

	public static RestFault createUnexpectedExceptionFault(Exception ae) {
		RestFault fault = new RestFault();
		fault.setErrorMessage("Unexpected Exception");
		fault.setResult("Application Error");
		return fault;
	}
}
