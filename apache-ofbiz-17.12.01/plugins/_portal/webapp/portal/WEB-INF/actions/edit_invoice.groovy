import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = session.getAttribute("userLogin");
def academyId = userLogin.get("partyId");

def preferredCurrency = [:]
def preferred_currency_info = dispatcher.runSync("readAcademyPartyAttrCurrencyInfo",["partyId":academyId,"attrName":"preferred_currency"])
def preferredCurrencyMap = preferred_currency_info.get("partyAttrCurrencyInfo")
def currencyTypeVal = preferredCurrencyMap.get("attrValue")
preferredCurrency.currencyType = currencyTypeVal
context.preferredCurrency = preferredCurrency

String invoiceId = request.getParameter("invoice_id");
context.invoiceId = invoiceId

def invoiceInfo = [:]
def invoiceParty = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);
if(UtilValidate.isNotEmpty(invoiceParty)){
    def invoicePartyId = delegator.findOne("Invoice",UtilMisc.toMap("invoiceId",invoiceId),false);
    invoiceInfo.invoiceId = invoicePartyId.invoiceId;
    invoiceInfo.partyId = invoicePartyId.partyId;
    invoiceInfo.statusId = invoicePartyId.statusId;
    invoiceInfo.invoiceDate = invoicePartyId.invoiceDate;
    invoiceInfo.dueDate = invoicePartyId.dueDate;
    invoiceInfo.statusId = invoicePartyId.statusId;
}
String studentId = invoiceParty.partyId;

def studentPerson = delegator.findOne("Person", UtilMisc.toMap("partyId", studentId), false)
invoiceInfo.firstName = studentPerson.firstName;
invoiceInfo.lastName = studentPerson.lastName

def invoiceResponse = dispatcher.runSync("readAcademyInvoice", UtilMisc.<String, Object>toMap("invoiceId", invoiceId, "academyId", academyId));
def resultMap = invoiceResponse.get("resultMap");
def invoicePaymentInfoList = resultMap.get("invoicePaymentInfoList")
def invoice = resultMap.get("invoice")

context.invoice = invoice;
context.invoicePaymentInfoList = invoicePaymentInfoList
def outstandingAmount = invoicePaymentInfoList.outstandingAmount
def paidAmount = invoicePaymentInfoList.paidAmount
invoiceInfo.outstandingAmount = outstandingAmount.get(0)
invoiceInfo.paidAmount = paidAmount
context.invoiceInfo = invoiceInfo;

def invoicePaymentById  = dispatcher.runSync("getPaymentInfoById",UtilMisc.toMap("invoiceId",invoiceId,"partyIdFrom",academyId))
def paymentInfoOnly= invoicePaymentById.get("paymentInfo")
context.paymentInfoOnly = paymentInfoOnly

def invoiceItemRes = dispatcher.runSync("readAcademyInvoiceItems",UtilMisc.toMap("academyId",academyId,"invoiceId",invoiceId))
def invoiceItemResList = invoiceItemRes.get("resultMap")
context.itemList = invoiceItemResList