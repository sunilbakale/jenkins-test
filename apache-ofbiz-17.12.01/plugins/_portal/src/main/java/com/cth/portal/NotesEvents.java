package com.cth.portal;

import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

public class NotesEvents {
    public static final String module = ExpenseEvent.class.getName();
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static String createPvtNote(HttpServletRequest request, HttpServletResponse response)
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String title = request.getParameter("pvtNoteTitle");
        String summary = request.getParameter("pvtNoteSummary");
        String noteParty[] = request.getParameterValues("noteParty[]");
        Map<String, Object> createNoteInputMap = UtilMisc.toMap("academyId", academyId);

        createNoteInputMap.put("noteName",title);
        createNoteInputMap.put("note",summary);
        createNoteInputMap.put("userLogin",UserLoginUtils.getSystemUserLogin(delegator));
        if(UtilValidate.isNotEmpty(noteParty) && noteParty.length > 0) {
            List<String> eventGuestIds = new ArrayList<>();
            Collections.addAll(eventGuestIds, noteParty);
            createNoteInputMap.put("partyId",eventGuestIds);
        }

        try {
            Map<String,Object> createNoteInputMapInfo = dispatcher.runSync("createPvtNote", UtilMisc.toMap(createNoteInputMap));
            if(!ServiceUtil.isSuccess(createNoteInputMapInfo)){
                String errorMessage = (String) createNoteInputMapInfo.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String updatePvtNote(HttpServletRequest request, HttpServletResponse response)
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String noteId = request.getParameter("privateNoteId");
        String title = request.getParameter("pvtNoteTitle");
        String summary = request.getParameter("pvtNoteSummary");
        String noteParty[] = request.getParameterValues("noteParty[]");

        Map<String,Object> updateNoteInputMap = new HashMap<>();
        updateNoteInputMap.put("noteId",noteId);
        updateNoteInputMap.put("academyId",academyId);
        updateNoteInputMap.put("noteName",title);
        updateNoteInputMap.put("note",summary);
        updateNoteInputMap.put("userLogin",UserLoginUtils.getSystemUserLogin(delegator));
        if(UtilValidate.isNotEmpty(noteParty) && noteParty.length > 0) {
            List<String> eventGuestIds = new ArrayList<>();
            Collections.addAll(eventGuestIds, noteParty);
            updateNoteInputMap.put("partyId",eventGuestIds);
        }
        try {
            Map<String,Object> createNoteInputMap = dispatcher.runSync("updateNote",
                    UtilMisc.toMap(updateNoteInputMap));
            if(!ServiceUtil.isSuccess(createNoteInputMap)){
                String errorMessage = (String) createNoteInputMap.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
    public static String deletePvtNote(HttpServletRequest request, HttpServletResponse response)
    {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        System.out.println("academyId is "+academyId);

        String noteId = request.getParameter("privateNoteId");
        System.out.print("inside removePvtNote");
        System.out.print("inside removePvtNote"+noteId);
        try {
            Map<String,Object> createNoteInputMap = dispatcher.runSync("removePrivateNote",
                    UtilMisc.toMap("partyId",academyId,
                            "noteId",noteId,
                            "userLogin",UserLoginUtils.getSystemUserLogin(delegator)));
            System.out.print("inside try"+createNoteInputMap);
            if(!ServiceUtil.isSuccess(createNoteInputMap)){
                String errorMessage = (String) createNoteInputMap.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                request.setAttribute("STATUS","ERROR");
                return ERROR;
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        request.setAttribute("STATUS","SUCCESS");
        return SUCCESS;
    }
}

