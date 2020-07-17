import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil

result = ServiceUtil.returnSuccess()

String newUserName = context.newUserName;
String newUserMail = context.newUserMail;
String newUserId = context.newUserPartyId;

if(newUserMail){
    Debug.log("Sending email to new User"+newUserId+"& mailId:"+newUserMail);

    String emailType ="VERIFY_USER_EMAIL";
    GenericValue  productStoreEmailSetting = delegator.findOne("ProductStoreEmailSetting",UtilMisc.toMap("productStoreId","9000","emailType",emailType),false);

    if(UtilValidate.isNotEmpty(productStoreEmailSetting)) {
        Map bodyParameters = UtilMisc.toMap("yourName",newUserName);
        dispatcher.runSync("sendMailFromScreen",
                UtilMisc.toMap(
                        "userLogin", context.userLogin,
                        "sendTo", newUserMail,
                        "sendFrom", productStoreEmailSetting.getString("fromAddress"),
                        "subject", productStoreEmailSetting.getString("subject"),
                        "bodyScreenUri", productStoreEmailSetting.getString("bodyScreenLocation"),
                        "bodyParameters", bodyParameters));
    }
    result.successMessage = (String) "Email Sent to [$newUserMail] "
    result.result = "Email Sent"
}else {
    result.successMessage = (String) "No Product store email setting found"
    result.result = "Setting not found"
    return ServiceUtil.returnFailure("Got no SendTo email id")
}

return result;