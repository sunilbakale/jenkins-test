import com.cth.academy.model.EventVO
import com.cth.academy.utils.UserLoginUtils
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.service.GenericServiceException
import org.apache.ofbiz.entity.util.EntityQuery
import org.apache.ofbiz.entity.condition.EntityCondition

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue
import org.ocpsoft.prettytime.PrettyTime
HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def preferredCurrency = [:]
def preferred_currency_info = dispatcher.runSync("readAcademyPartyAttrCurrencyInfo",["partyId":academyId,"attrName":"preferred_currency"])
def preferredCurrencyMap = preferred_currency_info.get("partyAttrCurrencyInfo")
def currencyTypeVal = preferredCurrencyMap.get("attrValue")
preferredCurrency.currencyType = currencyTypeVal
context.preferredCurrency = preferredCurrency

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def students = studentsResponse.get("students");

context.students = students

def Resp = dispatcher.runSync("fetchAcademyInvoice", ["academyId":academyId,"userLogin":UserLoginUtils.getSystemUserLogin(delegator)])
def  invoices = Resp.get("invoices");
context.invoices = invoices;

def upcomingEvents = dispatcher.runSync("getUpcomingEventSize",["academyId":academyId])
def upcomingEventsInfo = upcomingEvents.get("events");
context.upcomingEventsSize = upcomingEventsInfo.upcomingEventsSize;

Date curStartdate = new Date();
SimpleDateFormat formatterStartDate = new SimpleDateFormat("yyyy-MM-dd");
String startDateVal= formatterStartDate.format(curStartdate);
def startDate = Date.parse("yyyy-MM-dd",startDateVal).toTimestamp()

Date curEndDate = new Date();
curEndDate.setDate(curEndDate.getDate()+10)
SimpleDateFormat formatterEndDate = new SimpleDateFormat("yyyy-MM-dd");
String endDateVal= formatterEndDate.format(curEndDate);
def endDate = Date.parse("yyyy-MM-dd",endDateVal).toTimestamp()

Map<String, Object> getEventInputMap = UtilMisc.toMap("partyId", academyId);
getEventInputMap.put("startDate", startDate);
getEventInputMap.put("thruDate", endDate);

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
    Map<String,Object> eventEntry = new HashMap<>();
    eventEntry.put("eventId",event.getEventId());
    eventEntry.put("title", event.getTitle());
    eventEntry.put("start", event.getStartDateTime().getTime());
    eventEntry.put("end", event.getEndDateTime().getTime());
    eventEntry.put("description",event.getDescription()!=null?event.getDescription():"");
    eventsToReturn.add(eventEntry);
}

context.events = eventsToReturn

def expenseResp = dispatcher.runSync("getExpenses",["academyId":academyId]);
def expenses = expenseResp.get("expenses");
context.expenses = expenses;

def totalPaymentAmountInfo = dispatcher.runSync("getTotalPaymentAmount",["academyId":academyId]);
def totalPaymentAmount = totalPaymentAmountInfo.get("totalPaymentAmount");
context.totalPayment = totalPaymentAmount.amount

def totalExpenseAmountInfo = dispatcher.runSync("getTotalExpenseAmount",["academyId":academyId]);
def totalExpenseAmount = totalExpenseAmountInfo.get("totalExpenseAmount");

context.totalExpense = totalExpenseAmount.amount

Date curFromDate = new Date();
curFromDate.setDate(1)
SimpleDateFormat formatterFromDate = new SimpleDateFormat("yyyy-MM-dd");
String fromDateVal= formatterFromDate.format(curFromDate);
def fromDate = Date.parse("yyyy-MM-dd",fromDateVal).toTimestamp()

Date curThruDate = new Date();
curThruDate.setDate(1)
curThruDate.setMonth(curThruDate.getMonth()+1)
SimpleDateFormat formatterThruDate = new SimpleDateFormat("yyyy-MM-dd");
String thruDateVal= formatterThruDate.format(curThruDate);
def thruDate = Date.parse("yyyy-MM-dd",thruDateVal).toTimestamp()

