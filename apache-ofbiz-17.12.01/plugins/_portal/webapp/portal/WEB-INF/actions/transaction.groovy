import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityQuery

import java.sql.Timestamp
import java.text.SimpleDateFormat
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

Date date = new Date()
SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
String strDate= formatter.format(date);

String fromDateInput = null == parameters.fromDate? strDate : parameters.fromDate
def fromDateVal = new Date(fromDateInput).getTime()
Timestamp fromDate = new Timestamp(fromDateVal)

String thruDateInput = null == parameters.thruDate? strDate : parameters.thruDate
def thruDateVal = new Date(thruDateInput).getTime()
Timestamp thruDate = new Timestamp(thruDateVal)


def transactionResp = dispatcher.runSync("transactionLogInfoByDate",["academyId":academyId,"fromDate":fromDate,"thruDate":thruDate]);
def transactionList = transactionResp.get("resultList");
context.transactionList = transactionList;

println "transactionList"+transactionList

List<EntityCondition> condList = new LinkedList<EntityCondition>();
List<EntityCondition> condListForExpense = new LinkedList<EntityCondition>();
condList.add(EntityCondition.makeCondition("partyIdTo", academyId));
condListForExpense.add(EntityCondition.makeCondition("academyId", academyId))
EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
        EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate),
        EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, thruDate)),
        EntityOperator.AND);
condList.add(thruCond);
condListForExpense.add(thruCond);
EntityCondition condition = EntityCondition.makeCondition(condList);
EntityCondition conditionforExpense = EntityCondition.makeCondition(condListForExpense);

List<GenericValue> paymentInfo = null,expenseInfo = null;
try {
    println condition
    paymentInfo = EntityQuery.use(delegator).from("Payment").where(condition).queryList();
    println "paymentInfo "+paymentInfo;
    BigDecimal totalPayment = 0;
    for (GenericValue paymentGvs : paymentInfo){
        totalPayment += paymentGvs.getBigDecimal("amount");
    }
    context.totalPayment = totalPayment
    println "totalPayment"+totalPayment
    expenseInfo = EntityQuery.use(delegator).from("AcademyExpense").where(conditionforExpense).queryList();
    println "expenseInfo"+expenseInfo
    BigDecimal totalExpense = 0;
    for (GenericValue expenseGvs:expenseInfo){
        totalExpense += expenseGvs.getBigDecimal("amount");
    }
    context.totalExpense = totalExpense
    println "totalExpense"+totalExpense
} catch (GenericEntityException e) {
    e.printStackTrace();
}

println ":"
