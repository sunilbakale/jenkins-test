import org.apache.ofbiz.base.util.Debug
import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil

result = ServiceUtil.returnSuccess()

String studentName = context.studentName;
String studentMail = context.sendTo;
String studentId = context.studentId;
if(studentMail){
    Debug.log("Sending email to new student"+studentId+""+studentMail);

    String emailType ="NEW_STD_ONBOARDED";
    GenericValue  productStoreEmailSetting = delegator.findOne("ProductStoreEmailSetting",UtilMisc.toMap("productStoreId","9000","emailType",emailType),false);

    if(UtilValidate.isNotEmpty(productStoreEmailSetting)) {
        Map bodyParameters = UtilMisc.toMap("yourName",studentName);
        dispatcher.runSync("sendMailFromScreen",
                UtilMisc.toMap(
                        "userLogin", context.userLogin,
                        "sendTo", studentMail,
                        "sendFrom", productStoreEmailSetting.getString("fromAddress"),
                        "subject", productStoreEmailSetting.getString("subject"),
                        "bodyScreenUri", productStoreEmailSetting.getString("bodyScreenLocation"),
                        "bodyParameters", bodyParameters));
    }
    result.successMessage = (String) "Email Sent to [$studentMail] "
    result.result = "Email Sent"
}else {
    result.successMessage = (String) "No Product store email setting found"
    result.result = "Setting not found"
}

return result;