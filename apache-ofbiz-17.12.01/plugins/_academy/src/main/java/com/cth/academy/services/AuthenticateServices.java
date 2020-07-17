package com.cth.academy.services;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.*;

public class AuthenticateServices {

    private static String accessIdleSeconds = UtilProperties.getPropertyValue("academy.properties", "academy.access.idle.seconds");

    public static Map<String, Object> authenticate(DispatchContext dctx, Map<String, ? extends Object> context) {
        System.out.println("authenticate service");

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String email = (String) context.get("email");
        String password = (String) context.get("password");
        Object partyId = null;
        //Validate userName and password
        try {
            Map<String, Object> loginResult = dispatcher.runSync("userLogin",
                    UtilMisc.<String, Object>toMap("login.username", email, "login.password", password));
            if (!ServiceUtil.isSuccess(loginResult)) {
                return loginResult;
            }
            GenericValue userLoginvalue = (GenericValue) loginResult.get("userLogin");
            partyId = userLoginvalue.get("partyId");
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to Login, error: " + e.getMessage());
        }

        //Generate UUID and create record in User Login Access Token
        String accessToken = UUID.randomUUID().toString();
        try {
            GenericValue userAccessToken = delegator.makeValue("UserLoginAccessToken", UtilMisc.<String, Object>toMap(
                    "userLoginId", email,
                    "partyId", partyId,
                    "accessToken", accessToken,
                    "active", "Y",
                    "lastAccessedDate", new Timestamp(new Date().getTime())));
            delegator.create(userAccessToken);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to create User Login Authenticate, error: " + e.getMessage());
        }
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        sendResp.put("accessToken", accessToken);
        return sendResp;
    }

    public static Map<String, Object> logout(DispatchContext dctx, Map<String, ? extends Object> context) {
        System.out.println("logout service");

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String accessToken = (String) context.get("accessToken");
        try {
            int result = delegator.removeByAnd("UserLoginAccessToken", UtilMisc.toMap("accessToken", accessToken));
            if (result < 1) {
                return ServiceUtil.returnError("", null, UtilMisc.toMap("tokenNotExist", "true"), null);
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to logout, error: " + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    //move to seperate service
    public static Map<String, Object> validateToken(DispatchContext dctx, Map<String, ? extends Object> context) {
        System.out.println("validateToken service");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String accessToken = (String) context.get("accessToken");
        String partyId = null;
        String userLoginId = null;
        try {
            List<GenericValue> userLoginAccessTokens = delegator.findByAnd("UserLoginAccessToken", UtilMisc.toMap("accessToken", accessToken, "active", "Y"), null, false);
            if (UtilValidate.isNotEmpty(userLoginAccessTokens)) {
                GenericValue userLoginAccessToken = userLoginAccessTokens.get(0);
                Timestamp lastAccessedDate = userLoginAccessToken.getTimestamp("lastAccessedDate");
                if (UtilValidate.isEmpty(accessIdleSeconds)) {
                    //by default 300 seconds (5 minutes)
                    accessIdleSeconds = "300";
                }
                long currentTime = System.currentTimeMillis() - new Long(accessIdleSeconds) * 1000;
                System.out.println(new Date(currentTime));
                System.out.println(lastAccessedDate);
                if (lastAccessedDate.before(new Timestamp(currentTime))) {
                    return ServiceUtil.returnError("", null, UtilMisc.toMap("SessionTimeOut", "true"), null);
                }
                userLoginAccessToken.set("lastAccessedDate", new Timestamp(new Date().getTime()));
                delegator.store(userLoginAccessToken);
                partyId = userLoginAccessToken.getString("partyId");
                userLoginId = userLoginAccessToken.getString("userLoginId");
            } else {
                return ServiceUtil.returnError("", null, UtilMisc.toMap("invalidToken", "true"), null);
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to logout, error: " + e.getMessage());
        }
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        sendResp.put("partyId", partyId);
        sendResp.put("userLoginId", userLoginId);
        return sendResp;
    }
}
