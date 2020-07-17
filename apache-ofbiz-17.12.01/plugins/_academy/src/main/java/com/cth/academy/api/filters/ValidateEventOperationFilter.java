package com.cth.academy.api.filters;

import com.cth.academy.model.EventVO;
import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Priority(3)
public class ValidateEventOperationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("default", delegator);
        String academyId = requestContext.getHeaderString("partyId");
        String maxEvents = requestContext.getHeaderString("maxEvents");
        boolean validPlan = false;
        if (StringUtils.isEmpty(academyId) || StringUtils.isEmpty(maxEvents)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Failed to find academy").build());
            return;
        }
        int maxEventsInt = Integer.parseInt(maxEvents);
        if (maxEventsInt == -1) {
            return;
        }
        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyId", academyId));
        EntityCondition createdCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getTimestamp(requestContext.getHeaderString("fromDate"))),
                EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getTimestamp(requestContext.getHeaderString("thruDate")))),
                EntityOperator.AND);
        condList.add(createdCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);
        try {
            List<GenericValue> eventRoles = delegator.findList("EventRole", condition, null, null, null, false);
            if (null == eventRoles || eventRoles.size() < maxEventsInt) {
                return;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem finding events.");
        }
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("As per plan, you have already reached limits").build());
        return;
    }
}