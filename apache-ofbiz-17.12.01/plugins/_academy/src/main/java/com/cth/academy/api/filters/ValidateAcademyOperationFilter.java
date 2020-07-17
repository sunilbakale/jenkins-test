package com.cth.academy.api.filters;

import org.apache.commons.lang.StringUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.StringUtil;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Priority(3)
public class ValidateAcademyOperationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("default", delegator);
        String academyId = requestContext.getHeaderString("partyId");
        String maxStudents = requestContext.getHeaderString("maxStudents");
        boolean validPlan = false;
        if (StringUtils.isEmpty(academyId) || StringUtils.isEmpty(maxStudents)) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("Failed to find academy").build());
            return;
        }
        int maxStudentsInt = Integer.parseInt(maxStudents);
        if(maxStudentsInt== -1){
            return;
        }

        String roleTypeIdFrom = "TEACHER";
        String roleTypeIdTo = "STUDENT";
        String partyRelationshipTypeId = "STUDENT";

        List<EntityCondition> condList = new ArrayList<>();
        condList.add(EntityCondition.makeCondition("partyIdFrom", academyId));
        condList.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
        condList.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
        condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
        EntityCondition condition = EntityCondition.makeCondition(condList);

        List<GenericValue> studentsRels = null;
        try {
            studentsRels = delegator.findList("PartyRelationship", condition, null, null, null, false);
            if(null == studentsRels || studentsRels.size() < maxStudentsInt){
                return;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem finding PartyRelationships.");
        }
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("As per plan, you have already reached limits").build());
        return;
    }
}
