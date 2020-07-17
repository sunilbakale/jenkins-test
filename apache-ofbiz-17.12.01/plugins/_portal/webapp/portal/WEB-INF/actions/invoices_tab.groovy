import java.sql.Timestamp
import java.text.SimpleDateFormat;
import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
def academyId = userLogin.get("partyId")

def preferredCurrency = [:]
def preferred_currency_info = dispatcher.runSync("readAcademyPartyAttrCurrencyInfo",["partyId":academyId,"attrName":"preferred_currency"])
def preferredCurrencyMap = preferred_currency_info.get("partyAttrCurrencyInfo")
def currencyTypeVal = preferredCurrencyMap.get("attrValue")
preferredCurrency.currencyType = currencyTypeVal
context.preferredCurrency = preferredCurrency

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def students = studentsResponse.get("students");
context.students = students

//Invoice Filtering
Map<String, Object> invoiceInputMap = UtilMisc.toMap("academyId", academyId);

String startDateStr = parameters.startDate;
String endDateStr = parameters.endDate;
if(UtilValidate.isNotEmpty(startDateStr) && UtilValidate.isNotEmpty(endDateStr)){
    Date startDateVal = new Date(startDateStr);
    Timestamp startDate = new Timestamp(startDateVal.getTime());
    invoiceInputMap.put("startDate",startDate);

    Date endDateVal = new Date(endDateStr);
    Timestamp endDate = new Timestamp(endDateVal.getTime());
    invoiceInputMap.put("endDate",endDate);
}

//String[] studentList = request.getParameterValues("studentList")
String[] statusList = request.getParameterValues("statusList")
invoiceInputMap.put("userLogin",userLogin);

String student = request.getParameter("studentList")
invoiceInputMap.put("studentId",student);

if(UtilValidate.isNotEmpty(statusList) && statusList.length > 0) {
    List<String> statusType = new ArrayList<>();
    Collections.addAll(statusType, statusList);
    invoiceInputMap.put("statusList",statusType)
}

def invoicesResp = dispatcher.runSync("fetchAcademyInvoice", invoiceInputMap)
def  invoices = invoicesResp.get("invoices");
context.invoices = invoices;

BigDecimal invoiceTotalAmount = BigDecimal.ZERO;
for (def invoiceAmount : invoices.outstandingAmount)
{
    invoiceTotalAmount = invoiceTotalAmount.add(invoiceAmount)
}
context.invoicesTotalAmount = invoiceTotalAmount

String[] invoiceIds = invoices.invoiceId

BigDecimal outstandingAmountTotal = BigDecimal.ZERO;
if(UtilValidate.isNotEmpty(invoiceIds)){
    for (String invoiceIdToFetch : invoiceIds) {
        Map<String, Object> invoicePaymentListResponse = dispatcher.runSync("getInvoicePaymentInfoList", UtilMisc.<String, Object>toMap("invoiceId",invoiceIdToFetch, "userLogin", userLogin));
        if(UtilValidate.isNotEmpty(invoicePaymentListResponse)) {
            def invoicePaymentInfoList = invoicePaymentListResponse.get("invoicePaymentInfoList")
            outstandingAmountTotal = outstandingAmountTotal.add(invoicePaymentInfoList.outstandingAmount)
        }
    }
}
context.outstandingAmountTotal = outstandingAmountTotal

def paidAmount = invoiceTotalAmount - outstandingAmountTotal;
context.totalPayment = paidAmount

if(invoiceTotalAmount !=0){
    def unpaidTotalPercentage = outstandingAmountTotal/invoiceTotalAmount*100;
    context.unpaidTotalPercentage = unpaidTotalPercentage

    def paidTotalPercentage = paidAmount/invoiceTotalAmount*100;
    context.paidTotalPercentage = paidTotalPercentage;
}else {
    context.unpaidTotalPercentage = 0;
    context.paidTotalPercentage = 0
}

