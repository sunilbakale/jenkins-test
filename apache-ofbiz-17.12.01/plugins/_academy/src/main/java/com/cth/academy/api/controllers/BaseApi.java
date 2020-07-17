package com.cth.academy.api.controllers;


import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.service.GenericDispatcherFactory;
import org.apache.ofbiz.service.LocalDispatcher;

/**
 * Base Interface which gives us delegator & dispatcher objects
 * All API Classes should implement this
 */
public interface BaseApi {
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = new GenericDispatcherFactory().createLocalDispatcher("default",delegator);

        /** Other Utility methods */
        default GenericValue getUserLogin(Delegator delegator, String userLoginId) {
                GenericValue userLogin = null;
                try {
                        userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
                } catch (GenericEntityException e) {
                        Debug.logError(e, "Error Getting User Login for: " + userLoginId, "");
                }
                return userLogin;
        }
}