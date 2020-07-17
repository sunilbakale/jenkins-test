import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericEntityException
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.entity.util.EntityQuery

import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");


def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def studentslist = studentsResponse.get("students");

context.studentslist = studentslist