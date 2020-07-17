/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.ofbiz.base.util.UtilMisc
import org.apache.ofbiz.base.util.UtilValidate
import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil
import org.apache.ofbiz.base.util.Debug

Debug.logInfo("-=-=-=- TEST GROOVY SERVICE -=-=-=-", "")
result = ServiceUtil.returnSuccess()
String module = "SampleEmailService.groovy"

String emailTo = context.emailTo;
if (emailTo) {
    Debug.logInfo("----- Sending email to: $emailTo -----", module)

    String emailType = "VERIFY_USER_EMAIL"
    GenericValue productStoreEmailSetting = delegator.findOne("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId","9000", "emailType", emailType), false)

    if(UtilValidate.isNotEmpty(productStoreEmailSetting)) {
        Map bodyParameters = UtilMisc.toMap("yourName", context.yourName )

        dispatcher.runSync("sendMailFromScreen",
                UtilMisc.toMap("userLogin", userLogin,
                        "sendTo", emailTo,
                        "sendFrom", productStoreEmailSetting.getString("fromAddress"),
                        "subject", productStoreEmailSetting.getString("subject"),
                        "bodyScreenUri", productStoreEmailSetting.getString("bodyScreenLocation"),
                        "bodyParameters", bodyParameters));

        result.successMessage = (String) "Email Sent to [$emailTo] "
        result.result = "Email Sent"
    } else  {
        result.successMessage = (String) "No Product store email setting found"
        result.result = "Setting not found"
    }

} else {
    result.successMessage = (String) "Got no SendTo email id"
    result.result = (String) "Got no SendTo email id"
    Debug.logInfo("Got no SendTo email id", module)
}

return result
