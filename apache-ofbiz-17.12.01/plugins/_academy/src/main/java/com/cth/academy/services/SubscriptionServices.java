package com.cth.academy.services;

import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.List;
import java.util.Map;

public class SubscriptionServices {
    public static final String module = SubscriptionServices.class.getName();
    public static Map<String,Object> canAddStudent(DispatchContext dctx, Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String planTypeId = (String) context.get("planTypeId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        if(UtilValidate.isEmpty(partyId)){
            return ServiceUtil.returnFailure("partId is missing");
        }
        int studentListSize = 0;
        try {
            Map<String,Object> studentInfoMap = dispatcher.runSync("fetchStudentsOfAcademy",
                    UtilMisc.toMap("academyId",partyId));
            Debug.logInfo("fetchStudentsOfAcademy",module);
            if(!ServiceUtil.isSuccess(studentInfoMap)){
                return ServiceUtil.returnFailure("Unable to fetch academy students");
            }
            if (UtilValidate.isNotEmpty(studentInfoMap)){
                List studentInfoList = (List) studentInfoMap.get("students");
                studentListSize = studentInfoList.size();
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Failed to fetch academy students"+e.getMessage());
        }
        int maxStudentCountVal = 0;
        try {
            GenericValue maxStudentCountGv = delegator.findOne("SystemProperty",
                    UtilMisc.toMap("systemResourceId","subscription","systemPropertyId","max.student.count"),false);
            if (UtilValidate.isNotEmpty(maxStudentCountGv)){
                maxStudentCountVal = Integer.parseInt((String) maxStudentCountGv.get("systemPropertyValue"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Failed to fetch maxStudentCount"+e.getMessage());
        }
        switch (planTypeId){
            case "Free":
            case "Premium":
            case "Basic":
                sendResp.put("hasPermission", studentListSize < maxStudentCountVal);
                break;
        }
        Debug.logInfo("canAddStudent:"+sendResp.get("hasPermission"),module);
        return sendResp;
    }
    public static Map<String,Object> canAddInvoice(DispatchContext dctx, Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String planTypeId = (String) context.get("planTypeId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        if(UtilValidate.isEmpty(partyId)){
            return ServiceUtil.returnFailure("PartyId is missing");
        }
        int invoicesSize = 0;
        Map<String,Object> invoiceInfoMap = null;
        try {
            invoiceInfoMap = dispatcher.runSync("fetchAcademyInvoice",
                    UtilMisc.toMap("academyId",partyId,"userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
            Debug.logInfo("fetchAcademyInvoice",module);
            if (!ServiceUtil.isSuccess(invoiceInfoMap)){
                return ServiceUtil.returnFailure("Unable to fetch academy invoice");
            }
            if(UtilValidate.isNotEmpty(invoiceInfoMap)){
                List  invoices = (List) invoiceInfoMap.get("invoices");
                invoicesSize = invoices.size();
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Failed to fetch academy invoice"+e.getMessage());
        }
        int maxInvoiceCountVal = 0;
        try {
            GenericValue maxInvoiceCountGv = delegator.findOne("SystemProperty",
                    UtilMisc.toMap("systemResourceId","subscription","systemPropertyId","max.invoice.count"),false);
            if(UtilValidate.isNotEmpty(maxInvoiceCountGv)){
                maxInvoiceCountVal = Integer.parseInt((String) maxInvoiceCountGv.get("systemPropertyValue"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Failed to fetch max invoice count"+e.getMessage());
        }
        switch (planTypeId){
            case "Free":
            case "Premium":
            case "Basic":
                sendResp.put("hasPermission", invoicesSize < maxInvoiceCountVal);
                break;
        }
        Debug.logInfo("canAddInvoice response"+sendResp.get("hasPermission"),module);
        return sendResp;
    }
}
