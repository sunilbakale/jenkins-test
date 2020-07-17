import com.cth.academy.model.EventVO
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.service.GenericServiceException
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;
HttpSession  session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

String startCalDate = request.getParameter("start");
def startDateInput = Date.parse("yyyy-MM-dd", startCalDate).toTimestamp()

String endCalDate = request.getParameter("end");
def endDateInput = Date.parse("yyyy-MM-dd",endCalDate).toTimestamp()

print endDateInput
Map<String, Object> getEventInputMap = UtilMisc.toMap("partyId", academyId);
getEventInputMap.put("startDate", startDateInput);
getEventInputMap.put("thruDate", endDateInput);

Map<String, Object> getEventsResponse = null;
try {
    getEventsResponse = dispatcher.runSync("getEvents", UtilMisc.toMap(getEventInputMap));
} catch (GenericServiceException e) {
    e.printStackTrace();
    request.setAttribute("_ERROR_MESSAGE_", "Error getting events");
    return "error";
}
List<EventVO> eventsList = (List) getEventsResponse.get("events");
println ">>Event List from FectchCalendarEventsAjax.groovy: "+eventsList
List<Map<String,Object>> eventsToReturn = new ArrayList<>();

for (EventVO event : eventsList) {
    Map<String,Object> eventEntry = new HashMap<>();
    eventEntry.put("eventId",event.getEventId());
    eventEntry.put("title", event.getTitle());
    eventEntry.put("start", event.getStartDateTime().getTime());
    eventEntry.put("end", event.getEndDateTime().getTime());
    eventEntry.put("description",event.getDescription()!=null?event.getDescription():"");
    eventsToReturn.add(eventEntry);
}

ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), eventsToReturn );
