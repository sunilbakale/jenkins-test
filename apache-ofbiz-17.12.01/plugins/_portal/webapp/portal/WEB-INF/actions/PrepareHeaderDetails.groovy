import org.apache.ofbiz.base.util.UtilMisc

context.userLogin = userLogin;



loggedInParty = delegator.findOne("Party", UtilMisc.toMap("partyId", userLogin.partyId), false)
context.loggedInParty = loggedInParty