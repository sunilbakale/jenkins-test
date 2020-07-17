package com.cth.academy.api.filters;

import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Priority(1)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("default", delegator);

        //Get request headers
        String accessToken = requestContext.getHeaderString("accessToken");
        String partyId = null;
        String userLoginId = null;
        try {
            if (StringUtils.isEmpty(accessToken)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Empty Token, Unauthorized Access").build());
                return;
            }
            Map<String, Object> result = dispatcher.runSync("validateToken", UtilMisc.<String, Object>toMap("accessToken", accessToken));
            if (ServiceUtil.isError(result)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity(result.get("errorMessageMap")).build());
                return;
            }
            if (!ServiceUtil.isSuccess(result)) {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Unauthorized Access").build());
                return;
            }
            partyId = (String) result.get("partyId");
            userLoginId = (String) result.get("userLoginId");
        } catch (GenericServiceException e) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Unable to read academy " + e.getMessage()).build());
            return;
        }
        //set party id and email into context
        requestContext.getHeaders().add("partyId", partyId);
        requestContext.getHeaders().add("userLoginId", userLoginId);
    }
}
