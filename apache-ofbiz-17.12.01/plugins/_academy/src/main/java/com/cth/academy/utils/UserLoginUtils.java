package com.cth.academy.utils;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;

import java.util.List;

public class UserLoginUtils {

    public static final String module = UserLoginUtils.class.getName();


    public static GenericValue getSystemUserLogin(Delegator delegator) {
        GenericValue userLogin = null;
        try {
            userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding party in getPartyByPartyId", module);
        }
        return userLogin;
    }

    public static String getUserLoginIdForPartyId(Delegator delegator, String partyId) {
        String userLoginId = null;
        try {
            List<GenericValue> userLogins = EntityQuery.use(delegator).from("UserLogin").where("partyId", partyId).queryList();
            if (userLogins.size() != 0) {
                userLoginId = (String) userLogins.get(0).get("userLoginId");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return userLoginId;
    }

}
