import javax.servlet.http.HttpSession;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.party.contact.ContactMechWorker

HttpSession session = request.getSession();
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String academyId = (String) userLogin.get("partyId");

def partyInfo = [:]
def partyInfoResp = dispatcher.runSync("readAcademy",["academyId":academyId])
def academyVO = partyInfoResp.get("academyVO")
partyInfo.academyId = academyVO.academyId
partyInfo.firstName = academyVO.firstName
partyInfo.lastName = academyVO.lastName
partyInfo.gmail =  userLogin.userLoginId
partyInfo.partyId = academyId
partyInfo.mobile = academyVO.mobile
context.partyInfo = partyInfo
