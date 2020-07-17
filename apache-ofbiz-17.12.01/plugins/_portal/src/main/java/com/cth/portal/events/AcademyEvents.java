package com.cth.portal.events;
import com.cth.academy.utils.UserLoginUtils;
import com.cth.portal.utils.PortalLoginWorker;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class AcademyEvents {
    public final static String module = AcademyEvents.class.getName();
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";

    public static String updateAcademy(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String partyId = request.getParameter("partyId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");

        try {
            Map<String,Object> updateAcademyResp = dispatcher.runSync("updateAcademy",UtilMisc.toMap("userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                    "academyId",partyId,
                    "firstName",firstName,
                    "lastName",lastName,
                    "email",email,
                    "mobile",mobile));
            if (!ServiceUtil.isSuccess(updateAcademyResp)){
                String errorMessage = (String) updateAcademyResp.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS",ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS",ERROR);
            return ERROR;
        }
        System.out.println("SUCCESS");
        request.setAttribute("STATUS",SUCCESS);
        return SUCCESS;
    }

    public static String updatePreferredCurrency(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String partyId = academyId;
        String preferredCurrencyVal = request.getParameter("preferredCurrency");

        try {
            Map<String,Object> updatePreferredCurrencyMap = dispatcher.runSync("updateAcademyPartyAttrCurrencyInfo",UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                        "partyId",partyId,
                        "attrName","preferred_currency",
                        "attrVal",preferredCurrencyVal));
            if (!ServiceUtil.isSuccess(updatePreferredCurrencyMap)){
                String errorMessage = (String) updatePreferredCurrencyMap.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS",ERROR);
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS",ERROR);
            return ERROR;
        }
        request.setAttribute("STATUS",SUCCESS);
        return SUCCESS;
    }
    public static String updatePassword(HttpServletRequest request,HttpServletResponse response){
//        List<String> errorList = PasswordPolicyHelper.validatePasswordPolicy(request.getParameter("currentPassword"));
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String username = userLogin.getString("userLoginId");

        String  currentPassword = (String) request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String newPasswordVerify = request.getParameter("newPasswordVerify");

        Map<String,Object> pwdInputMap =UtilMisc.toMap("login.username", username, "login.password", currentPassword, "locale", UtilHttp.getLocale(request));
        pwdInputMap.put("userLoginId", username);
        pwdInputMap.put("currentPassword", currentPassword);
        pwdInputMap.put("newPassword", newPassword);
        pwdInputMap.put("newPasswordVerify", newPasswordVerify);

        Map<String, Object> resultPasswordChange = null;

        resultPasswordChange = PortalLoginWorker.updatePassword(request,response,pwdInputMap);

        if (ServiceUtil.isError(resultPasswordChange)) {
            String errorMessage = (String) resultPasswordChange.get(ModelService.ERROR_MESSAGE);
            if (UtilValidate.isNotEmpty(errorMessage)) {
                request.setAttribute("info", errorMessage);
                request.setAttribute("message",ERROR);
                return ERROR;
            }
            request.setAttribute("info", resultPasswordChange.get(ModelService.ERROR_MESSAGE_LIST));
            request.setAttribute("message",ERROR);
            return ERROR;
        }
        if(null == resultPasswordChange){
            request.setAttribute("info", "Current Password is not correct, please re-enter.");
            request.setAttribute("message",ERROR);
            return ERROR;
        }
        request.setAttribute("message",SUCCESS);
        request.setAttribute("info","Password updated successfully");
        return SUCCESS;
    }
    public static String createTeacherHelperAccount(HttpServletRequest request,HttpServletResponse response){
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

        String planType = request.getParameter("planType");
        System.out.println("planType::"+planType);

        String name = request.getParameter("name");
        System.out.println("name::"+name);

        String password = request.getParameter("password");
        System.out.println("password::"+password);

        String passwordVerify = request.getParameter("passwordVerify");
        System.out.println("passwordVerify::"+passwordVerify);

        return SUCCESS;
    }
}
