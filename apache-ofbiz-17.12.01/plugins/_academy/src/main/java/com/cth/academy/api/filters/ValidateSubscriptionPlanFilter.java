package com.cth.academy.api.filters;

import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Priority(2)
public class ValidateSubscriptionPlanFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("default", delegator);
        String partyId = requestContext.getHeaderString("partyId");
        if (StringUtils.isEmpty(partyId)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Failed to find academy").build());
            return;
        }
        boolean hasValidPlan = false;
        try {
            List<GenericValue> subscriptions = delegator.findByAnd("Subscription", UtilMisc.toMap("partyId", partyId), null, false);
            if (UtilValidate.isNotEmpty(subscriptions) && subscriptions.size() > 0) {
                for (GenericValue subscription : subscriptions) {
                    Timestamp fromDate = subscription.getTimestamp("fromDate");
                    Timestamp thruDate = subscription.getTimestamp("thruDate");
                    Timestamp currentTimeStamp = UtilDateTime.nowTimestamp();
                    if (fromDate.before(currentTimeStamp) && thruDate.after(currentTimeStamp)) {
                        List<GenericValue> productAttributes = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId", subscription.getString("productId")), null, false);
                        if (UtilValidate.isNotEmpty(productAttributes) && productAttributes.size() > 0) {
                            for (GenericValue productAttribute : productAttributes) {
                                requestContext.getHeaders().add(productAttribute.getString("attrName"), productAttribute.getString("attrValue"));
                            }
                            hasValidPlan = true;
                        }
                        requestContext.getHeaders().add("subscriptionId", subscription.getString("subscriptionId"));
                        requestContext.getHeaders().add("productId", subscription.getString("productId"));
                        requestContext.getHeaders().add("fromDate", String.valueOf(fromDate.getTime()));
                        requestContext.getHeaders().add("thruDate", String.valueOf(thruDate.getTime()));
                        break;
                    }
                }
            }
        } catch (GenericEntityException e) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Failed to fetch subscription " + e.getMessage()).build());
            return;
        }
        if (!hasValidPlan) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Does not have proper plan").build());
            return;
        }
    }
}
