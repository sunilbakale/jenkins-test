import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.party.contact.*;
import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def noteInfo = [:]
println noteInfo
def privateNoteId = request.getParameter("privateNoteId");
context.academyId = academyId; //used editNote Page to compare
def privateNoteInfo = dispatcher.runSync("getPrivateNote",["academyId":academyId,"noteId":privateNoteId]);
def privateNoteList = privateNoteInfo.get("privateNote")

def noteData = delegator.findOne("NoteData", UtilMisc.toMap("noteId", privateNoteId), false);
if(UtilValidate.isNotEmpty(noteData)) {
    noteInfo.noteId = noteData.noteId
    noteInfo.title = noteData.noteName
    noteInfo.noteParty = noteData.noteParty
    noteInfo.summary = noteData.noteInfo
    noteInfo.noteDateTime = noteData.noteDateTime
    context.noteInfo = noteInfo
    List<Map<String,Object>> partyNote = delegator.findByAnd("PartyNote",UtilMisc.toMap( "noteId", privateNoteId), null, false);
    if(!partyNote.isEmpty()){
        context.partyNote = partyNote.partyId
    }
}

def studentsResponse = dispatcher.runSync("fetchStudentsOfAcademy", ["academyId":academyId])
def studentslist = studentsResponse.get("students");

context.studentslist = studentslist

