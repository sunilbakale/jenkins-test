package com.cth.portal;

import com.cth.academy.model.ItemVO;
import com.cth.academy.utils.UserLoginUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.base.util.Base64;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.tools.ant.taskdefs.condition.Http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class BillingEvents {
    public final static String module = BillingEvents.class.getName();
    private static final String ERROR = "error";
    private static final String SUCCESS = "success";

    public static String createNewInvoice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession  session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        //BigDecimal discount = new BigDecimal("0.12"); // TODO: Handle discount
        String encodedInvoiceInfo = request.getParameter("encodedInvoiceInfo");

        //check User has permission to add a new invoice
        try {
            Map<String,Object> canAddInvoice = dispatcher.runSync("canAddInvoice",
                    UtilMisc.toMap("partyId",academyId,"planTypeId","Free"));//planTypeId Free hardcoded
            if(!ServiceUtil.isSuccess(canAddInvoice)){
                String errMsg = (String) canAddInvoice.get("errorMessage");
                request.setAttribute("_ERROR_MESSAGE_",errMsg);
                return ERROR;
            }
            Boolean hasPermissionToCreateInvoice = (Boolean) canAddInvoice.get("hasPermission");
            if (!hasPermissionToCreateInvoice){
                request.setAttribute("_ERROR_MESSAGE_","Max invoice count exceeded for the subscription,You need to upgrade plan to continue");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_","Failed to fetch subscription");
            return ERROR;
        }
        String decodedInvoice = Base64.base64Decode(encodedInvoiceInfo);

        ObjectMapper mapperObj = new ObjectMapper();
        Map<String,Object> invoiceInfoMap = new HashMap<String,Object>();
        invoiceInfoMap = mapperObj.readValue(decodedInvoice,new TypeReference<Map<String, Object>>(){});

        String invoiceNum = (String) invoiceInfoMap.get("invoiceNum");
        String invoiceDate = String.valueOf(invoiceInfoMap.get("invoiceDate"));
        long longDate = Long.parseLong(invoiceDate);

        Date invoiceDateConv = new Date(longDate);
        Timestamp dueDate = new Timestamp(invoiceDateConv.getTime());
        String studentId = (String) invoiceInfoMap.get("studentId");
        List<Map> inputInvoiceItems = (List<Map>) invoiceInfoMap.get("invoiceItems");

        List<ItemVO> itemsVoList = new ArrayList<>();
        for(Map itemEntry: inputInvoiceItems){
            ItemVO itemVO = new ItemVO();
            itemVO.setDescription((String) itemEntry.get("item"));
            String priceString = (String) itemEntry.get("price");
            BigDecimal price = new BigDecimal(priceString);
            itemVO.setUnitPrice(price) ;
            String qtyString = (String) itemEntry.get("quantity");
            BigDecimal qty = new BigDecimal(qtyString);
            itemVO.setQuantity(qty);
            itemsVoList.add(itemVO);
        }

        Map<String,Object> createInvoiceInputMap = UtilMisc.toMap("academyId",academyId);
        createInvoiceInputMap.put("toParty", studentId);
        createInvoiceInputMap.put("items", itemsVoList);
        //createInvoiceInputMap.put("invoiceTypeId", invoiceNum);
        createInvoiceInputMap.put("invoiceTypeId", "SALES_INVOICE");
        //createInvoiceInputMap.put("discount", discount);
        createInvoiceInputMap.put("dueDate", dueDate);
        createInvoiceInputMap.put("userLogin", UserLoginUtils.getSystemUserLogin(delegator));

        Map<String,Object> createNewInvoiceResponse = null;
        try{
            createNewInvoiceResponse = dispatcher.runSync("createAcademyInvoice",UtilMisc.toMap(createInvoiceInputMap));
            if(!ServiceUtil.isSuccess(createNewInvoiceResponse)){
                String errorMessage = (String) createNewInvoiceResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        }catch (GenericServiceException e){
            e.printStackTrace();
            request.setAttribute("STATUS","ERROR");
            return ERROR;

        }
        request.setAttribute("STATUS", "SUCCESS");
        return SUCCESS;
    }

    public static String updateInvoicePayment(HttpServletRequest request, HttpServletResponse response) {
        HttpSession  session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String invoiceId = request.getParameter("invoiceId");
        String studentId = request.getParameter("studentId");
        String paymentType = request.getParameter("payment_method");
        BigDecimal amount = (BigDecimal.valueOf(Long.parseLong(request.getParameter("amount"))));
        String paymentDesc = request.getParameter("paymentDesc");
        try {
            Map<String, Object> updateInvoiceInputMap = dispatcher.runSync("updateInvoicePayment",
                    UtilMisc.toMap("academyId", academyId,
                            "invoiceId", invoiceId,
                            "fromParty", studentId,
                            "amount", amount,
                            "paymentMethodTypeId",paymentType,
                            "comments", paymentDesc,
                            "userLogin",UserLoginUtils.getSystemUserLogin(delegator)
                    ));
            if(!ServiceUtil.isSuccess(updateInvoiceInputMap)){
                String errorMessage = (String) updateInvoiceInputMap.get("errorMessage");
                return ERROR;
        }
        }catch (GenericServiceException e){
            e.printStackTrace();
            request.setAttribute("STATUS","ERROR");
            request.setAttribute("_ERROR_MESSAGE_","Unable to apply payment. " + e.getMessage());
            return "error";
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String cancelAcademyInvoice(HttpServletRequest request, HttpServletResponse response){
        HttpSession  session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String invoiceId = request.getParameter("invoiceId");
        try {
            Map<String,Object> cancelInvoiceInputMap = dispatcher.runSync("cancelAcademyInvoice",UtilMisc.toMap("invoiceId",invoiceId,"partyIdFrom",academyId));
            if (!ServiceUtil.isSuccess(cancelInvoiceInputMap)){
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
