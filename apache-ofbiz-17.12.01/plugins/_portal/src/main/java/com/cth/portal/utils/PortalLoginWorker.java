package com.cth.portal.utils;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortalLoginWorker {
    public final static String module = PortalLoginWorker.class.getName();

    public static Map<String, Object> updatePassword(HttpServletRequest request, HttpServletResponse response, Map<String, Object> inMap) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String ERROR = "error";
        Map<String, Object> resultPasswordChange = null;
        Map<String, Object> passwordValidationMsg = null;
        try {
            passwordValidationMsg = dispatcher.runSync("verifyPassword", inMap);
            if (ServiceUtil.isError(passwordValidationMsg)) {
                return passwordValidationMsg;
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            System.out.println("Password update failed ="+e);
            return passwordValidationMsg;
        }
        try {
            resultPasswordChange = dispatcher.runSync("updatePassword", inMap);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            System.out.println("Password update failed ="+e);
            return resultPasswordChange;
        }
        return resultPasswordChange;
    }


}
