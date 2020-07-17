package com.cth.portal;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class ExpenseEvent {
    public static final String module = ExpenseEvent.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String createExpense(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String expName = request.getParameter("expTitle");
        long expenseDateLong = Long.parseLong(request.getParameter("expDate"));
        Date expDateVal = new Date(expenseDateLong);
        Timestamp expDate = new Timestamp(expDateVal.getTime());
        BigDecimal expAmount = (BigDecimal.valueOf(Long.parseLong(request.getParameter("expAmount"))));
        String expPayMode = request.getParameter("expPayMode");
        String expDesc = request.getParameter("expDesc");

        Map<String,Object> createExpenseInput = UtilMisc.toMap("academyId",academyId);
        createExpenseInput.put("title",expName);
        createExpenseInput.put("date",expDate);
        createExpenseInput.put("amount",expAmount);
        createExpenseInput.put("paymentMode",expPayMode);
        createExpenseInput.put("description",expDesc);

        Map<String,Object> createExpenseInputResponse =null;
        try{
            createExpenseInputResponse = dispatcher.runSync("createExpense",UtilMisc.toMap(createExpenseInput));
            if(!ServiceUtil.isSuccess(createExpenseInputResponse)){
                String errorMessage = (String)createExpenseInputResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS","ERROR");
            return ERROR;
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }

    public static String removeExpense(HttpServletRequest request,HttpServletResponse response){
    Delegator delegator = (Delegator) request.getAttribute("delegator");
    LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    HttpSession session = request.getSession();
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    String academyId = (String) userLogin.get("partyId");
    String expenseId = request.getParameter("expenseId");

        try {
            Map<String,Object> deleteExpenseInput = dispatcher.runSync("removeExpense",UtilMisc.toMap("academyId",academyId,"expenseId",expenseId));
            if((!ServiceUtil.isSuccess(deleteExpenseInput))){
                String errorMessage = (String) deleteExpenseInput.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS","ERROR");
            return ERROR;
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String updateAcademyExpense(HttpServletRequest request,HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String expenseId = request.getParameter("expenseId");
        String title = request.getParameter("title");
        long dateLong = Long.parseLong(request.getParameter("date"));
        Date dateDate = new Date(dateLong);
        Timestamp date = new Timestamp(dateDate.getTime());
        BigDecimal amount = (BigDecimal.valueOf(Long.parseLong(request.getParameter("amount"))));
        String paymentMode = request.getParameter("paymentMode");
        String description = request.getParameter("description");

        Map<String,Object> updateExpenseInput = UtilMisc.toMap("academyId",academyId);
        updateExpenseInput.put("expenseId",expenseId);
        updateExpenseInput.put("title",title);
        updateExpenseInput.put("date",date);
        updateExpenseInput.put("amount",amount);
        updateExpenseInput.put("paymentMode",paymentMode);
        updateExpenseInput.put("description",description);

        Map<String,Object> updateExpenseResponse = null;
        try {
            updateExpenseResponse = dispatcher.runSync("updateAcademyExpense", UtilMisc.toMap(updateExpenseInput));
            String errorMessage = (String) updateExpenseResponse.get("errorMessage");
            if (!ServiceUtil.isSuccess(updateExpenseResponse)){
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS","ERROR");
            return ERROR;
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
}
