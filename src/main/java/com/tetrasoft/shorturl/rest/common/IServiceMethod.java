package com.tetrasoft.shorturl.rest.common;

/**
 * Created by @author Eresh Gorantla on 28-Mar-2018
 */

@FunctionalInterface
public interface IServiceMethod<I, O> {

	O execute(I request) throws Exception;
}


