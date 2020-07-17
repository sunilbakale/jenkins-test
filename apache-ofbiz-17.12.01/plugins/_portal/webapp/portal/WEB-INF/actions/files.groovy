import org.apache.ofbiz.entity.util.EntityQuery
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.base.util.UtilMisc
import javax.servlet.http.HttpSession;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

List<GenericValue> content = EntityQuery.use(delegator).from("Content").where(EntityCondition.makeCondition("createdByUserLogin", "system")).queryList()
context.contentList = content

GenericValue planMaxDiskSize = delegator.findOne("SystemProperty",
        UtilMisc.toMap("systemResourceId","subscription","systemPropertyId","max.size.limit"),false);

context.planMaxDiskSize = planMaxDiskSize.systemPropertyValue

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def students = studentsResponse.get("students");

context.students = students