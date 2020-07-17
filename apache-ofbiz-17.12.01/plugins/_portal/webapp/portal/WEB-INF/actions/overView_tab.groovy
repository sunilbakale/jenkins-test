import org.apache.ofbiz.base.util.UtilValidate;
import com.cth.academy.utils.UserLoginUtils
import org.apache.ofbiz.base.util.UtilMisc
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def preferredCurrency = [:]
def preferred_currency_info = dispatcher.runSync("readAcademyPartyAttrCurrencyInfo",["partyId":academyId,"attrName":"preferred_currency"])
def preferredCurrencyMap = preferred_currency_info.get("partyAttrCurrencyInfo")
def currencyTypeVal = preferredCurrencyMap.get("attrValue")
preferredCurrency.currencyType = currencyTypeVal
context.preferredCurrency = preferredCurrency

def Resp = dispatcher.runSync("fetchAcademyInvoice", ["academyId":academyId,"userLogin":UserLoginUtils.getSystemUserLogin(delegator)])
def  invoices = Resp.get("invoices");
context.invoices = invoices;

def expenseResp = dispatcher.runSync("getExpenses",["academyId":academyId]);
def expenses = expenseResp.get("expenses");
context.expenses = expenses;

def getInvoiceListTotalAmount = dispatcher.runSync("getInvoiceListTotalAmount",["academyId":academyId]);
def invoicesTotal = getInvoiceListTotalAmount.get("invoicesTotal");
def invoicesTotalAmount = invoicesTotal.allInvoiceTotal
context.invoicesTotalAmount = invoicesTotalAmount;

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

def totalPaymentAmountInfo = dispatcher.runSync("getTotalPaymentAmount",["academyId":academyId]);
def totalPaymentAmount = totalPaymentAmountInfo.get("totalPaymentAmount");
context.totalPayment = totalPaymentAmount.amount

if(invoicesTotalAmount !=0){
    def unpaidTotalPercentage = outstandingAmountTotal/invoicesTotalAmount*100;
    context.unpaidTotalPercentage = unpaidTotalPercentage

    def paidTotalPercentage = totalPaymentAmount.amount/invoicesTotalAmount*100;
    context.paidTotalPercentage = paidTotalPercentage;
}else {
    context.unpaidTotalPercentage = 0;
    context.paidTotalPercentage = 0
}

def transactionLog = dispatcher.runSync("transactionLog",["academyId":academyId]);
def resultList = transactionLog.get("resultList");
context.txInfo = resultList