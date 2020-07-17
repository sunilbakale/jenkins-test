package com.cth.portal;

import com.cth.academy.model.EventVO;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class CalenderEvents {
    public final static String module = CalenderEvents.class.getName();
    private static final String ERROR = "error";
    private static final String SUCCESS = "success";
    // TODO: Create new events method to fetch list of events
    // it should first get start & end date parameter (eg. if monthly -- day 1 to day 30/31 -- like example i showed)
    // invoke service getEvents to get list of events
    // then prepare List and return in request Attribute ("events")
    // events list should have event in this format:

    /**
     we will return this format of event
     {
     "title": "Event 1",
     "start": 1567674000,
     "end": 1567706400
     },

     * @param request
     * @param response
     * @return
     * @throws ParseException
     * @throws GenericServiceException
     */
    public static String fetchCalendarEvents(HttpServletRequest request, HttpServletResponse response)  {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        Long startDateFetch = 1577854932000L; // Jan 1, 2020
        Date geStartDate = new Date(startDateFetch);
        Timestamp startTimestampFetch = new Timestamp(geStartDate.getTime());

        Long thruDateFetch = 1583038932000L; // March 1, 2020
        Date gethruDate = new Date(thruDateFetch);
        Timestamp thruTimestampFetch = new Timestamp(gethruDate.getTime());

        System.out.println("START: " + startTimestampFetch);
        System.out.println("END:" + thruTimestampFetch);

        Map<String, Object> getEventInputMap = UtilMisc.toMap("academyId", academyId);
        getEventInputMap.put("startDate", startTimestampFetch);
        getEventInputMap.put("thruDate", thruTimestampFetch);

        Map<String, Object> getEventsResponse = null;
        try {
            getEventsResponse = dispatcher.runSync("getEvents", UtilMisc.toMap(getEventInputMap));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", "Error getting events");
            return "error";
        }
        List<EventVO> eventsList = (List) getEventsResponse.get("events");

        List<Map<String,Object>> eventsToReturn = new ArrayList<>();

        for (EventVO event : eventsList) {
            System.out.println("event"+event);

            Map<String,Object> eventEntry = new HashMap<>();
            eventEntry.put("title", event.getTitle());
            eventEntry.put("start", event.getStartDateTime().getTime());
            eventEntry.put("end", event.getEndDateTime().getTime());

            eventsToReturn.add(eventEntry);
            // TODO: Process and make output compatible with fullCalendar expectation
        }

        request.setAttribute("events", eventsToReturn);
        return "success";

    }

    public static String createCalendarEvent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String type = request.getParameter("classType");//"CLASS";
        String ename = request.getParameter("ename");

        String[] studentIds = request.getParameterValues("studentIds[]");

        long sdate = Long.parseLong(request.getParameter("startDate"));
        long edate = Long.parseLong(request.getParameter("endDate"));

        Date startDate = new Date(sdate);
        Timestamp startTimestamp = new Timestamp(startDate.getTime());

        Date endDate = new Date(edate);
        Timestamp endTimestamp = new Timestamp(endDate.getTime());

        String eventDescription = request.getParameter("edesc");
        String eventLocation = request.getParameter("eventLoc");
        String isPrivateEvent = request.getParameter("isPrivateEvent");
        String notifyGuests = "N";
        String isRecurringEvent = request.getParameter("isRecurringEvent");

        Map<String, Object> createEventInputMap = UtilMisc.toMap("academyId", academyId);
        createEventInputMap.put("academyId", academyId);
        createEventInputMap.put("type", type);
        createEventInputMap.put("title", ename);
        createEventInputMap.put("description", eventDescription);
        createEventInputMap.put("eventLocation", eventLocation);
        createEventInputMap.put("startDateTime", startTimestamp);
        createEventInputMap.put("endDateTime", endTimestamp);
        createEventInputMap.put("isPrivateEvent", isPrivateEvent);
        createEventInputMap.put("isRecurringEvent", isRecurringEvent);

        if("Y".equalsIgnoreCase(isRecurringEvent)) {
            String recurringType = request.getParameter("recurringType");

            String recStartDate = request.getParameter("startDateVal");
            long startDateVal = Long.parseLong(recStartDate);
            Date startDateCon = new Date(startDateVal);
            Timestamp startDateRec = new Timestamp(startDateCon.getTime());
            createEventInputMap.put("startDate", startDateRec);

            String until = request.getParameter("until");
            switch(recurringType){
                case "DAILY":
                    createEventInputMap.put("recurringType", recurringType);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        createEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        createEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        createEventInputMap.put("endDate", endDateRec);
                    }
                    break;
                case "WEEKLY":
                    createEventInputMap.put("recurringType", recurringType);
                    String onSunday = request.getParameter("onSunday");
                    String onMonday = request.getParameter("onMonday");
                    String onTuesday = request.getParameter("onTuesday");
                    String onWednesday = request.getParameter("onWednesday");
                    String onThursday = request.getParameter("onThursday");
                    String onFriday = request.getParameter("onFriday");
                    String onSaturday = request.getParameter("onSaturday");

                    createEventInputMap.put("onSunday", onSunday);
                    createEventInputMap.put("onMonday", onMonday);
                    createEventInputMap.put("onTuesday", onTuesday);
                    createEventInputMap.put("onWednesday", onWednesday);
                    createEventInputMap.put("onThursday", onThursday);
                    createEventInputMap.put("onFriday", onFriday);
                    createEventInputMap.put("onSaturday", onSaturday);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        createEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        createEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        createEventInputMap.put("endDate", endDateRec);
                    }
                    break;
                case "MONTHLY":
                    createEventInputMap.put("recurringType", recurringType);
                    long onMonthDay = Long.valueOf(request.getParameter("onMonthDay"));
                    createEventInputMap.put("onMonthDay", onMonthDay);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        createEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        createEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        createEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        createEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        createEventInputMap.put("endDate", endDateRec);
                    }
                    break;
            }
        }
        if(UtilValidate.isNotEmpty(studentIds) && studentIds.length > 0) {
            List<String> eventGuestIds = new ArrayList<>();
            Collections.addAll(eventGuestIds, studentIds);
            createEventInputMap.put("guestList", eventGuestIds);
        }
        createEventInputMap.put("notifyGuests", notifyGuests);
        createEventInputMap.put("userLogin", UserLoginUtils.getSystemUserLogin(delegator));
        Map<String, Object> createEventrResponse = null;
        try {
            createEventrResponse = dispatcher.runSync("createEvent", UtilMisc.toMap(createEventInputMap));

            if(!ServiceUtil.isSuccess(createEventrResponse)){
                String errorMessage = (String) createEventrResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                return ERROR;
            }

        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS", "ERROR");
            request.setAttribute("_ERROR_MESSAGE_", "Unable to add new event");
            request.setAttribute("_ERROR_", e.getMessage());
            return ERROR;
        }

        request.setAttribute("STATUS", "SUCCESS");
        request.setAttribute("eventId", createEventrResponse.get("eventId"));

        return SUCCESS;
    }

    public static String updateCalendarEvent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String type = "CLASS";
        String eName = request.getParameter("eventName");
        String eventId = request.getParameter("eventId");

        String[] updateStudentIds = request.getParameterValues("studentIds[]");

        long sdate = Long.parseLong(request.getParameter("updateStartDateTime"));
        long edate = Long.parseLong(request.getParameter("updateEndDateTime"));

        Date startDate = new Date(sdate);
        Timestamp startTimestamp = new Timestamp(startDate.getTime());

        Date endDate = new Date(edate);
        Timestamp endTimestamp = new Timestamp(endDate.getTime());

        String eventDescription = request.getParameter("updateEventDesc");
        String updateEventLoc = "CLASS";
        String isPrivateEvent = request.getParameter("isPrivateEvent");
        String notifyGuests = "N";
        String isRecurringEvent = request.getParameter("isRecurringEvent");

        Map<String, Object> updateEventInputMap = UtilMisc.toMap("academyId", academyId);
        updateEventInputMap.put("academyId", academyId);
        updateEventInputMap.put("type", type);
        updateEventInputMap.put("eventId", eventId);
        updateEventInputMap.put("title", eName);
        updateEventInputMap.put("description", eventDescription);
        updateEventInputMap.put("eventLocation", updateEventLoc);
        updateEventInputMap.put("startDateTime", startTimestamp);
        updateEventInputMap.put("endDateTime", endTimestamp);
        updateEventInputMap.put("isPrivateEvent",isPrivateEvent);
        updateEventInputMap.put("isRecurringEvent",isRecurringEvent);
        if("Y".equalsIgnoreCase(isRecurringEvent)) {
            String recurringType = request.getParameter("recurringType");

            String recStartDate = request.getParameter("startDateVal");
            long startDateVal = Long.parseLong(recStartDate);
            Date startDateCon = new Date(startDateVal);
            Timestamp startDateRec = new Timestamp(startDateCon.getTime());
            updateEventInputMap.put("startDate", startDateRec);

            String until = request.getParameter("until");
            switch(recurringType){
                case "DAILY":
                    updateEventInputMap.put("recurringType", recurringType);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        updateEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        updateEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        updateEventInputMap.put("endDate", endDateRec);
                    }
                    break;
                case "WEEKLY":
                    updateEventInputMap.put("recurringType", recurringType);
                    String onSunday = request.getParameter("onSunday");
                    String onMonday = request.getParameter("onMonday");
                    String onTuesday = request.getParameter("onTuesday");
                    String onWednesday = request.getParameter("onWednesday");
                    String onThursday = request.getParameter("onThursday");
                    String onFriday = request.getParameter("onFriday");
                    String onSaturday = request.getParameter("onSaturday");

                    updateEventInputMap.put("onSunday", onSunday);
                    updateEventInputMap.put("onMonday", onMonday);
                    updateEventInputMap.put("onTuesday", onTuesday);
                    updateEventInputMap.put("onWednesday", onWednesday);
                    updateEventInputMap.put("onThursday", onThursday);
                    updateEventInputMap.put("onFriday", onFriday);
                    updateEventInputMap.put("onSaturday", onSaturday);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        updateEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        updateEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        updateEventInputMap.put("endDate", endDateRec);
                    }
                    break;
                case "MONTHLY":
                    updateEventInputMap.put("recurringType", recurringType);
                    long onMonthDay = Long.valueOf(request.getParameter("onMonthDay"));
                    updateEventInputMap.put("onMonthDay", onMonthDay);

                    if("FOR_EVER".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);
                        long recSpecLong = Long.parseLong(request.getParameter("recEvtEndDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else if ("TILL_DATE".equalsIgnoreCase(until)){
                        updateEventInputMap.put("until", until);

                        long recSpecLong = Long.parseLong(request.getParameter("recSpecDate"));
                        Date recSpecDate = new Date(recSpecLong);
                        Timestamp endDateTimeStamp = new Timestamp(recSpecDate.getTime());
                        updateEventInputMap.put("endDate", endDateTimeStamp);

                    }else {
                        updateEventInputMap.put("until", until);
                        long numberOfOccurrence = Long.valueOf(request.getParameter("recOcur"));
                        updateEventInputMap.put("numberOfOccurrence", numberOfOccurrence);

                        String recEnddate = request.getParameter("recEvtEndDate");
                        long endtDateVal = Long.parseLong(recEnddate);
                        Date endtDateCon = new Date(endtDateVal);
                        Timestamp endDateRec = new Timestamp(endtDateCon.getTime());
                        updateEventInputMap.put("endDate", endDateRec);
                    }
                    break;
            }
        }
        if(UtilValidate.isNotEmpty(updateStudentIds) && updateStudentIds.length > 0) {
            List<String> eventGuestIds = new ArrayList<>();
            Collections.addAll(eventGuestIds, updateStudentIds);
            updateEventInputMap.put("guestList", eventGuestIds);
        }
        updateEventInputMap.put("userLogin", UserLoginUtils.getSystemUserLogin(delegator));

        Map<String, Object> updateEventrResponse = null;
        try {
            updateEventrResponse = dispatcher.runSync("updateEvent", UtilMisc.toMap(updateEventInputMap));

            if(!ServiceUtil.isSuccess(updateEventrResponse)){
                String errorMessage = (String) updateEventrResponse.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                return ERROR;
            }

        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS", "ERROR");
            request.setAttribute("_ERROR_MESSAGE_", "Unable to update event");
            request.setAttribute("_ERROR_", e.getMessage());
            return ERROR;
        }

        request.setAttribute("STATUS", "SUCCESS");
        request.setAttribute("eventId", updateEventrResponse.get("eventId"));

        return SUCCESS;
    }

    public static String deleteEvent(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String eventId = request.getParameter("del_eventId");
        Map<String, Object> deleteEventInputMap = null;
        try {
            deleteEventInputMap = dispatcher.runSync("deleteEvent",
                    UtilMisc.toMap("academyId", academyId,
                            "eventId", eventId,
                            "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
            if(!ServiceUtil.isSuccess(deleteEventInputMap)){
                String errorMessage = (String) deleteEventInputMap.get("errorMessage");
                Debug.logError(errorMessage,module);
                request.setAttribute("_ERROR_MESSAGE_",errorMessage);
                return ERROR;
            }

        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("STATUS", "ERROR");
            request.setAttribute("_ERROR_MESSAGE_", "Unable to add new event");
            request.setAttribute("_ERROR_", e.getMessage());
            return ERROR;
        }

        request.setAttribute("STATUS", "SUCCESS");
        request.setAttribute("eventId", deleteEventInputMap.get("eventId"));

        return SUCCESS;
    }
}
