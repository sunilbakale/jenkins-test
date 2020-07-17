import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;
import com.fasterxml.jackson.databind.ObjectMapper

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def privateNotesList = dispatcher.runSync("fetchPvtNotes",["academyId":academyId])
def privateNote = privateNotesList.get("noteInfoList")
context.privateNote = privateNote;
context.noteTypeId = academyId;
List<Map<String,Object>> partyNote = delegator.findByAnd("PartyNote",null, null, false);
if(!partyNote.isEmpty()){
    context.partyNote = partyNote.noteId
}

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def studentslist = studentsResponse.get("students");

context.studentslist = studentslist

ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), privateNote);
