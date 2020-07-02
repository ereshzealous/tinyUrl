package com.tetrasoft.shorturl.rest.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetrasoft.shorturl.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Eresh Gorantla on 12/Jun/2019
 **/

public class BaseRestApi {

	@Autowired
	ObjectMapper objectMapper;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	protected <I> ResponseEntity<RestResponse> inboundServiceCall(I request,
	                                                              IServiceMethod<I, ResponseEntity<RestResponse>> service)
			throws RestApiException {
		try {
			ResponseEntity<RestResponse> response = service.execute(request);
			return response;
		} catch (Exception e) {
			logError(e);
			throw processException(e);
		}
	}

	protected <I> ModelAndView modelAndViewServiceCall(I request, IServiceMethod<I, ModelAndView> service)
			throws RestApiException {
		try {
			ModelAndView response = service.execute(request);
			return response;
		} catch (Exception e) {
			logError(e);
			throw processException(e);
		}
	}


	private void logError(Exception e) {
		logger.error("Error processing Rest Request => ", e);
	}

	protected RestApiException processException(Exception e) {
		logger.debug("At: processException()");
		RestApiException restApiException = null;
		Throwable rootCause = e.getCause();
		if (rootCause instanceof ApplicationException) {
			restApiException = new RestApiException(e.getMessage(), RestApiException.createApplicationExceptionFault(
					(ApplicationException) e));
		}
		if (restApiException == null) {
			restApiException = new RestApiException(e.getMessage(), RestApiException.createUnexpectedExceptionFault(e));
		}
		return restApiException;
	}
}
