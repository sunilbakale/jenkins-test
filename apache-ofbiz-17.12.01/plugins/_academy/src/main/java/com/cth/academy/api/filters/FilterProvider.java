package com.cth.academy.api.filters;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class FilterProvider implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().isAnnotationPresent(SecuredEndpoint.class)) {
            context.register(AuthenticationFilter.class);
        }
        if (resourceInfo.getResourceMethod().isAnnotationPresent(ValidSubscriptionPlan.class)) {
            context.register(ValidateSubscriptionPlanFilter.class);
        }
        if (resourceInfo.getResourceMethod().isAnnotationPresent(ValidAcademyOperation.class)) {
            context.register(ValidateAcademyOperationFilter.class);
        }
        if (resourceInfo.getResourceMethod().isAnnotationPresent(ValidEventOperation.class)) {
            context.register(ValidateEventOperationFilter.class);
        }
    }
}