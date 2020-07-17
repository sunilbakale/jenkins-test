
package com.cth.academy.api.mappers;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    private static final long serialVersionUID = 1L;

    @Override
    public Response toResponse(Throwable exception) {
        String message = exception.getMessage();
        if (UtilValidate.isEmpty(message))
            message = "Something bad happened. Please try again !!";
        Map<String,String> errorInfo = UtilMisc.toMap("message", message);
        exception.printStackTrace();
        return Response.status(500).entity(errorInfo).type("application/json").build();
    }
}