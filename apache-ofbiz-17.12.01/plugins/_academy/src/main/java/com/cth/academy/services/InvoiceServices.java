package com.cth.academy.services;

import com.cth.academy.model.InvoiceVO;
import com.cth.academy.model.ItemVO;
import com.cth.academy.utils.UserLoginUtils;
import org.apache.commons.net.io.Util;
import org.apache.ofbiz.accounting.invoice.InvoiceWorker;
import org.apache.ofbiz.base.util.UtilFormatOut;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.apache.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTotal;

public class InvoiceServices {
    private static boolean recentActivityViewDone = false;
    private static final int INVOICE_ITEM_SEQUENCE_ID_DIGITS = 5; // this is the number of digits used for invoiceItemSeqId: 00001, 00002...
    public static final String resource = "AccountingUiLabels";
    public final static String module = EventServices.class.getName();
    public static Map<String, Object> createAcademyInvoice(DispatchContext dctx, Map<String, ? extends Object> inputMap){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) inputMap.get("userLogin");
        Locale locale = (Locale) inputMap.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String invoiceId = null;
        String partyIdFrom = (String) inputMap.get("academyId");
        String partyIdTo = (String) inputMap.get("toParty");
        BigDecimal discount = (BigDecimal) inputMap.get("discount");
        String invoiceTypeId = (String) inputMap.get("invoiceTypeId");
        Timestamp dueDate = (Timestamp) inputMap.get("dueDate");
        ArrayList<ItemVO> items = (ArrayList<ItemVO>) inputMap.get("items");
        String errorMsg = UtilProperties.getMessage(resource, "AccountingErrorCreatingInvoiceForReturn",UtilMisc.toMap("returnId", ""),locale);
        if (UtilValidate.isEmpty(invoiceId)) {
            Map<String, Object> createInvoiceContext = new HashMap<String, Object>();
            createInvoiceContext.put("partyId", partyIdTo);
            createInvoiceContext.put("partyIdFrom", partyIdFrom);
            createInvoiceContext.put("dueDate", dueDate);
            createInvoiceContext.put("invoiceTypeId", invoiceTypeId);
            createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
            createInvoiceContext.put("userLogin", userLogin);
            try {
                Map<String, Object> serviceResults = dispatcher.runSync("createInvoice", createInvoiceContext);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                }
                invoiceId = (String) serviceResults.get("invoiceId");

                if(UtilValidate.isNotEmpty(discount)){
                    ItemVO itemVO = new ItemVO();
                    itemVO.setDescription("Ad");
                    itemVO.setItemType("INVOICE_ADJ");
                    itemVO.setUnitPrice(discount);
                    itemVO.setQuantity(BigDecimal.ONE);
                    items.add(itemVO);
                }
                int invoiceItemSeqNum = 1;
                for (ItemVO item : items) {
                    String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, INVOICE_ITEM_SEQUENCE_ID_DIGITS);
                    Map<String, Object> createInvoiceItemContext = new HashMap<String, Object>();
                    createInvoiceItemContext.put("invoiceId", invoiceId);
                    createInvoiceItemContext.put("invoiceItemSeqId", invoiceItemSeqId);
                    createInvoiceItemContext.put("invoiceItemTypeId", item.getItemType());
                    createInvoiceItemContext.put("description", item.getDescription());
                    createInvoiceItemContext.put("quantity", item.getQuantity());
                    createInvoiceItemContext.put("amount", item.getUnitPrice());
                    createInvoiceItemContext.put("userLogin", userLogin);
                    Map<String, Object> createInvoiceItemResult = dispatcher.runSync("createInvoiceItem", createInvoiceItemContext);
                    if (ServiceUtil.isError(createInvoiceItemResult)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "AccountingErrorCreatingInvoiceItemFromOrder", locale), null, null, createInvoiceItemResult);
                    }
                    invoiceItemSeqNum++;
                }
                invoiceRecentActivity(dctx,partyIdFrom,invoiceId,"CREATED");
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }
            sendResp.put("invoiceId", invoiceId);
        }
        return sendResp;
    }

    public static Map<String, Object> updateInvoicePayment(DispatchContext dctx, Map<String, ? extends Object> inputMap) throws Exception {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) inputMap.get("userLogin");
        Locale locale = (Locale) inputMap.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String invoiceId = (String) inputMap.get("invoiceId");
        String paymentMethodTypeId = (String) inputMap.get("paymentMethodTypeId");
        BigDecimal paymentAmount = (BigDecimal) inputMap.get("amount");
        Map<String, Object> invoicepaymentList = null;
        try {
            invoicepaymentList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
           throw new Exception("Exception while updating invoice :: "+e.getMessage());
        }
        List<Map<String, Object>> invoicePaymentInfoList = (List<Map<String, Object>>) invoicepaymentList.get("invoicePaymentInfoList");
        BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfoList.get(0).get("outstandingAmount");
        if (outstandingAmount.compareTo(paymentAmount) < 0){
            throw new Exception("Outstanding Amount of invoice less than payment !!");
        }
        String payToPartyId = (String) inputMap.get("academyId");
        String paymentFromId = (String) inputMap.get("fromParty");
        String comments = (String) inputMap.get("comments");
        Map<String, Object> paymentParams = new HashMap<String, Object>();
        paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");//CUSTOMER_PAYMENT
        paymentParams.put("paymentMethodTypeId", paymentMethodTypeId);
        paymentParams.put("amount", paymentAmount);
        paymentParams.put("statusId", "PMNT_RECEIVED");
        paymentParams.put("partyIdFrom", paymentFromId);
        paymentParams.put("partyIdTo", payToPartyId);
        if (comments != null) {
            paymentParams.put("comments", comments);
        }
        paymentParams.put("userLogin", userLogin);

        try {
            sendResp = dispatcher.runSync("createPayment", paymentParams);
        } catch (GenericServiceException e) {
            throw new Exception("Exception occurred while creating payment for invoice "+invoiceId);
        }
        Map<String, Object> updateInvoiceContext = new HashMap<String, Object>();
        updateInvoiceContext.put("invoiceId", invoiceId);
        updateInvoiceContext.put("paymentId", sendResp.get("paymentId"));
        try {
            Map<String, Object> updateInvoiceResult = dispatcher.runSync("updatePaymentApplicationDef", updateInvoiceContext);
        } catch (GenericServiceException e) {
            throw new Exception("Exception occurred while updating payment details to invoice "+invoiceId);
        }
        if(outstandingAmount.compareTo(paymentAmount) == 0){
            //Update invoice status
            Map<String, Object> serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
               // return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                throw new Exception("Exception occurred while updating invoice status for invoice "+invoiceId);
            }
        }
        invoiceRecentActivity(dctx,payToPartyId,invoiceId,"UPDATED");
        return sendResp;
    }


    public static Map<String, Object> readAcademyInvoice(DispatchContext dispatchContext, Map<String, ? extends Object> inputMap) throws Exception {
        LocalDispatcher dispatcher = dispatchContext.getDispatcher();
        Delegator delegator = dispatchContext.getDelegator();
        GenericValue userLogin = UserLoginUtils.getSystemUserLogin(delegator);
        Locale locale = (Locale) inputMap.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String invoiceId = (String) inputMap.get("invoiceId");
        String payToPartyId = (String) inputMap.get("academyId");
        Map<String, Object> readInvoiceResult = null;
        try {
            readInvoiceResult = dispatcher.runSync("getInvoice", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            throw  new Exception("Exception while reading invoice data :: "+e.getMessage());
        }
        Map<String, Object> invoicepaymentList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
        invoicepaymentList.remove("responseMessage");
        readInvoiceResult.putAll(invoicepaymentList);

        List<GenericValue> activities = delegator.findAll("RecentActivity", false);
        String oldInvoiceId = null;
        String oldInvoiceAction = null;

        for (GenericValue activity : activities) {
            oldInvoiceId = (String) activity.get("activityTypeId");
            oldInvoiceAction = (String) activity.get("action");
        }
        if (invoiceId.equals(oldInvoiceId) && oldInvoiceAction.equals("VIEWED")) {
            System.out.println("should not create recent activity as it is already viewed");
        } else {
            invoiceRecentActivity(dispatchContext, payToPartyId, invoiceId, "VIEWED");
            recentActivityViewDone = true;
        }

        sendResp.put("resultMap", readInvoiceResult);
        return sendResp;
    }
    public static Map<String,Object> readAcademyInvoiceItems(DispatchContext dctx,Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = UserLoginUtils.getSystemUserLogin(delegator);
        Locale locale = (Locale) context.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String invoiceId = (String) context.get("invoiceId");
        String payToPartyId = (String) context.get("academyId");
        List<ItemVO> readInvoiceItemResult = new ArrayList<>();

        List<GenericValue> invoiceItemGvs = new ArrayList<>();

        try {
            invoiceItemGvs = EntityQuery.use(delegator).from("InvoiceItem").where("invoiceId",invoiceId).queryList();
            if (UtilValidate.isNotEmpty(invoiceItemGvs)){
                for (GenericValue invoiceItemGv : invoiceItemGvs){
                    ItemVO itemVO = new ItemVO();
                    itemVO.setDescription((String) invoiceItemGv.get("description"));
                    itemVO.setUnitPrice((BigDecimal) invoiceItemGv.get("amount"));
                    itemVO.setQuantity((BigDecimal) invoiceItemGv.get("quantity"));
                    readInvoiceItemResult.add(itemVO);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("resultMap", readInvoiceItemResult);
        return sendResp;
    }
    public static  Map<String,Object> fetchAcademyInvoice(DispatchContext dctx, Map<String, ? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale  locale = (Locale) context.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String academyId = (String) context.get("academyId");
        Timestamp startDate = (Timestamp) context.get("startDate");
        Timestamp endDate = (Timestamp) context.get("endDate");
//        ArrayList<String> studentList = (ArrayList<String>) context.get("studentList");
        String studentId = (String) context.get("studentId");
        ArrayList<String> statusList = (ArrayList<String>) context.get("statusList");
        List<InvoiceVO> invoiceList = new ArrayList<>();
        List<GenericValue> invoices = null;
        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyIdFrom", academyId));
        if(UtilValidate.isNotEmpty(studentId)){
            condList.add(EntityCondition.makeCondition("partyId",studentId));
        }
        if(UtilValidate.isNotEmpty(statusList)) {
            for (String statusId :statusList){
                if (statusId.equals("INVOICE_PAID")){
                    condList.add(EntityCondition.makeCondition("statusId",statusId));
                }else if (statusId.equals("INVOICE_IN_PROCESS")){
                    condList.add(EntityCondition.makeCondition("statusId",statusId));
                }
            }
         }
        if(UtilValidate.isNotEmpty(startDate) && UtilValidate.isNotEmpty(endDate)) {
            EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate),
                    EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate)),
                    EntityOperator.AND);
            condList.add(thruCond);
        }
        EntityCondition condition = EntityCondition.makeCondition(condList);
        try{
            invoices = EntityQuery.use(delegator).from("Invoice").where(condition).queryList();
            if(UtilValidate.isNotEmpty(invoices)){
                for (GenericValue invoiceGV : invoices) {
                    /*if (UtilValidate.isNotEmpty(studentList)) {
                        for (String partyId : studentList) {
                            if (partyId.equals(invoiceGV.getString("partyId"))) {
                                InvoiceVO invoiceVO = new InvoiceVO();
                                String invoiceId = invoiceGV.getString("invoiceId");
                                invoiceVO.setInvoiceId(invoiceId);
                                invoiceVO.setStatusId(invoiceGV.getString("statusId"));
                                invoiceVO.setInvoiceTypeId(invoiceGV.getString("invoiceTypeId"));
                                invoiceVO.setDueDate(invoiceGV.getTimestamp("dueDate"));
                                invoiceVO.setToParty(invoiceGV.getString("partyId"));
                                invoiceVO.setCreatedDate(invoiceGV.getTimestamp("createdStamp"));
                                try {
                                    Map<String, Object> InvoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId,"userLogin",userLogin));
                                    if (UtilValidate.isNotEmpty(InvoicePaymentInfoList)) {
                                        List<Map<String,Object>> invoicePaymentInfoList = (List<Map<String,Object>>)InvoicePaymentInfoList.get("invoicePaymentInfoList");
                                        BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfoList.get(0).get("outstandingAmount");
                                        invoiceVO.setOutstandingAmount(outstandingAmount);
                                    }else{
                                        invoiceVO.setOutstandingAmount(BigDecimal.ZERO);
                                    }
                                } catch (GenericServiceException e) {
                                    e.printStackTrace();
                                }
                                invoiceList.add(invoiceVO);
                            }
                        }
                    } else {
                        System.out.println("else part in service");
                        InvoiceVO invoiceVO = new InvoiceVO();
                        String invoiceId = invoiceGV.getString("invoiceId");
                        invoiceVO.setInvoiceId(invoiceId);
                        invoiceVO.setStatusId(invoiceGV.getString("statusId"));
                        invoiceVO.setInvoiceTypeId(invoiceGV.getString("invoiceTypeId"));
                        invoiceVO.setDueDate(invoiceGV.getTimestamp("dueDate"));
                        invoiceVO.setToParty(invoiceGV.getString("partyId"));
                        invoiceVO.setCreatedDate(invoiceGV.getTimestamp("createdStamp"));
                        try {
                            Map<String, Object> InvoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
                            if (UtilValidate.isNotEmpty(InvoicePaymentInfoList)) {
                                List<Map<String, Object>> invoicePaymentInfoList = (List<Map<String, Object>>) InvoicePaymentInfoList.get("invoicePaymentInfoList");
                                BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfoList.get(0).get("outstandingAmount");
                                invoiceVO.setOutstandingAmount(outstandingAmount);
                            } else {
                                invoiceVO.setOutstandingAmount(BigDecimal.ZERO);
                            }
                        } catch (GenericServiceException e) {
                            e.printStackTrace();
                        }
                        System.out.println("invocie VO in service");
                        invoiceList.add(invoiceVO);
                        System.out.println("invoice LIst in service(else part)");
                    }*/
                    InvoiceVO invoiceVO = new InvoiceVO();
                    String invoiceId = invoiceGV.getString("invoiceId");
                    invoiceVO.setInvoiceId(invoiceId);
                    invoiceVO.setStatusId(invoiceGV.getString("statusId"));
                    invoiceVO.setInvoiceTypeId(invoiceGV.getString("invoiceTypeId"));
                    invoiceVO.setDueDate(invoiceGV.getTimestamp("dueDate"));
                    invoiceVO.setToParty(invoiceGV.getString("partyId"));
                    invoiceVO.setCreatedDate(invoiceGV.getTimestamp("createdStamp"));
                    try {
                        Map<String, Object> InvoicePaymentInfoList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
                        if (UtilValidate.isNotEmpty(InvoicePaymentInfoList)) {
                            List<Map<String, Object>> invoicePaymentInfoList = (List<Map<String, Object>>) InvoicePaymentInfoList.get("invoicePaymentInfoList");
                            BigDecimal outstandingAmount = (BigDecimal) invoicePaymentInfoList.get(0).get("outstandingAmount");
                            invoiceVO.setOutstandingAmount(outstandingAmount);
                        } else {
                            invoiceVO.setOutstandingAmount(BigDecimal.ZERO);
                        }
                    } catch (GenericServiceException e) {
                        e.printStackTrace();
                    }
                    invoiceList.add(invoiceVO);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("invoices",invoiceList);
        return sendResp;
    }
    public static Map<String,Object> transactionLogInfoByDate(DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");

        Timestamp fromDate = (Timestamp) context.get("fromDate");

        Timestamp thruDate = (Timestamp) context.get("thruDate");

        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        List<EntityCondition> condListForExpense = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyIdTo", academyId));
        condListForExpense.add(EntityCondition.makeCondition("academyId", academyId));

        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
                EntityOperator.AND);
        condList.add(thruCond);
        condListForExpense.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);
        EntityCondition conditionForExpense = EntityCondition.makeCondition(condListForExpense);

        List<GenericValue> paymentInfo = null,expenseInfo = null;
        List<Map<String, Object>> txInfo = new ArrayList<>();
        try {
            paymentInfo = EntityQuery.use(delegator).from("Payment").where(condition).queryList();
            for (GenericValue paymentGvs : paymentInfo){
                Map<String,Object> paymentInfoMap = new HashMap<>();
                paymentInfoMap.put("transactionId",paymentGvs.getString("paymentId"));
                Timestamp txTimeStamp = paymentGvs.getTimestamp("createdStamp");
                Date txDate = new Date(txTimeStamp.getTime());
                paymentInfoMap.put("transactionTime",txDate);
                paymentInfoMap.put("transactionAmount",paymentGvs.getString("amount"));
                paymentInfoMap.put("transactionType","Payment");
                txInfo.add(paymentInfoMap);
            }
            expenseInfo = EntityQuery.use(delegator).from("AcademyExpense").where(conditionForExpense).queryList();
            for (GenericValue expenseGvs:expenseInfo){
                Map<String,Object> expenseInfoMap = new HashMap<>();
                expenseInfoMap.put("transactionId",expenseGvs.getString("expenseId"));
                Timestamp txTimeStamp = expenseGvs.getTimestamp("createdStamp");
                Date txDate = new Date(txTimeStamp.getTime());
                expenseInfoMap.put("transactionTime",txDate);
                expenseInfoMap.put("transactionAmount",expenseGvs.getString("amount"));
                expenseInfoMap.put("transactionType","Expense");
                txInfo.add(expenseInfoMap);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("resultList",txInfo);
        return sendResp;
    }
    public static Map<String,Object> transactionLog(DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");

        List<GenericValue> paymentInfo = null,expenseInfo = null;
        List<Map<String, Object>> txInfo = new ArrayList<>();
        try {
            paymentInfo = EntityQuery.use(delegator).from("Payment").where("partyIdTo",academyId).queryList();
            for (GenericValue paymentGvs : paymentInfo){
                Map<String,Object> paymentInfoMap = new HashMap<>();
                paymentInfoMap.put("transactionId",paymentGvs.getString("paymentId"));
                Timestamp txTimeStamp = paymentGvs.getTimestamp("createdStamp");
                Date txDate = new Date(txTimeStamp.getTime());
                paymentInfoMap.put("transactionTime",txDate);
                paymentInfoMap.put("transactionAmount",paymentGvs.getString("amount"));
                paymentInfoMap.put("transactionType","Payment");
                txInfo.add(paymentInfoMap);
            }
            expenseInfo = EntityQuery.use(delegator).from("AcademyExpense").where("academyId",academyId).queryList();
            for (GenericValue expenseGvs:expenseInfo){
                Map<String,Object> expenseInfoMap = new HashMap<>();
                expenseInfoMap.put("transactionId",expenseGvs.getString("expenseId"));
                Timestamp txTimeStamp = expenseGvs.getTimestamp("createdStamp");
                Date txDate = new Date(txTimeStamp.getTime());
                expenseInfoMap.put("transactionTime",txDate);
                expenseInfoMap.put("transactionAmount",expenseGvs.getString("amount"));
                expenseInfoMap.put("transactionType","Expense");
                txInfo.add(expenseInfoMap);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("resultList",txInfo);
        return sendResp;
    }
    public static Map<String,Object> getPaymentInfoById (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();

        String invoiceId = (String) context.get("invoiceId");
        String partyIdFrom = (String) context.get("partyIdFrom");

        List<GenericValue> invoicePaymentInfo = null;
        try{
            invoicePaymentInfo = EntityQuery.use(delegator).from("InvoiceAndApplAndPayment").where("invoiceId",invoiceId,"partyIdFrom",partyIdFrom).queryList();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("paymentInfo",invoicePaymentInfo);
        return sendResp;
    }
    public static Map<String,Object> cancelAcademyInvoice (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();

        String partyIdFrom = (String) context.get("partyIdFrom");
        String invoiceId = (String) context.get("invoiceId");
        GenericValue userLogin = UserLoginUtils.getSystemUserLogin(delegator);
        Map<String, Object> invoicePaymentList = null;
        try {
            invoicePaymentList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "userLogin", userLogin));
            if(UtilValidate.isNotEmpty(invoicePaymentList)){
                List<Map<String, Object>> invoicePaymentInfoListToCheck = (List<Map<String, Object>>) invoicePaymentList.get("invoicePaymentInfoList");
                BigDecimal paidAmountToCheck = (BigDecimal) invoicePaymentInfoListToCheck.get(0).get("paidAmount");
                if (paidAmountToCheck.compareTo(new BigDecimal("0.00")) == 0) {
                    Map<String, Object> serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "statusId", "INVOICE_CANCELLED", "userLogin", userLogin));
                    if (ServiceUtil.isError(serviceResults)) {
                        // return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                        throw new Exception("Exception occurred while updating invoice status for invoice " + invoiceId);
                    }
                }
                invoiceRecentActivity(dctx,partyIdFrom,invoiceId,"CANCELLED");
            }else {
                return ServiceUtil.returnFailure("Unable to update Invoice"+invoiceId);
            }
        } catch (GenericServiceException | GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to update Invoice"+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendResp.put(ModelService.RESPONSE_MESSAGE,"invoice"+invoiceId+" is updated");
        return sendResp;
    }
    public static Map<String,Object> getTotalPaymentAmount (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");
        List<GenericValue> paymentInfo = null;
        try {
            paymentInfo = EntityQuery.use(delegator).from("Payment").where("partyIdTo",academyId).queryList();
            Map<String,Object> paymentAmountInfoMap = new HashMap<>();
            BigDecimal totalPaymentAmount = BigDecimal.ZERO;
            if (UtilValidate.isNotEmpty(paymentInfo)){
                for (GenericValue paymentGvs : paymentInfo){
                    BigDecimal paymentAmount = paymentGvs.getBigDecimal("amount");
                    totalPaymentAmount = totalPaymentAmount.add(paymentAmount);
                }
                paymentAmountInfoMap.put("amount",totalPaymentAmount);
                sendResp.put("totalPaymentAmount",paymentAmountInfoMap);
            }else {
                paymentAmountInfoMap.put("amount",totalPaymentAmount);
                sendResp.put("totalPaymentAmount",paymentAmountInfoMap);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }
    public static Map<String,Object> getTotalPaymentAmountByDate (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");

        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyIdTo", academyId));
        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
                EntityOperator.AND);
        condList.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);


        List<GenericValue> paymentInfoByDate = null;
        try {
            paymentInfoByDate = EntityQuery.use(delegator).from("Payment").where(condition).queryList();
            BigDecimal totalPaymentByDate = BigDecimal.ZERO;
            Map<String,Object> paymentInfoMap = new HashMap<>();
            for (GenericValue paymentGvs : paymentInfoByDate){
                BigDecimal totalPayment = paymentGvs.getBigDecimal("amount");
                totalPaymentByDate = totalPaymentByDate.add(totalPayment);
            }
            paymentInfoMap.put("totalPayment",totalPaymentByDate);
            sendResp.put("paymentTotal",paymentInfoMap);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }
    public static  Map<String,Object> fetchAcademyInvoiceByDate(DispatchContext dctx, Map<String, ? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale  locale = (Locale) context.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String academyId = (String) context.get("academyId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");

        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyIdFrom", academyId));

        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
                EntityOperator.AND);
        condList.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);

        List<InvoiceVO> invoiceList = new ArrayList<>();
        List<GenericValue> invoices = null;
        try{
            invoices = EntityQuery.use(delegator).from("Invoice").where(condition).queryList();
            if(UtilValidate.isNotEmpty(invoices)){
                for (GenericValue invoiceGV : invoices) {
                    InvoiceVO invoiceVO = new InvoiceVO();
                    String invoiceId = invoiceGV.getString("invoiceId");
                    invoiceVO.setInvoiceId(invoiceId);
                    invoiceVO.setStatusId(invoiceGV.getString("statusId"));
                    invoiceVO.setInvoiceTypeId(invoiceGV.getString("invoiceTypeId"));
                    invoiceVO.setDueDate(invoiceGV.getTimestamp("dueDate"));
                    invoiceVO.setToParty(invoiceGV.getString("partyId"));
                    invoiceList.add(invoiceVO);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("invoicesByDate",invoiceList);
        return sendResp;
    }
    public static  Map<String,Object> getInvoiceListTotalAmount(DispatchContext dctx, Map<String, ? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String academyId = (String) context.get("academyId");
        List<GenericValue> invoices = null;
        Map<String,Object> invoiceTotalAmountMap = new HashMap<>();
        try{
            invoices = EntityQuery.use(delegator).from("Invoice").where("partyIdFrom", academyId).queryList();
            BigDecimal invoiceTotalAmount = BigDecimal.ZERO;
            if(UtilValidate.isNotEmpty(invoices)){
                for (GenericValue invoiceGV : invoices) {
                    String invoiceId = invoiceGV.getString("invoiceId");
                    invoiceTotalAmount = invoiceTotalAmount.add(InvoiceWorker.getInvoiceTotal(delegator, invoiceId));
                }
                invoiceTotalAmountMap.put("allInvoiceTotal",invoiceTotalAmount);
            }else {
                invoiceTotalAmountMap.put("allInvoiceTotal",invoiceTotalAmount);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("invoicesTotal",invoiceTotalAmountMap);

        return sendResp;
    }

    public static Map<String, Object> invoiceRecentActivity(DispatchContext dispatchContext, String academyId, String invoiceId,String action ) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Map sendResponse = ServiceUtil.returnSuccess();
        Date now = new Date();
        Timestamp activityCreatedDate = new Timestamp(now.getTime());
        Map<String, Object> createRecentActivity = new HashMap<>();
        createRecentActivity.put("academyId", academyId);
        createRecentActivity.put("activityType", "INVOICE");
        createRecentActivity.put("activityTypeId", invoiceId);
        createRecentActivity.put("activityCreatedDate", activityCreatedDate);
        createRecentActivity.put("action", action);
        try {
            localDispatcher.runSync("createRecentActivity", UtilMisc.toMap(createRecentActivity));
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return sendResponse;
    }
}
