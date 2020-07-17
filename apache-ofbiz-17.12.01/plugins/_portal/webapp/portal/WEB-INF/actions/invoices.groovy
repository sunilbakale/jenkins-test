import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate;
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = session.getAttribute("userLogin");
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

/*for Expenses*/
def expenseResp = dispatcher.runSync("getExpenses",["academyId":academyId]);
def expenses = expenseResp.get("expenses");
context.expenses = expenses;

def totalExpenseAmountInfo = dispatcher.runSync("getTotalExpenseAmount",["academyId":academyId]);
def totalExpenseAmount = totalExpenseAmountInfo.get("totalExpenseAmount");

context.totalExpense = totalExpenseAmount.amount

