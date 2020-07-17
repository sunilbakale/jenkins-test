import com.cth.academy.model.EventVO
import com.sun.org.apache.xpath.internal.objects.XObject
import org.apache.ofbiz.base.util.UtilDateTime
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.party.contact.ContactMechWorker

import java.sql.Timestamp
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

String studentId = request.getParameter("student_id")
context.studentId = studentId
def studentInfo = [:]
studentInfo.studentId = studentId
def studentParty = delegator.findOne("Party", UtilMisc.toMap("partyId", studentId), false);
if (UtilValidate.isNotEmpty(studentParty)) {
    def studentPerson = delegator.findOne("Person", UtilMisc.toMap("partyId", studentId), false)
    studentInfo.firstName = studentPerson.firstName;
    studentInfo.lastName = studentPerson.lastName;

    List<Map<String, Object>> contactMechs = ContactMechWorker.getPartyContactMechValueMaps(delegator, studentId, false);
    for (Map<String, Object> contactMechEntry : contactMechs) {
        def contactMechGv = contactMechEntry.get("contactMech");
        def contactMechType = contactMechGv.contactMechTypeId

        if (contactMechType == "EMAIL_ADDRESS") {
            studentInfo.emailAddress = contactMechGv.infoString
        } else if (contactMechType == "TELECOM_NUMBER") {
            def telecomGv = contactMechEntry.telecomNumber
            studentInfo.mobile = telecomGv.contactNumber
        }
    }
}
context.studentInfo = studentInfo;

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def students = studentsResponse.get("students");
context.students = students

def preferredCurrency = [:]
def preferred_currency_info = dispatcher.runSync("readAcademyPartyAttrCurrencyInfo",["partyId":academyId,"attrName":"preferred_currency"])
def preferredCurrencyMap = preferred_currency_info.get("partyAttrCurrencyInfo")
def currencyTypeVal = preferredCurrencyMap.get("attrValue")
preferredCurrency.currencyType = currencyTypeVal
context.preferredCurrency = preferredCurrency

Timestamp now = UtilDateTime.nowTimestamp()
Timestamp tillDateTs = UtilDateTime.addDaysToTimestamp(now, 30);

Map<String, Object> getEventInputMap = UtilMisc.toMap("partyId", studentId);
getEventInputMap.put("startDate", now);
getEventInputMap.put("thruDate", tillDateTs);

def getEventByPartyId = dispatcher.runSync("getEvents", getEventInputMap)
List<EventVO> eventsList = (List) getEventByPartyId.get("events");

List<Map<String, Object>> eventsToReturn = new ArrayList<>();
int threshold = 10, count = 0;

if (UtilValidate.isNotEmpty(eventsList)) {
    Collections.sort(eventsList)

    for (EventVO event : eventsList) {
        Map<String, Object> eventEntry = new HashMap<>();
        eventEntry.put("eventId", event.getEventId());
        eventEntry.put("title", event.getTitle());
        eventEntry.put("start", event.getStartDateTime().getTime());
        eventEntry.put("end", event.getEndDateTime().getTime());
        eventEntry.put("description", event.getDescription());
        String location = event.getEventLocation();
        if ("ONLINE".equalsIgnoreCase(location)) location = "Online"
        if ("CLASS".equalsIgnoreCase(location)) location = "Center"
        if ("HOME".equalsIgnoreCase(location)) location = "Home"

        eventEntry.put("location", location);
        eventsToReturn.add(eventEntry);
        count++;
        if (count > threshold) break;
    }

}
context.studentEvents = eventsToReturn

Map<String, Object> invoiceInputMap = UtilMisc.toMap("academyId", academyId);
invoiceInputMap.put("userLogin", userLogin);
ArrayList<String> statusList = ["INVOICE_IN_PROCESS"]
invoiceInputMap.put("statusList", statusList);
invoiceInputMap.put("studentId", studentId);
def invoicesResp = dispatcher.runSync("fetchAcademyInvoice", invoiceInputMap)
def invoices = invoicesResp.get("invoices");
context.invoices = invoices;