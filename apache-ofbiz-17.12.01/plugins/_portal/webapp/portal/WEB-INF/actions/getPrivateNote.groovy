import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.party.contact.*;
import com.fasterxml.jackson.databind.ObjectMapper
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def noteInfo = [:]
def privateNoteId = request.getParameter("noteId");

context.academyId = academyId;
def privateNoteInfo = dispatcher.runSync("getPrivateNote",["academyId":academyId,"noteId":privateNoteId]);
def privateNoteList = privateNoteInfo.get("privateNote")
ArrayList studentNames = new ArrayList();
def noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", privateNoteId), false);
if(UtilValidate.isNotEmpty(noteData)) {
    noteInfo.noteId = noteData.noteId
    noteInfo.title = noteData.noteName
    noteInfo.noteParty = noteData.noteParty
    noteInfo.noteSummary = noteData.noteInfo
    noteInfo.noteDateTime = noteData.noteDateTime
    noteInfo.guestListIds = privateNoteList[0].guestList
    String [] guestIds =  privateNoteList[0].guestList
    def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
    def studentslist = studentsResponse.get("students");
    for(Object student : studentslist) {
        for (String guestId : guestIds) {
            if (student.studentId == guestId) {
                studentNames.add(student.firstName + " " + student.lastName);
                noteInfo.guestListNames = studentNames;
            }
        }
    }
    List<Map<String,Object>> partyNote = delegator.findByAnd("PartyNote",UtilMisc.toMap( "noteId", privateNoteId), null, false);
    if(!partyNote.isEmpty()){
        context.partyNote = partyNote.partyId
    }
}
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), noteInfo);
