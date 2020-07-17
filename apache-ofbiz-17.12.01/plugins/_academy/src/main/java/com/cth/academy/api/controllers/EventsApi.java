package com.cth.academy.api.controllers;

import com.cth.academy.api.filters.SecuredEndpoint;
import com.cth.academy.api.filters.ValidEventOperation;
import com.cth.academy.api.filters.ValidSubscriptionPlan;
import com.cth.academy.model.EventVO;
import com.cth.academy.model.RecurringEventVO;
import com.cth.academy.model.SearchEventVO;
import com.cth.academy.model.StudentVO;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/academy/{academy}/events")
public class EventsApi implements BaseApi {
    public final static String module = EventsApi.class.getName();
    @PathParam("academy") String academyId;

    /**
     * Making searchEvents as POST instead of traditional GET to get search parameters in body
     * */
    @POST
    @Path("search")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<EventVO> searchEvents(SearchEventVO searchEventVO) throws Exception {
        GenericValue userLogin = getUserLogin(delegator, "system"); // TODO: use logged in user
        List<EventVO> events = null;
        try {
            // TODO: Process input params - convert date-str to Timestamp use Date-format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Map<String,Object> result = dispatcher.runSync("getEvents",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "startDate", searchEventVO.getStartDate(),
                            "thruDate", searchEventVO.getEndDate(),
                            "isPrivateEvent", searchEventVO.getIsPrivateEvent(),
                            "userLogin", userLogin));
            if(!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to find events: " + result);
            }
            events = (List<EventVO>) result.get("events");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to find events: " + e.getMessage());
        }
        return events;
    }

    @GET
    @Path("/{eventId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getEventDetail(@PathParam("eventId") String eventId) throws Exception {
        System.out.println("Fetching details for eventId " + eventId);
        Map<String,Object> result  = null;
        GenericValue userLogin = getUserLogin(delegator, "system"); // TODO: use logged in user
        try {
            result = dispatcher.runSync("getEvent", UtilMisc.toMap("academyId", academyId,
                    "eventId", eventId, "userLogin", userLogin));
            if(!ServiceUtil.isSuccess(result)) {
                    throw new Exception((String) result.get(ModelService.RESPONSE_MESSAGE));
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to find event: " + e.getMessage());
        }
        return Response.ok((EventVO) result.get("event")).status(Response.Status.OK).build();
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getAllEvents(EventVO eventVO) throws Exception {
        Map<String,Object> result  = null;
        GenericValue userLogin = getUserLogin(delegator, "system"); // TODO: use logged in user
        try {
            result = dispatcher.runSync("getEvents", UtilMisc.toMap("academyId", academyId,
                     "userLogin", userLogin,
                    "startDate", eventVO.getStartDateTime(),
                    "thruDate", eventVO.getEndDateTime(),
                    "isPrivateEvent", eventVO.getIsPrivateEvent()));
            if(!ServiceUtil.isSuccess(result)) {
                throw new Exception((String) result.get(ModelService.RESPONSE_MESSAGE));
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to find event: " + e.getMessage());
        }
        return Response.ok((EventVO) result.get("events")).status(Response.Status.OK).build();
    }


    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//    @ValidSubscriptionPlan
//    @ValidEventOperation
    @SecuredEndpoint
    public Response createEvent(EventVO eventVO) throws Exception {
        Map<String,Object> result  = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            RecurringEventVO recEventVO = eventVO.getRecurringEventVO();
            result = dispatcher.runSync("createEvent",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "type", eventVO.getType(),
                            "title", eventVO.getTitle(),
                            "description", eventVO.getDescription(),
                            "eventLocation", eventVO.getEventLocation(),
                            "startDateTime", eventVO.getStartDateTime(),
                            "endDateTime", eventVO.getEndDateTime(),
                            "isPrivateEvent", eventVO.getIsPrivateEvent(),
                            "isRecurringEvent", eventVO.getIsRecurringEvent(),
                            "recurringType", recEventVO.getRecurringType(),
                            "onSunday", recEventVO.getOnSunday(),
                            "onMonday", recEventVO.getOnMonday(),
                            "onTuesday", recEventVO.getOnTuesday(),
                            "onWednesday", recEventVO.getOnWednesday(),
                            "onThursday", recEventVO.getOnThursday(),
                            "onFriday", recEventVO.getOnFriday(),
                            "onSaturday", recEventVO.getOnSaturday(),
                            "onMonthDay", recEventVO.getOnMonthDay(),
                            "notifyGuests", eventVO.getNotifyGuests(),
                            "guestList", eventVO.getGuestList(),
                            "startDate", recEventVO.getStartDate(),
                            "endDate", recEventVO.getEndDate(),
                            "until", recEventVO.getUntil(),
                            "numberOfOccurrence", recEventVO.getNumberOfOccurrence(),
                            "userLogin", userLogin));
            if(!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to create event: " + result);
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to create event: " + e.getMessage());
        }
        return Response.ok(UtilMisc.<String, Object>toMap("eventId", (String) result.get("eventId"))).status(Response.Status.CREATED).build();
    }


    @DELETE
    @Path("/{eventId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response deleteEvent(@PathParam("eventId") String eventId) throws Exception {
        Map<String,Object> result  = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            result = dispatcher.runSync("deleteEvent", UtilMisc.toMap("academyId", academyId,
                        "eventId", eventId,
                        "userLogin", userLogin));
        } catch (GenericServiceException e) {
            throw new Exception("Unable to delete event: " + e.getMessage());
        }
        return Response.ok(result).status(Response.Status.OK).build();

    }

    @PUT
    @Path("/{eventId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateEvent(@PathParam("eventId") String eventId, EventVO eventVO) throws Exception {

        Map<String,Object> result  = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            RecurringEventVO recEventVO = eventVO.getRecurringEventVO();
            if(UtilValidate.isEmpty(recEventVO)){
                recEventVO = new RecurringEventVO();
            }
            result = dispatcher.runSync("updateEvent",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "eventId", eventId,
                            "type", eventVO.getType(),
                            "title", eventVO.getTitle(),
                            "description", eventVO.getDescription(),
                            "eventLocation", eventVO.getEventLocation(),
                            "startDateTime", eventVO.getStartDateTime(),
                            "endDateTime", eventVO.getEndDateTime(),
                            "isPrivateEvent", eventVO.getIsPrivateEvent(),
                            "isRecurringEvent", eventVO.getIsRecurringEvent(),
                            "recurringType", recEventVO.getRecurringType(),
                            "onSunday", recEventVO.getOnSunday(),
                            "onMonday", recEventVO.getOnMonday(),
                            "onTuesday", recEventVO.getOnTuesday(),
                            "onWednesday", recEventVO.getOnWednesday(),
                            "onThursday", recEventVO.getOnThursday(),
                            "onFriday", recEventVO.getOnFriday(),
                            "onSaturday", recEventVO.getOnSaturday(),
                            "onMonthDay", recEventVO.getOnMonthDay(),
                            "notifyGuests", eventVO.getNotifyGuests(),
                            "guestList", eventVO.getGuestList(),
                            "startDate", recEventVO.getStartDate(),
                            "endDate", recEventVO.getEndDate(),
                            "until", recEventVO.getUntil(),
                            "numberOfOccurrence", recEventVO.getNumberOfOccurrence(),
                            "userLogin", userLogin));
            if(!ServiceUtil.isSuccess(result)) {
                return Response.ok(result).status(Response.Status.BAD_REQUEST).build();
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to update event: " + e.getMessage());
        }
        return Response.ok(result).status(Response.Status.OK).build();
    }

}
