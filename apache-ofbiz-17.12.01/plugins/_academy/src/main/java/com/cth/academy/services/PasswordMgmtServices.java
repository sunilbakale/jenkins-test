package com.cth.academy.services;

import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.common.login.LoginServices;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PasswordMgmtServices {

    public static final String module = PasswordMgmtServices.class.getName();
    public static final String resource = "SecurityextUiLabels";

    public static Map<String, Object> verifyPassword(DispatchContext ctx, Map<String, ?> context) {
        Delegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        boolean useEncryption = "true".equals(EntityUtilProperties.getPropertyValue("security", "password.encrypt", delegator));
        String currentPassword =(String)context.get("currentPassword");
        boolean passwordMatches = LoginServices.checkPassword(userLogin.getString("currentPassword"), useEncryption, (String)context.get("currentPassword"));
        List<String> errorMessageList = new LinkedList<String>();
        String errMsg;
        if (currentPassword == null || !passwordMatches) {
            errMsg = UtilProperties.getMessage(resource, "loginservices.old_password_not_correct_reenter", locale);
            errorMessageList.add(errMsg);
        }
        if (LoginServices.checkPassword(userLogin.getString("currentPassword"), useEncryption, (String)context.get("newPassword"))) {
            errMsg = UtilProperties.getMessage(resource, "loginservices.new_password_is_equal_to_old_password", locale);
            errorMessageList.add(errMsg);
        }
        if(errorMessageList.size()>0){
            return ServiceUtil.returnError(errorMessageList);
        } else {
            return ServiceUtil.returnSuccess();
        }
    }
}
