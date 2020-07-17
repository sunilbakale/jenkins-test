package com.cth.academy.services;

import com.cth.academy.model.EventVO;
import com.cth.academy.model.RecurringEventVO;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
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
import org.apache.ofbiz.service.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventServices {
    public final static String module = EventServices.class.getName();
    private static boolean recentActivityViewDone = false;

    /**
     * Get Events for given academy
     *
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getEvents(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String partyId = (String) context.get("partyId");
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        List<EventVO> events = new ArrayList<>();
        Timestamp fromDate = (Timestamp) context.get("startDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        String isPrivateEvent = (String) context.get("isPrivateEvent");

        // Getting Events for Academy
        Debug.logInfo("Getting Events for Party:  " + partyId, module);

        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyId", partyId));
        condList.add(EntityCondition.makeCondition("isDeleted", "N"));
        if (UtilValidate.isNotEmpty(isPrivateEvent)) {
            condList.add(EntityCondition.makeCondition("isPrivateEvent", isPrivateEvent.toUpperCase()));
        }
        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("endDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
                EntityCondition.makeCondition("endDateTime", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
                EntityOperator.AND);
        condList.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);

        try {
            List<GenericValue> eventAndEventRole = EntityQuery.use(delegator).from("EventAndEventRole").where(condition).queryList();
            if (UtilValidate.isNotEmpty(eventAndEventRole)) {
                for (GenericValue event : eventAndEventRole) {
                    EventVO eventVO = buildEvent(delegator, event);
                    if ("Y".equalsIgnoreCase(eventVO.getIsRecurringEvent())) {
                        getRecurringEventByICal4j(eventVO, events, fromDate, thruDate, delegator);
                    } else {
                        events.add(eventVO);
                    }
                }
            }

        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("events", events);
        return sendResp;
    }


    private static void getRecurringEventByICal4j(EventVO eventVO, List<EventVO> events, Timestamp fromDate, Timestamp thruDate, Delegator delegator) throws GenericEntityException {
        RecurringEventVO recurringEventVO = eventVO.getRecurringEventVO();
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 3.0//EN"));
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        DateTime eventStartTime = new DateTime(eventVO.getStartDateTime().getTime());
        DateTime eventEndTime = new DateTime(eventVO.getEndDateTime().getTime()); // TODO: figure out right end-date to use
        String eventName = eventVO.getTitle();
        VEvent icsEvent = null;
        icsEvent = new VEvent(eventStartTime, eventEndTime, eventName);
        //adding rule
        RRule rrule = null;
        try {
            rrule = new RRule(generateRRule(recurringEventVO));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != rrule) {
            icsEvent.getProperties().add(rrule);
        }
        // Add the event to the calender
        icsCalendar.getComponents().add(icsEvent);
        DateTime eventEndDate = new DateTime(recurringEventVO.getEndDate());
        DateTime eventStartDate = new DateTime(recurringEventVO.getStartDate());
        Period period = new Period(eventStartDate, eventEndDate);
        PeriodList periodList = icsCalendar.getComponents().getComponent(Component.VEVENT).calculateRecurrenceSet(period);
        for (Object event : periodList) {
            Period p = (Period) event;
            Date startDate = new Date(p.getStart().getTime());
            SimpleDateFormat dateOnlySdf = new SimpleDateFormat("MM/dd/yyyy");
            String startDate_dateOnly = dateOnlySdf.format(startDate);
            SimpleDateFormat bothDateAndTime = new SimpleDateFormat("MM/dd/yyyy h:mm:ss");
            Date eventInstanceStartDateTime = null;
            try {
                eventInstanceStartDateTime = bothDateAndTime.parse(startDate_dateOnly + " 00:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Timestamp eventInstanceStartTime = new Timestamp(p.getStart().getTime());
            Timestamp startDateForExceptionCheck = new Timestamp(eventInstanceStartDateTime.getTime());
            if (eventInstanceStartTime.compareTo(fromDate) >= 0 && !isEventAnException(delegator, eventVO.getEventId(), startDateForExceptionCheck)) {
                EventVO e1 = buildRecurringEventInstance(eventVO);

                Debug.logInfo("Adding Event Occurrence for event Id " + e1.getEventId() + " for date " + eventInstanceStartTime, module);
                String pattern = "yyyyMMdd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String eventIdSuffix = simpleDateFormat.format(new Date(eventInstanceStartTime.getTime()));
                e1.setEventId(e1.getEventId() + "#" + eventIdSuffix);
                e1.setRecurringEventVO(null);
                e1.setStartDateTime(eventInstanceStartTime);
                e1.setEndDateTime(new Timestamp(p.getEnd().getTime()));
                events.add(e1);
            }
        }
    }

    public static boolean isEventAnException(Delegator delegator, String eventId, Timestamp onDate) throws GenericEntityException {
        Debug.log("Check for Exception for " + eventId + " onDate " + onDate, module);
        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("eventId", EntityOperator.EQUALS, eventId),
                EntityCondition.makeCondition("onDate", EntityOperator.EQUALS, onDate)),
                EntityOperator.AND);
        EntityCondition condition = EntityCondition.makeCondition(thruCond);
        List<GenericValue> exceptionEventGvsList = delegator.findList("ExceptionEvent", condition, null, null, null, false);
        Debug.logInfo("Exception Events found: " + exceptionEventGvsList, module);
        if (UtilValidate.isNotEmpty(exceptionEventGvsList)) {
            return true;
        }
        return false;
    }


    public static EventVO buildRecurringEventInstance(EventVO eventVO) {

        EventVO event = new EventVO();
        event.setEventId(eventVO.getEventId());
        event.setTitle(eventVO.getTitle());
        event.setType(eventVO.getType());
        event.setDescription(eventVO.getDescription());
        event.setEventLocation(eventVO.getEventLocation());
        event.setStartDateTime(eventVO.getStartDateTime());
        event.setEndDateTime(eventVO.getEndDateTime());
        event.setIsPrivateEvent(eventVO.getIsPrivateEvent());
        event.setIsRecurringEvent(eventVO.getIsRecurringEvent());
        event.setGuestList(eventVO.getGuestList());
        return event;
    }

    public static Map<String, Object> getEvent(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher localDispatcher = dctx.getDispatcher();
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        String eventId = (String) context.get("eventId");
        String academyId = (String) context.get("academyId");
        GenericValue eventGv = null ;
        try {
            eventGv = delegator.findOne("Event",
                    UtilMisc.toMap("eventId", eventId), false);
            if (UtilValidate.isNotEmpty(eventGv) && "N".equalsIgnoreCase(eventGv.getString("isDeleted"))) {
                sendResp.put("event", buildEvent(delegator, eventGv));
            } else {
                return ServiceUtil.returnFailure("Event " + eventId + " is not found ");
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to read event, error: " + e.getMessage());
        }
        List<GenericValue> activities = delegator.findAll("RecentActivity", false);
        String oldEventId = null;
        String oldEventAction = null;

        for (GenericValue activity : activities) {
            oldEventId = (String) activity.get("activityTypeId");
            oldEventAction = (String) activity.get("action");
        }
        if (eventId.equals(oldEventId) && oldEventAction.equals("VIEWED")) {
            System.out.println("should not create recent activity as it is already viewed");
        } else {
            try {
                eventRecentActivity(dctx, academyId, eventId, "VIEWED",eventGv.getString("eventName"));
                recentActivityViewDone = true;
            }
            catch (GenericEntityException e) {
                e.printStackTrace();
            }
        }

        return sendResp;
    }


    public static Map<String, Object> createEvent(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher localDispatcher = dctx.getDispatcher();
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        String newEventId = null;
        // 1. Add Event
        //eventId used for creation of recurring event update call
        String parentEventId = (String) context.get("parentEventId");
        String type = (String) context.get("type");
        if (UtilValidate.isEmpty(type))
            type = (String) context.get("eventTypeId");
        String title = (String) context.get("title");
        if (UtilValidate.isEmpty(title))
            title = (String) context.get("eventName");
        String description = (String) context.get("description");
        String location = (String) context.get("eventLocation");
        Timestamp startDateTime = (Timestamp) context.get("startDateTime");
        Timestamp endDateTime = (Timestamp) context.get("endDateTime");
        String isPrivateEvent = (String) context.get("isPrivateEvent");
        String isRecurringEvent = (String) context.get("isRecurringEvent");
        String notifyGuests = (String) context.get("notifyGuests");
        String academyId = (String) context.get("academyId");

        try {
            // DB Operations
            newEventId = delegator.getNextSeqId("Event");
            GenericValue event = delegator.makeValue("Event", UtilMisc.<String, Object>toMap(
                    "eventId", newEventId,
                    "eventTypeId", type,
                    "eventName", title,
                    "description", description,
                    "eventLocation", location,
                    "startDateTime", startDateTime,
                    "endDateTime", endDateTime,
                    "isPrivateEvent", isPrivateEvent,
                    "isRecurringEvent", isRecurringEvent,
                    "notifyGuests", notifyGuests,
                    "parentEventId", parentEventId,
                    "isDeleted", "N"));
            delegator.create(event);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to create Event, error: " + e.getMessage());
        }


        // 2. Add Recurring Event
        if ("Y".equalsIgnoreCase(isRecurringEvent)) {
            String recurringType = (String) context.get("recurringType");
            Timestamp startDate = (Timestamp) context.get("startDate");
            Timestamp endDate = (Timestamp) context.get("endDate");
            String until = (String) context.get("until");
            Long numberOfOccurrence = (Long) context.get("numberOfOccurrence");
            String onSunday = (String) context.get("onSunday");
            String onMonday = (String) context.get("onMonday");
            String onTuesday = (String) context.get("onTuesday");
            String onWednesday = (String) context.get("onWednesday");
            String onThursday = (String) context.get("onThursday");
            String onFriday = (String) context.get("onFriday");
            String onSaturday = (String) context.get("onSaturday");
            Long onMonthDay = (Long) context.get("onMonthDay");
            try {
                Map<String, Object> recurringEvent = UtilMisc.toMap(
                        "eventId", newEventId,
                        "recurringType", recurringType,
                        "until", until,
                        "numberOfOccurrence", numberOfOccurrence,
                        "fromDate", startDate,
                        "thruDate", endDate,
                        "onSunday", onSunday,
                        "onMonday", onMonday,
                        "onTuesday", onTuesday,
                        "onWednesday", onWednesday,
                        "onThursday", onThursday,
                        "onFriday", onFriday,
                        "onSaturday", onSaturday,
                        "onMonthDay", onMonthDay
                );
                GenericValue recurringEventGvs = delegator.makeValue("RecurringEvent", recurringEvent);
                delegator.create(recurringEventGvs);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to create recurring event, error: " + e.getMessage());
            }
        }

        // 3. Add Event Role
        try {
            ArrayList<String> guestList = (ArrayList<String>) context.get("guestList");
            Map<String, Object> eventRole = UtilMisc.toMap(
                    "eventId", newEventId,
                    "roleTypeId", "EVENT_ORGANIZER"
            );
            eventRole.put("partyId", academyId);
            GenericValue eventRoleGvs = delegator.makeValue("EventRole", eventRole);
            delegator.create(eventRoleGvs);
            if (!UtilValidate.isEmpty(guestList)) {
                for (String guestId : guestList) {
                    eventRole.put("partyId", guestId);
                    eventRole.put("roleTypeId", "EVENT_ATTENDEE");
                    eventRoleGvs = delegator.makeValue("EventRole", eventRole);
                    delegator.create(eventRoleGvs);
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to create Event Role, error: " + e.getMessage());
        }
        if (UtilValidate.isNotEmpty(newEventId)) {
            eventRecentActivity(dctx, academyId, newEventId, "CREATED",title);
        }

        //TODO 4. Notify Guests about event (Mail / SMS)

        sendResp.put("eventId", newEventId);
        return sendResp;
    }

    public static EventVO buildEvent(Delegator delegator, GenericValue value) throws GenericEntityException {
        EventVO event = new EventVO();
        event.setEventId(value.getString("eventId"));
        event.setTitle(value.getString("eventName"));
        event.setType(value.getString("eventTypeId"));
        event.setDescription(value.getString("description"));
        event.setEventLocation(value.getString("eventLocation"));
        event.setStartDateTime(value.getTimestamp("startDateTime"));
        event.setEndDateTime(value.getTimestamp("endDateTime"));
        event.setIsPrivateEvent(value.getString("isPrivateEvent"));
        event.setParentEventId(value.getString("parentEventId"));
        String isRecurringEvent = value.getString("isRecurringEvent");
        event.setIsRecurringEvent(isRecurringEvent);
        if ("Y".equalsIgnoreCase(isRecurringEvent) && UtilValidate.isEmpty(value.getString("parentEventId"))) {
            EntityCondition condition = EntityCondition.makeCondition("eventId", event.getEventId());
            GenericValue recurringEventGvs = EntityQuery.use(delegator).from("RecurringEvent").where(condition).queryOne();
            event.setRecurringEventVO(buildRecurringEvent(recurringEventGvs));
        }
        List<GenericValue> eventRolesByEventIdGvs = delegator.findByAnd("EventRole", UtilMisc.toMap("eventId", event.getEventId()), null, false);
        if (UtilValidate.isNotEmpty(eventRolesByEventIdGvs)) {
            ArrayList<String> guestList = new ArrayList<>();
            eventRolesByEventIdGvs.forEach(eventRole -> {
                if ("EVENT_ATTENDEE".equalsIgnoreCase((String) eventRole.get("roleTypeId"))) {
                    guestList.add((String) eventRole.get("partyId"));
                }
            });
            event.setGuestList(guestList);
        }
        return event;
    }

    public static RecurringEventVO buildRecurringEvent(GenericValue value) {
        RecurringEventVO recurringEventVO = new RecurringEventVO();
        recurringEventVO.setRecurringType(value.getString("recurringType"));
        recurringEventVO.setUntil(value.getString("until"));
        recurringEventVO.setNumberOfOccurrence(value.getLong("numberOfOccurrence"));
        recurringEventVO.setOnSunday(value.getString("onSunday"));
        recurringEventVO.setOnMonday(value.getString("onMonday"));
        recurringEventVO.setOnTuesday(value.getString("onTuesday"));
        recurringEventVO.setOnWednesday(value.getString("onWednesday"));
        recurringEventVO.setOnThursday(value.getString("onThursday"));
        recurringEventVO.setOnFriday(value.getString("onFriday"));
        recurringEventVO.setOnSaturday(value.getString("onSaturday"));
        recurringEventVO.setOnMonthDay(value.getLong("onMonthDay"));
        recurringEventVO.setStartDate(value.getTimestamp("fromDate"));
        recurringEventVO.setEndDate(value.getTimestamp("thruDate"));
        return recurringEventVO;
    }

    public static String generateRRule(RecurringEventVO recEvtVO) {
        StringBuilder rrule = new StringBuilder("FREQ=");
        String recurringType = recEvtVO.getRecurringType();
        rrule.append(recurringType).append(";");
        boolean isDailyEvent = ("DAILY").equalsIgnoreCase(recurringType);
        if (!isDailyEvent) {
            boolean isWeeklyEvent = ("WEEKLY").equalsIgnoreCase(recurringType);
            if (isWeeklyEvent) {
                rrule.append("BYDAY=");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnMonday()))
                    rrule.append("MO").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnTuesday()))
                    rrule.append("TU").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnWednesday()))
                    rrule.append("WE").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnThursday()))
                    rrule.append("TH").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnFriday()))
                    rrule.append("FR").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnSaturday()))
                    rrule.append("SA").append(",");
                if ("Y".equalsIgnoreCase(recEvtVO.getOnSunday()))
                    rrule.append("SU").append(",");

                rrule.setLength(rrule.length() - 1);
                rrule.append(";");
            } else {
                //Monthly
                rrule.append("BYMONTHDAY=").append(recEvtVO.getOnMonthDay()).append(";");
            }
        }
        //FOR_EVER, TILL_DATE, OCCURRENCE
        String until = recEvtVO.getUntil();
        if ("OCCURRENCE".equalsIgnoreCase(until)) {
            rrule.append("COUNT=").append(recEvtVO.getNumberOfOccurrence()).append(";");
        } else if ("TILL_DATE".equalsIgnoreCase(until)) {
            Timestamp untilEndDate = recEvtVO.getEndDate();
            String pattern = "yyyyMMdd'T'HHmmss'Z'"; //UNTIL FORMAT: 19970902T170000Z
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String untilDateString = simpleDateFormat.format(new Date(untilEndDate.getTime()));
            System.out.println("Recurring UNTIL : " + untilDateString);
            rrule.append("UNTIL=").append(untilDateString).append(";");
        }
        rrule.append("INTERVAL=1;");
        return rrule.toString().toUpperCase();
    }

    public static Map<String, Object> deleteEvent(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher localDispatcher = dctx.getDispatcher();
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");
        String inputEventId = (String) context.get("eventId");
        String eventId = null;
        Timestamp onDate = null;
        if (inputEventId.contains("#")) {
            String[] array = inputEventId.split("#");
            eventId = array[0];
            String eventPrefix = array[1];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try {
                Date eventOnDate = formatter.parse(eventPrefix);
                onDate = new Timestamp(eventOnDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //onDate = new Timestamp(Long.parseLong(array[1]));
        } else {
            eventId = inputEventId;
        }
        Debug.logInfo("Deleting Event for eventId: " + eventId, module);
        Debug.logInfo("Deleting event instance for onDate: " + onDate, module);
        GenericValue eventGvs = null ;
        try {
            eventGvs = delegator.findOne("Event", UtilMisc.toMap("eventId", eventId), false);
            Debug.logInfo("Event GV to Delete: " + eventGvs, module);
            if (UtilValidate.isNotEmpty(eventGvs) && "N".equalsIgnoreCase(eventGvs.getString("isDeleted"))) {
                if (UtilValidate.isNotEmpty(onDate)) {
                    Map<String, Object> exceptionEvent = UtilMisc.toMap(
                            "eventId", eventId,
                            "onDate", onDate);
                    if (UtilValidate.isEmpty(delegator.findOne("ExceptionEvent", exceptionEvent, false))) {
                        exceptionEvent.put("action", "DELETE");
                        delegator.create(delegator.makeValue("ExceptionEvent", exceptionEvent));
                    } else {
                        return ServiceUtil.returnFailure(" Event " + inputEventId + " is not found: ");
                    }
                } else {
                    eventGvs.put("isDeleted", "Y");
                    delegator.store(eventGvs);
                }
            } else {
                return ServiceUtil.returnFailure(" Event " + inputEventId + " is not found ");
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to delete event, error: " + e.getMessage());
        }
        List<GenericValue> activities = delegator.findAll("RecentActivity", false);
        String lastActivityId = null;
        for (GenericValue activity : activities) {
            lastActivityId = (String) activity.get("activityId");
        }
        try {
            if (recentActivityViewDone) {
                GenericValue recentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", lastActivityId), false);
                recentActivityGenValue.remove();
                eventRecentActivity(dctx, academyId, inputEventId, "DELETED",eventGvs.getString("eventName"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put(ModelService.RESPONSE_MESSAGE, "Event " + inputEventId + " is deleted");
        return sendResp;
    }

    public static Map<String, Object> updateEvent(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher localDispatcher = dctx.getDispatcher();
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        String academyId = (String) context.get("academyId");
        String inputEventId = (String) context.get("eventId");
        Debug.log("Event Id Input for updating: " + inputEventId);
        String eventId = null;
        Timestamp onDate = null;
        if (inputEventId.contains("#")) {
            String[] array = inputEventId.split("#");
            eventId = array[0];
            String eventPrefix = array[1];
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            try {
                Date eventOnDate = formatter.parse(eventPrefix);
                onDate = new Timestamp(eventOnDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //onDate = new Timestamp(Long.parseLong(array[1]));
        } else {
            eventId = inputEventId;
        }
        Debug.log("Updating Event for eventId: " + eventId);
        Debug.log("onDate value for update is: " + onDate);

        GenericValue eventGv = null, excEventGvs = null, recurringEventGv = null;
        List<GenericValue> eventRolesByEventIdGvs = null;
        try {
            eventGv = delegator.findOne("Event", UtilMisc.toMap("eventId", eventId), false);
            Debug.logInfo("Event GV to update: " + eventGv, module);
            if (UtilValidate.isNotEmpty(eventGv) && "N".equalsIgnoreCase(eventGv.getString("isDeleted"))) {
                if (UtilValidate.isNotEmpty(onDate)) {
                    Debug.log("Updating a particular occurance: " + onDate);

                    Map<String, Object> exceptionEventMap = UtilMisc.toMap(
                            "eventId", eventId,
                            "onDate", onDate);
                    excEventGvs = delegator.findOne("ExceptionEvent", exceptionEventMap, false);
                    if (UtilValidate.isEmpty(excEventGvs)) {
                        exceptionEventMap.put("action", "UPDATE");
                        delegator.create(delegator.makeValue("ExceptionEvent", exceptionEventMap));
                        Map<String, Object> updateRecurrinGEvent = UtilMisc.toMap();
                        updateRecurrinGEvent.putAll(eventGv);
                        updateRecurrinGEvent = prepareEventForUpdate(context);
                        eventRolesByEventIdGvs = delegator.findByAnd("EventRole", UtilMisc.toMap("eventId", eventId, "roleTypeId", "EVENT_ATTENDEE"), null, false);
                        if (UtilValidate.isNotEmpty(eventRolesByEventIdGvs)) {
                            List<String> guestList = new ArrayList<>();
                            eventRolesByEventIdGvs.forEach(eventRole -> {
                                guestList.add((String) eventRole.get("partyId"));
                            });
                            updateRecurrinGEvent.put("guestList", guestList);
                        }
                        updateRecurrinGEvent.put("isRecurringEvent", "N");
                        updateRecurrinGEvent.put("academyId", academyId);
                        updateRecurrinGEvent.put("parentEventId", eventId);
                        sendResp = createEvent(dctx, updateRecurrinGEvent);
                    } else {
                        return ServiceUtil.returnFailure(" Event " + inputEventId + " is not found: ");
                    }
                } else {
                    eventGv.putAll(prepareEventForUpdate(context));
                    delegator.store(eventGv);
                    Debug.log("Is updating event isRecurringEvent: " + eventGv.getString("isRecurringEvent"));
                    if ("Y".equalsIgnoreCase(eventGv.getString("isRecurringEvent"))) {
                        // Update/create recurring event info...
                        recurringEventGv = delegator.findOne("RecurringEvent", UtilMisc.toMap("eventId", eventId), false);
                        recurringEventGv.putAll(prepareRecurringEventForUpdate(context));
                        delegator.createOrStore(recurringEventGv);
                    }
                    //Update Event_Role
                    //if eventRolesByEventIdGvs is not empty and need to update/ remove attendees need to get "guestList" key in request and
                    // value should be empty. if "guestList" key not found in request no need to preform any operations on event roles
                    if (context.keySet().contains("guestList")) {
                        ArrayList<String> guestList = (ArrayList<String>) context.get("guestList");
                        eventRolesByEventIdGvs = delegator.findByAnd("EventRole", UtilMisc.toMap("eventId", eventId, "roleTypeId", "EVENT_ATTENDEE"), null, false);
                        if (UtilValidate.isNotEmpty(eventRolesByEventIdGvs)) {
                            eventRolesByEventIdGvs.forEach(eventRole -> {
                                try {
                                    if (UtilValidate.isEmpty(guestList)) {
                                        eventRole.remove();
                                    } else if (!guestList.contains(eventRole.getString("partyId"))) {
                                        eventRole.remove();
                                    } else {
                                        guestList.remove(eventRole.getString("partyId"));
                                    }
                                } catch (GenericEntityException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        String finalEventId = eventId;
                        guestList.forEach(partyId -> {
                            // create a event role
                            Map<String, Object> eventRoleMap = UtilMisc.toMap(
                                    "eventId", finalEventId,
                                    "roleTypeId", "EVENT_ATTENDEE",
                                    "partyId", partyId
                            );
                            try {
                                delegator.create("EventRole", eventRoleMap);
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            }

                        });
                    }
                }
            } else {
                return ServiceUtil.returnFailure(" Event " + inputEventId + " is not found");
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to update event, error: " + e.getMessage());
        }
        if (UtilValidate.isEmpty(sendResp.get("eventId"))) {
            sendResp.put(ModelService.RESPONSE_MESSAGE, "Event " + inputEventId + " updated successfully");
        } else {
            sendResp.put(ModelService.RESPONSE_MESSAGE, "new event " + sendResp.get("eventId") + " created successfully");
            sendResp.remove("eventId");
        }
        List<GenericValue> activities = delegator.findAll("RecentActivity", false);
        String lastActivityId = null;
        for (GenericValue activity : activities) {
            lastActivityId = (String) activity.get("activityId");
        }
        try {
            if (recentActivityViewDone) {
                GenericValue recentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", lastActivityId), false);
                recentActivityGenValue.remove();
                eventRecentActivity(dctx, academyId, eventId, "UPDATED",eventGv.getString("eventName"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }

    //Method will return a map, that map contains all fields that are required for create new event or update a existing event
    public static Map<String, Object> prepareEventForUpdate(Map<String, ? extends Object> input) {

        Map<String, Object> genericValue = UtilMisc.toMap();
        String eventId = (String) input.get("eventId");
        String eventName = (String) input.get("title");
        String description = (String) input.get("description");
        Timestamp startDateTime = (Timestamp) input.get("startDateTime");
        Timestamp endDateTime = (Timestamp) input.get("endDateTime");
        String isPrivateEvent = (String) input.get("isPrivateEvent");
        String isRecurringEvent = (String) input.get("isRecurringEvent");
        String eventTypeId = (String) input.get("type");
        String eventLocation = (String) input.get("eventLocation");
        String notifyGuests = (String) input.get("notifyGuests");
        String parentEventId = (String) input.get("parentEventId");

        if (UtilValidate.isNotEmpty(eventId)) {
            genericValue.put("eventId", eventId);
        }
        if (UtilValidate.isNotEmpty(eventName)) {
            genericValue.put("eventName", eventName);
        }
        if (UtilValidate.isNotEmpty(eventTypeId)) {
            genericValue.put("eventTypeId", eventTypeId);
        }
        if (UtilValidate.isNotEmpty(description)) {
            genericValue.put("description", description);
        }
        if (UtilValidate.isNotEmpty(eventLocation)) {
            genericValue.put("eventLocation", eventLocation);
        }
        if (UtilValidate.isNotEmpty(startDateTime)) {
            genericValue.put("startDateTime", startDateTime);
        }
        if (UtilValidate.isNotEmpty(startDateTime)) {
            genericValue.put("endDateTime", endDateTime);
        }
        if (UtilValidate.isNotEmpty(isPrivateEvent)) {
            genericValue.put("isPrivateEvent", isPrivateEvent);
        }
        if (UtilValidate.isNotEmpty(isRecurringEvent)) {
            genericValue.put("isRecurringEvent", isRecurringEvent);
        }
        if (UtilValidate.isNotEmpty(notifyGuests)) {
            genericValue.put("notifyGuests", notifyGuests);
        }
        if (UtilValidate.isNotEmpty(parentEventId)) {
            genericValue.put("parentEventId", parentEventId);
        }
        return genericValue;
    }

    //Method will return a map, that map contains all fields that are required for update a existing recurring event
    public static Map<String, Object> prepareRecurringEventForUpdate(Map<String, ? extends Object> input) {
        Map<String, Object> genericValue = UtilMisc.toMap();
        String eventId = (String) input.get("eventId");
        String recurringType = (String) input.get("recurringType");
        String until = (String) input.get("until");
        Long numberOfOccurrence = (Long) input.get("numberOfOccurrence");
        String onSunday = (String) input.get("onSunday");
        String onMonday = (String) input.get("onMonday");
        String onTuesday = (String) input.get("onTuesday");
        String onWednesday = (String) input.get("onWednesday");
        String onThursday = (String) input.get("onThursday");
        String onFriday = (String) input.get("onFriday");
        String onSaturday = (String) input.get("onSaturday");
        Long onMonthDay = (Long) input.get("onMonthDay");
        Timestamp fromDate = (Timestamp) input.get("startDate");
        Timestamp thruDate = (Timestamp) input.get("endDate");

        if (UtilValidate.isNotEmpty(eventId)) {
            genericValue.put("eventId", eventId);
        }
        if (UtilValidate.isNotEmpty(recurringType)) {
            genericValue.put("recurringType", recurringType);
        }
        if (UtilValidate.isNotEmpty(until)) {
            genericValue.put("until", until);
        }
        if (UtilValidate.isNotEmpty(numberOfOccurrence)) {
            genericValue.put("numberOfOccurrence", numberOfOccurrence);
        }
        if (UtilValidate.isNotEmpty(onSunday)) {
            genericValue.put("onSunday", onSunday);
        }
        if (UtilValidate.isNotEmpty(onMonday)) {
            genericValue.put("onMonday", onMonday);
        }
        if (UtilValidate.isNotEmpty(onTuesday)) {
            genericValue.put("onTuesday", onTuesday);
        }
        if (UtilValidate.isNotEmpty(onWednesday)) {
            genericValue.put("onWednesday", onWednesday);
        }
        if (UtilValidate.isNotEmpty(onThursday)) {
            genericValue.put("onThursday", onThursday);
        }
        if (UtilValidate.isNotEmpty(onFriday)) {
            genericValue.put("onFriday", onFriday);
        }
        if (UtilValidate.isNotEmpty(onSaturday)) {
            genericValue.put("onSaturday", onSaturday);
        }
        if (UtilValidate.isNotEmpty(onMonthDay)) {
            genericValue.put("onMonthDay", onMonthDay);
        }
        if (UtilValidate.isNotEmpty(fromDate)) {
            genericValue.put("fromDate", fromDate);
        }
        if (UtilValidate.isNotEmpty(thruDate)) {
            genericValue.put("thruDate", thruDate);
        }
        return genericValue;
    }

    public static Map<String, Object> getUpcomingEventSize(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String academyId = (String) context.get("academyId");
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        Timestamp startDate = new Timestamp(new Date().getTime());
        Date endDateVal = new Date();
        endDateVal.setYear(endDateVal.getYear() + 1);//end Date set to plus one year from current Date
        Timestamp endDate = new Timestamp(endDateVal.getTime());

        List<EntityCondition> condList = new LinkedList<EntityCondition>();
        condList.add(EntityCondition.makeCondition("partyId", academyId));
        condList.add(EntityCondition.makeCondition("isDeleted", "N"));

        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("endDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, startDate),
                EntityCondition.makeCondition("endDateTime", EntityOperator.LESS_THAN_EQUAL_TO, endDate)),
                EntityOperator.AND);
        condList.add(thruCond);
        EntityCondition condition = EntityCondition.makeCondition(condList);
        Map<String, Object> allEvent = new HashMap<>();
        try {
            List<GenericValue> eventAndEventRole = EntityQuery.use(delegator).from("EventAndEventRole").where(condition).queryList();
            if (UtilValidate.isNotEmpty(eventAndEventRole)) {
                allEvent.put("upcomingEventsSize", eventAndEventRole.size());
            } else {
                allEvent.put("upcomingEventsSize", "0");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("events", allEvent);
        return sendResp;
    }

    public static Map<String, Object> eventRecentActivity(DispatchContext dispatchContext, String academyId, String eventId, String action, String activityTypeInfo) throws GenericEntityException {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Map sendResponse = ServiceUtil.returnSuccess();
        Map<String, Object> createRecentActivity = new HashMap<>();
        Date now = new Date();
        Timestamp activityCreatedDate = new Timestamp(now.getTime());
        createRecentActivity.put("academyId", academyId);
        createRecentActivity.put("activityType", "EVENT");
        createRecentActivity.put("activityTypeId", eventId);
        createRecentActivity.put("activityTypeInfo", activityTypeInfo);
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
