import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery
import com.fasterxml.jackson.databind.ObjectMapper

def contentId = request.getParameter("contentId")
List<GenericValue> sharedFiles = EntityQuery.use(delegator).from("PartyContent").where("contentId", contentId).queryList();

ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(response.getWriter(), sharedFiles.partyId);
