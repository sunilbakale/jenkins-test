import com.fasterxml.jackson.databind.ObjectMapper

import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;
HttpSession  session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

String inputEventId = request.getParameter("inputEventId");
println "inputEventId"+inputEventId;
String eventId = null;
Timestamp onDate = null;

boolean isRecurringInstance = false;
if(inputEventId.contains("#")){
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
    isRecurringInstance = true;
}else {
    eventId = inputEventId;
}
println("Fetching Event for eventId: "+eventId);
println("onDate value for fetch is: "+onDate);

def eventResponse = dispatcher.runSync("getEvent", ["academyId": academyId,"eventId":eventId])
def event = eventResponse.get("event");
if(isRecurringInstance) {
    Timestamp startDateTime = event.getStartDateTime();
    Timestamp endDateTime = event.getEndDateTime();

    // Use Date From Instance prefix
    SimpleDateFormat dateOnlySdf = new SimpleDateFormat("MM/dd/yyyy");
    String eventInstanceDateOnly = dateOnlySdf.format(new Date(onDate.getTime()));

    // Use Time from Event's Start/End datetime field
    SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
    String startTimeOnly = sdf.format(new Date(startDateTime.getTime()));
    String endTimeOnly = sdf.format(new Date(endDateTime.getTime()));

    // Combine both to get instance's start date and time
    SimpleDateFormat bothDateAndTime = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
    Date eventInstanceStartDateTime = bothDateAndTime.parse(eventInstanceDateOnly + " " + startTimeOnly);
    Date eventInstanceEndDateTime = bothDateAndTime.parse(eventInstanceDateOnly + " " + endTimeOnly);

    event.setStartDateTime(new Timestamp(eventInstanceStartDateTime.getTime()))
    event.setEndDateTime(new Timestamp(eventInstanceEndDateTime.getTime()))
}
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), event);

