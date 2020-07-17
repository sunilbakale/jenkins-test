import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def students = studentsResponse.get("students");

context.students = students