def getTotalExpenseAmountByDate = dispatcher.runSync("getTotalExpenseAmountByDate",["academyId":academyId,"fromDate":fromDate,"thruDate":thruDate]);
def expenseTotal = getTotalExpenseAmountByDate.get("expenseTotal");
context.totalExpenseByDate = expenseTotal.totalExpense

def getTotalPaymentAmountByDate = dispatcher.runSync("getTotalPaymentAmountByDate",["academyId":academyId,"fromDate":fromDate,"thruDate":thruDate]);
def paymentTotal = getTotalPaymentAmountByDate.get("paymentTotal");
context.totalPaymentByDate = paymentTotal.totalPayment


def academyInvoiceResp = dispatcher.runSync("fetchAcademyInvoice", ["academyId":academyId,"userLogin":UserLoginUtils.getSystemUserLogin(delegator)])
def  invoicesInfo = academyInvoiceResp.get("invoices");
String[] invoiceIds = invoicesInfo.invoiceId

BigDecimal outstandingAmountTotal = BigDecimal.ZERO;
for (String invoiceIdToFetch : invoiceIds) {
    Map<String, Object> invoicepaymentList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId",invoiceIdToFetch, "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
    def invoicePaymentInfoList1 = invoicepaymentList.get("invoicePaymentInfoList")
    outstandingAmountTotal = outstandingAmountTotal.add(invoicePaymentInfoList1.outstandingAmount)
}
context.outstandingAmountTotal = outstandingAmountTotal

def fetchAcademyInvoiceByDate = dispatcher.runSync("fetchAcademyInvoiceByDate", ["academyId":academyId,"fromDate":fromDate,"thruDate":thruDate])
def  invoicesByDate = fetchAcademyInvoiceByDate.get("invoicesByDate");
String[] invoiceIdsByDate = invoicesByDate.invoiceId

BigDecimal outstandingAmountTotalByDate = BigDecimal.ZERO;
for (String invoiceIdByDate : invoiceIdsByDate) {
    Map<String, Object> invoicepaymentList = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId",invoiceIdByDate, "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
    def invoicePaymentInfoList1 = invoicepaymentList.get("invoicePaymentInfoList")
    outstandingAmountTotalByDate = outstandingAmountTotalByDate.add(invoicePaymentInfoList1.outstandingAmount)
}
context.outstandingAmountTotalByDate = outstandingAmountTotalByDate

def notesList = dispatcher.runSync("fetchPvtNotes",["academyId":academyId])
def notes = notesList.get("noteInfoList")
context.notes = notes;

List<GenericValue> content = EntityQuery.use(delegator).from("Content").where(EntityCondition.makeCondition("createdByUserLogin", "system")).queryList()
context.contentList = content

def fetchRecentActivities = dispatcher.runSync("getRecentActivities",["academyId":academyId]);
def recentActivitiesList = fetchRecentActivities.get("recentActivities");
def recentActivities = []
for(def i = recentActivitiesList.size-1; i >= 0 ; i--)
{
    recentActivities.add(recentActivitiesList[i])
}
context.recentActivities = recentActivities
def recentActivityDateList = new ArrayList()
for(def recentActivity : recentActivities)
{
    PrettyTime prettyTime = new PrettyTime();
    if(recentActivity.activityType == "STUDENT" || recentActivity.activityType == "EVENT" || recentActivity.activityType == "INVOICE" || recentActivity.activityType == "NOTE" || recentActivity.activityType == "FILE")
    {
        if(recentActivity.action == "CREATED") {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if(recentActivity.action == "VIEWED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if(recentActivity.action == "UPDATED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "DELETED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "CANCELLED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "UPLOAD")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "DOWNLOAD")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "SHARED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
        else if (recentActivity.action == "UNSHARED")
        {
            def data = [:]
            Timestamp studentActivityTime = recentActivity.activityCreatedDate;
            String studentCreatedActivities = prettyTime.format(studentActivityTime)
            data.put("activityId",recentActivity.activityId)
            data.put("time",studentCreatedActivities)
            recentActivityDateList.add(data)
        }
    }
}
context.recentActivityDateList = recentActivityDateList