package com.cth.academy.services;

import com.cth.academy.model.RecentActivityVO;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.*;

public class RecentActivityServices {
    public static final String module = RecentActivityServices.class.getName();

    public static Map<String, Object> createRecentActivity(DispatchContext dispatchContext, Map<String, Object> data) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Delegator delegator = localDispatcher.getDelegator();

        Map<String, Object> sendResponse = ServiceUtil.returnSuccess();

        String academyId = (String) data.get("academyId");
        String activityType = (String) data.get("activityType");
        String activityTypeId = (String) data.get("activityTypeId");
        String activityTypeInfo = (String) data.get("activityTypeInfo");
        Timestamp activityCreatedDate = (Timestamp) data.get("activityCreatedDate");
        String action = (String) data.get("action");

        String activityId = delegator.getNextSeqId("RecentActivity");
        if (UtilValidate.isNotEmpty(activityId)) {
            Map<String, Object> createRecentActivity = new HashMap<>();
            createRecentActivity.put("partyId", academyId);
            createRecentActivity.put("activityId", activityId);
            createRecentActivity.put("activityType", activityType);
            createRecentActivity.put("activityTypeId", activityTypeId);
            createRecentActivity.put("activityTypeInfo", activityTypeInfo);
            createRecentActivity.put("activityCreatedDate", activityCreatedDate);
            createRecentActivity.put("action", action);

            try {
                GenericValue recentActivityGenVal = delegator.makeValue("RecentActivity", UtilMisc.toMap(createRecentActivity));
                delegator.create(recentActivityGenVal);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to create a recent activity" + e.getMessage());
            }
            sendResponse.put("activityId", activityId);
        }
        return sendResponse;
    }

    public static Map<String, Object> getRecentActivities(DispatchContext dispatchContext, Map<String, Object> data) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Delegator delegator = localDispatcher.getDelegator();

        String academyId = (String) data.get("academyId");
        Map<String, Object> sendResponse = ServiceUtil.returnSuccess();

        List<RecentActivityVO> recentActivities = new ArrayList<>();
        try {
            List<GenericValue> recentActivitiesGenVal = EntityQuery.use(delegator).from("RecentActivity").where("partyId", academyId).queryList();
            if (UtilValidate.isNotEmpty(recentActivitiesGenVal)) {
                for (GenericValue recentActivityGenVal : recentActivitiesGenVal) {
                    RecentActivityVO recentActivity = new RecentActivityVO();
                    recentActivity.setActivityId(recentActivityGenVal.getString("activityId"));
                    recentActivity.setActivityType(recentActivityGenVal.getString("activityType"));
                    recentActivity.setActivityTypeId(recentActivityGenVal.getString("activityTypeId"));
                    recentActivity.setActivityTypeInfo(recentActivityGenVal.getString("activityTypeInfo"));
                    recentActivity.setActivityCreatedDate(recentActivityGenVal.getTimestamp("activityCreatedDate"));
                    recentActivity.setAction(recentActivityGenVal.getString("action"));
                    recentActivities.add(recentActivity);
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to get recent activities " + e.getMessage());
        }
        sendResponse.put("recentActivities", recentActivities);
        return sendResponse;
    }

    public static Map<String, Object> updateRecentActivity(DispatchContext dispatchContext, Map<String, Object> data) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Delegator delegator = localDispatcher.getDelegator();

        Map<String, Object> sendResponse = ServiceUtil.returnSuccess();

        String academyId = (String) data.get("academyId");
        String activityId = (String) data.get("activityId");
        String activityType = (String) data.get("activityType");
        String activityTypeId = (String) data.get("activityTypeId");
        Timestamp activityCreatedDate = (Timestamp) data.get("activityCreatedDate");
        String action = (String) data.get("action");

        if (!activityId.isEmpty()) {
            Map<String, Object> updateRecentActivity = new HashMap<>();
            updateRecentActivity.put("partyId", academyId);
            updateRecentActivity.put("activityId", activityId);
            updateRecentActivity.put("activityType", activityType);
            updateRecentActivity.put("activityTypeId", activityTypeId);
            updateRecentActivity.put("activityCreatedDate", activityCreatedDate);
            updateRecentActivity.put("action", action);

            try {
                GenericValue updateRecentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", activityId), false);
                if (UtilValidate.isNotEmpty(updateRecentActivityGenValue)) {
                    updateRecentActivityGenValue.putAll(updateRecentActivity);
                    updateRecentActivityGenValue.store();
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to update recent activity " + e.getMessage());
            }

        }
        return sendResponse;
    }

    public static Map<String, Object> deleteRecentActivity(DispatchContext dispatchContext, Map<String, Object> data) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Delegator delegator = localDispatcher.getDelegator();

        Map<String, Object> sendResponse = ServiceUtil.returnSuccess();

        String activityId = (String) data.get("activityId");

        try {
            GenericValue recentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", activityId), false);
            recentActivityGenValue.remove();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to delete recent activity " + e.getMessage());
        }
        return sendResponse;
    }
}