package com.cth.academy.services;

import com.cth.academy.model.ExpenseVO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class ExpenseServices {
    public static final String module = ExpenseServices.class.getName();
    public static final String resourse = "";//need to fill
    public static Map<String,Object> createExpense(DispatchContext dctx, Map<String,? extends Object> inputMap){
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) inputMap.get("locale");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String expenseId = null;
        String academyId = (String) inputMap.get("academyId");
        String title = (String) inputMap.get("title");
        Timestamp date = (Timestamp) inputMap.get("date");
        BigDecimal amount = (BigDecimal) inputMap.get("amount");
        String paymentMode = (String) inputMap.get("paymentMode");
        String description = (String) inputMap.get("description");
        String errorMsg = UtilProperties.getMessage(resourse,"", UtilMisc.toMap("returnId",""),locale);

        String newExpenseId = delegator.getNextSeqId("AcademyExpense");
        if(!UtilValidate.isEmpty(newExpenseId)){
            Map<String,Object> createExpenseContext = new HashMap<>();
            createExpenseContext.put("academyId",academyId);
            createExpenseContext.put("expenseId",newExpenseId);
            createExpenseContext.put("title",title);
            createExpenseContext.put("date",date);
            createExpenseContext.put("amount",amount);
            createExpenseContext.put("paymentMode",paymentMode);
            createExpenseContext.put("description",description);

            try {
                GenericValue expenseGvs = delegator.makeValue("AcademyExpense",UtilMisc.toMap(createExpenseContext));
                delegator.create(expenseGvs);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to create a expense" +e.getMessage());
            }
            sendResp.put("expenseId",newExpenseId);
        }
    return sendResp;
    }
    public static Map<String,Object> getExpenses (DispatchContext dctx,Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String academyId = (String) context.get("academyId");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        List<ExpenseVO> expensesList = new ArrayList<>();
        List<GenericValue> expensesGvs = null;
        try {
            expensesGvs = EntityQuery.use(delegator).from("AcademyExpense").where("academyId",academyId).queryList();
            if(UtilValidate.isNotEmpty(expensesGvs)) {
                for (GenericValue expenseGv : expensesGvs) {
                    ExpenseVO expenseVO = new ExpenseVO();
                    expenseVO.setExpenseId(expenseGv.getString("expenseId"));
                    expenseVO.setTitle(expenseGv.getString("title"));
                    expenseVO.setDate(expenseGv.getTimestamp("date"));
                    expenseVO.setAmount(expenseGv.getBigDecimal("amount"));
                    expenseVO.setPaymentMode(expenseGv.getString("paymentMode"));
                    expenseVO.setDescription(expenseGv.getString("description"));
                    expensesList.add(expenseVO);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("expenses",expensesList);
        return sendResp;
    }
    public static Map<String,Object> removeExpense(DispatchContext dctx, Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String academyId = (String) context.get("academyId");
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String expenseId = (String) context.get("expenseId");
        try {
            GenericValue expenseGvs = delegator.findOne("AcademyExpense",UtilMisc.toMap("academyId",academyId,"expenseId",expenseId),false);
            Debug.logInfo("Expense Gv to delete"+expenseGvs,module);
            if(UtilValidate.isNotEmpty(expenseGvs)){
                expenseGvs.remove();
            }else {
                return ServiceUtil.returnFailure("Unable delete Expense ");
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable delete Expense "+e.getMessage());
        }
        sendResp.put(ModelService.RESPONSE_MESSAGE,"Expense "+expenseId+" is deleted");
        return sendResp;
    }

    public static Map<String,Object> updateAcademyExpense (DispatchContext dctx,Map<String,? extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String academyId = (String) context.get("academyId");
        String expenseId = (String) context.get("expenseId");
        String title = (String) context.get("title");
        Timestamp date = (Timestamp) context.get("date");
        BigDecimal amount = (BigDecimal) context.get("amount");
        String paymentMode = (String) context.get("paymentMode");
        String description = (String) context.get("description");

        if(!expenseId.isEmpty()){
            Map<String,Object> updateExpenseInput = new HashMap<>();
            updateExpenseInput.put("academyId",academyId);
            updateExpenseInput.put("expenseId",expenseId);
            updateExpenseInput.put("title",title);
            updateExpenseInput.put("date",date);
            updateExpenseInput.put("amount",amount);
            updateExpenseInput.put("paymentMode",paymentMode);
            updateExpenseInput.put("description",description);

            GenericValue expenseInputGvs = null;
            try {
                expenseInputGvs = delegator.findOne("AcademyExpense", UtilMisc.toMap("expenseId",expenseId),false);
                if (UtilValidate.isNotEmpty(expenseInputGvs)){
                    expenseInputGvs.putAll(updateExpenseInput);
                    expenseInputGvs.store();
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to update Expense"+e.getMessage());
            }
        }
        return sendResp;
    }
    public static Map<String,Object> getExpense (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

//        String academyId = (String) context.get("academyId");
        String expenseId = (String) context.get("expenseId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        if (!expenseId.isEmpty()){
            try {
                Map<String,Object> getExpenseMap = delegator.findOne("AcademyExpense",UtilMisc.toMap("expenseId",expenseId),false);
                System.out.println("get_expense Gv"+getExpenseMap);
                if(UtilValidate.isNotEmpty(getExpenseMap)) {
                    sendResp.put("resultMap",getExpenseMap);
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to get Expense"+e.getMessage());
            }
        }
        return sendResp;
    }
    public static Map<String,Object> getTotalExpenseAmount (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");
        List<GenericValue> expenseInfo = null;
        try {
            expenseInfo = EntityQuery.use(delegator).from("AcademyExpense").where("academyId",academyId).queryList();
            Map<String,Object> expenseAmountInfoMap = new HashMap<>();
            BigDecimal totalExpenseAmount = BigDecimal.ZERO;
            for (GenericValue expenseGvs:expenseInfo){
                BigDecimal expenseAmount = expenseGvs.getBigDecimal("amount");
                totalExpenseAmount = totalExpenseAmount.add(expenseAmount);
            }
            expenseAmountInfoMap.put("amount",totalExpenseAmount);
            sendResp.put("totalExpenseAmount",expenseAmountInfoMap);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }
    public static Map<String,Object> getTotalExpenseAmountByDate (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map <String,Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");

        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");

        List<EntityCondition> condListForExpense = new LinkedList<EntityCondition>();
        condListForExpense.add(EntityCondition.makeCondition("academyId", academyId));
        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
                EntityOperator.AND);
        condListForExpense.add(thruCond);
        EntityCondition conditionForExpense = EntityCondition.makeCondition(condListForExpense);


        List<GenericValue> expenseInfoByDate = null;
        try {

            expenseInfoByDate = EntityQuery.use(delegator).from("AcademyExpense").where(conditionForExpense).queryList();
            BigDecimal totalExpenseByDate = BigDecimal.ZERO;
            Map<String,Object> expenseInfoMap = new HashMap<>();
            for (GenericValue paymentGvs : expenseInfoByDate){
                BigDecimal totalPayment = paymentGvs.getBigDecimal("amount");
                totalExpenseByDate = totalExpenseByDate.add(totalPayment);
            }
            expenseInfoMap.put("totalExpense",totalExpenseByDate);
            sendResp.put("expenseTotal",expenseInfoMap);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }
}
