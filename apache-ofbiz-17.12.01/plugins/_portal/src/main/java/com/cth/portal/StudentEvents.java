package com.cth.portal;

import com.cth.academy.utils.UserLoginUtils;
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
import java.util.Map;

public class StudentEvents {

    private static final String ERROR = "error";
    private static final String SUCCESS = "success";
    private final static String module = StudentEvents.class.getName();

    public static String addStudent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");

        String firstName=request.getParameter("firstName");
        String lastName=request.getParameter("lastName");
        String email=request.getParameter("email");
        String mobile=request.getParameter("mobile");

        //check User has permission to add a new student
        try {
            Map<String,Object> canAddStudent = dispatcher.runSync("canAddStudent",
                    UtilMisc.toMap("partyId",academyId,"planTypeId","Free"));//planTypeId Free hardcoded
            if(!ServiceUtil.isSuccess(canAddStudent)){
                String errMsg = (String) canAddStudent.get("errorMessage");
                request.setAttribute("_ERROR_MESSAGE_",errMsg);
                return ERROR;
            }
            Boolean hasPermissionToAddStudent = (Boolean) canAddStudent.get("hasPermission");
            if (!hasPermissionToAddStudent){
                request.setAttribute("_ERROR_MESSAGE_","Max student count exceeded for the subscription,You need to upgrade plan to continue");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_","Failed to fetch subscription");
            return ERROR;
        }
        try {
            Map<String, Object> createStudentResponse = dispatcher.runSync("createStudent",
                    UtilMisc.<String, Object>toMap("academyId",academyId,
                            "firstName", firstName,
                            "lastName", lastName,
                            "email", email,
                            "mobile",mobile,
                            "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));

            if(!ServiceUtil.isSuccess(createStudentResponse)){
                String errorMessage = (String) createStudentResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                return ERROR;
            }

        } catch (GenericServiceException e) {
            e.printStackTrace();
            return "error";
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String updateStudent(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");

        String studentId = request.getParameter("studentId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String mobile = request.getParameter("mobile");
        try {
            Map<String, Object> updateStudentResponse = dispatcher.runSync("updateStudent",
                    UtilMisc.toMap(  "academyId",academyId,
                                            "studentId",studentId,
                                            "firstName", firstName,
                                            "lastName", lastName,
                                            "mobile", mobile,
                                            "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));

            if(!ServiceUtil.isSuccess(updateStudentResponse)){
                String errorMessage = (String) updateStudentResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                return ERROR;
            }

        } catch (GenericServiceException e) {
            e.printStackTrace();
            return "error";
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String removeStudentFromAcademy(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");

        String studentId = request.getParameter("studentId");
        System.out.println("StudentId:"+studentId);
         try {
            Map<String, Object> deleteStudentResponse = dispatcher.runSync("removeStudentFromAcademy",
                    UtilMisc.toMap("academyId",academyId,
                                    "studentId",studentId,
                                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
                if(!ServiceUtil.isSuccess(deleteStudentResponse)){
                    String errorMessage = (String) deleteStudentResponse.get("errorMessage");
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


}
