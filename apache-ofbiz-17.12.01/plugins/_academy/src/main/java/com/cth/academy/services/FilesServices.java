package com.cth.academy.services;

import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class FilesServices {
    public static final String module = FilesServices.class.getName();

    public static Map<String, Object> deleteFileDetails(DispatchContext dispatchContext, Map<String, Object> context) {

        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Delegator delegator = localDispatcher.getDelegator();
        Map<String, Object> sendResponse = ServiceUtil.returnSuccess();

        String fileId = (String) context.get("fileId");

        try {
            GenericValue file = delegator.findOne("FileUpload", UtilMisc.toMap("fileId",fileId), false);
            file.remove();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to delete file Details " + e.getMessage());
        }
        return sendResponse;
    }
}
