import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.party.contact.ContactMechWorker

import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

String studentId = request.getParameter("student_id") // step 1
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
    Date activityCreatedDate = new Date();
    Map<String, Object> createViewRecentActivity = UtilMisc.toMap("academyId", academyId)
    createViewRecentActivity.put("activityType", "STUDENT")
    createViewRecentActivity.put("activityTypeId", studentId)
    createViewRecentActivity.put("action", "VIEWED")
    createViewRecentActivity.put("activityCreatedDate", activityCreatedDate)
    try {
        List<GenericValue> activities = delegator.findAll("RecentActivity", false);
        String oldStudentId = null;
        String oldStudentAction = null;

        for (GenericValue activity : activities) {
            oldStudentId = activity.get("activityTypeId")
            oldStudentAction = activity.get("action")
        }
        if (studentId == oldStudentId && oldStudentAction == "VIEWED") {
            print("should not create recent activity")
        } else {
            def student = delegator.findOne("Person", UtilMisc.toMap("partyId", studentId), false)
            createViewRecentActivity.put("activityTypeInfo", student.firstName +" "+student.lastName)
            dispatcher.runSync("createRecentActivity", createViewRecentActivity);
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }
}
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), studentInfo);