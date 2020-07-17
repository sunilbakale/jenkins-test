package com.cth.academy.services;

import com.cth.academy.model.AcademyVO;
import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.order.shoppingcart.CartItemModifyException;
import org.apache.ofbiz.order.shoppingcart.ShoppingCart;
import org.apache.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.ofbiz.party.contact.ContactMechWorker;
import org.apache.ofbiz.party.party.PartyWorker;
import org.apache.ofbiz.service.*;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class AcademyServices {

    public static final String module = AcademyServices.class.getName();
    private static String productStoreId = UtilProperties.getPropertyValue("academy.properties", "academy.product.store");
    private static String currency = UtilProperties.getPropertyValue("academy.properties", "academy.currency");

    public static Map<String, Object> readAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        System.out.println("readAcademy service : reading user");

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String academyId = (String) context.get("academyId");
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        AcademyVO academyVO = null;
        try {
            GenericValue partyObject = EntityQuery.use(delegator).from("PartyNameView").where("partyId", academyId).queryOne();
            academyVO = new AcademyVO();
            academyVO.setAcademyId(academyId);
            academyVO.setFirstName(partyObject.getString("firstName"));
            academyVO.setLastName(partyObject.getString("lastName"));

            List<Map<String, Object>> contactMechs = ContactMechWorker.getPartyContactMechValueMaps(delegator, academyId, false);
            if (UtilValidate.isNotEmpty(contactMechs) && contactMechs.size() > 0) {
                for (Map<String, Object> contactMechEntry : contactMechs) {
                    GenericValue contactMechTypeGv = (GenericValue) contactMechEntry.get("contactMechType");
                    GenericValue contactMechGv = (GenericValue) contactMechEntry.get("contactMech");

                    String contactMechType = contactMechTypeGv.getString("contactMechTypeId");
                    switch (contactMechType) {
                        case "EMAIL_ADDRESS":
                            academyVO.setEmail(contactMechGv.getString("infoString"));
                            break;
                        case "TELECOM_NUMBER":
                            GenericValue telecomNumberGv = (GenericValue) contactMechEntry.get("telecomNumber");
                            academyVO.setMobile(telecomNumberGv.getString("contactNumber"));
                            break;
                        case "POSTAL_ADDRESS":
                            GenericValue postalAddressGv = (GenericValue) contactMechEntry.get("postalAddress");
                            String countryGeoId = postalAddressGv.getString("countryGeoId");
                            GenericValue country = delegator.findOne("CountryCode", UtilMisc.toMap("countryCode", countryGeoId), false);
                            academyVO.setCountryName(country.getString("countryName"));
                            academyVO.setCountryGeoId(countryGeoId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendResp.put("academyVO", academyVO);
        return sendResp;
    }

    public static Map<String, Object> createAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        Debug.logInfo("createAcademy service: creating academy", module);

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String email = (String) context.get("email");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String mobile = (String) context.get("mobile");
        String password = (String) context.get("password");
        String countryGeoId = (String) context.get("countryGeoId");

        Map<String, Object> sendResp = ServiceUtil.returnSuccess();

        String academyId;

        // Check if party already exists for this email...
        String partyId = getPartyForEmailAddress(delegator, email);
        if (UtilValidate.isNotEmpty(partyId)) {
            Debug.logError("User with this email Id already exists", module);
            return ServiceUtil.returnError("", null, UtilMisc.toMap("emailExists", "true"), null);
            //Failure("Academy exists with Id "+partyId);
        }

        // 1. Add Party / User
        try {
            Map<String, Object> createUserResult = dispatcher.runSync("createPerson",
                    UtilMisc.<String, Object>toMap("firstName", firstName, "lastName", lastName));
            if (!ServiceUtil.isSuccess(createUserResult)) {
                return createUserResult;
            }
            academyId = (String) createUserResult.get("partyId");
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Person, error: " + e.getMessage());
        }

        // 2. Create USer login
        try {
            userLogin.setString("partyId", academyId);
            Map<String, Object> userLoginMap = UtilMisc.<String, Object>toMap("userLoginId", email, "partyId", academyId,
                    "currentPassword", password, "currentPasswordVerify", password, "enabled", "Y", "userLogin", userLogin);
            Map<String, Object> createUserResult = dispatcher.runSync("createUserLogin",
                    userLoginMap);
            if (!ServiceUtil.isSuccess(createUserResult)) {
                return createUserResult;
            }
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create User Login, error: " + e.getMessage());
        }

        // 3. Add Party Role
        try {
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", academyId,
                    "roleTypeId", "TEACHER",
                    "userLogin", userLogin
            );
            Map<String, Object> createPartyRoleResp = dispatcher.runSync("createPartyRole", partyRole);
            if (!ServiceUtil.isSuccess(createPartyRoleResp)) {
                return createPartyRoleResp;
            }
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Party Role (TEACHER), error: " + e.getMessage());
        }

        // 4. Add ContactMech & PartyContactMech for "email"
        try {
            String contactMechTypeId = "EMAIL_ADDRESS";
            String contactMechPurposeTypeId = "PRIMARY_EMAIL";

            Map<String, Object> createPartyContactMechResp = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", academyId,
                    "contactMechTypeId", contactMechTypeId,
                    "infoString", email,
                    "contactMechPurposeTypeId", contactMechPurposeTypeId,
                    "userLogin", userLogin));
            if (!ServiceUtil.isSuccess(createPartyContactMechResp)) {
                return createPartyContactMechResp;
            }
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Student Email Address, error: " + e.getMessage());
        }

        // 5. Add ContactMech for "mobile"
        if (UtilValidate.isNotEmpty(mobile)) {
            try {
                Map<String, Object> createTelecomNumberResp = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap(
                        "contactNumber", mobile,
                        "userLogin", userLogin));
                if (!ServiceUtil.isSuccess(createTelecomNumberResp)) {
                    return createTelecomNumberResp;
                }
                String telecomNumContactMechId = (String) createTelecomNumberResp.get("contactMechId");
                String contactMechPurposeTypeId = "PHONE_MOBILE";
                dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", academyId, "contactMechId", telecomNumContactMechId,
                        "contactMechTypeId", "TELECOM_NUMBER",
                        "contactMechPurposeTypeId", contactMechPurposeTypeId,
                        "userLogin", userLogin));
            } catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to create Student Telecom Number, error: " + e.getMessage());
            }
        }

        // Add ContactMech for "address"
        if (UtilValidate.isNotEmpty(countryGeoId)) {
            try {
                Map<String, Object> postalAddress = UtilMisc.toMap("userLogin", userLogin); // casting is here necessary for some compiler versions
                postalAddress.put("countryGeoId", countryGeoId);
                postalAddress.put("address1", "NA");
                postalAddress.put("postalCode", "NA");
                postalAddress.put("city", "NA");
                Map<String, Object> createPostalAddress = dispatcher.runSync("createPostalAddress", postalAddress);
                String postalContactMechId = (String) createPostalAddress.get("contactMechId");
                String contactMechPurposeTypeId = "PRIMARY_LOCATION";
                dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", academyId,
                        "contactMechId", postalContactMechId,
                        "contactMechTypeId", "POSTAL_ADDRESS",
                        "contactMechPurposeTypeId", contactMechPurposeTypeId,
                        "userLogin", userLogin));
            } catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to create academy postal address for countryGeoId, error: " + e.getMessage());
            }
        }
        //6.Send Email notification : sendMailToNewUser
        if(UtilValidate.isNotEmpty(email)){
            Debug.log("Sending mail to newUser, email:"+email);
            Map<String,Object> emailNotificationCtx = UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                    "newUserMail", email,
                    "newUserName",firstName+lastName,
                    "newUserPartyId",academyId
            );
            try {
                dispatcher.runSync("UserEmailVerification", emailNotificationCtx);
            }
            catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable send email notification, error: " + e.getMessage());
            }
        }
        sendResp.put("academyId", academyId);
        return sendResp;
    }

    public static Map<String, Object> assignSubscriptionToAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        //TODO: payment details
        //Assign subscription model to academy
        String productId = (String) context.get("productId");
        String academyId = (String) context.get("academyId");
        String productValidity = (String) context.get("productValidity");
        Map<String, Object> sendResp = null;
        String orderId = null;
        try {
            ShoppingCart cart = new ShoppingCart(delegator, productStoreId, null, locale, currency);
            try {
                cart.setUserLogin(userLogin, dispatcher);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            cart.setOrderType("SALES_ORDER");
            cart.setOrderPartyId(academyId);

            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
            GenericValue productPrice = EntityQuery.use(delegator).from("ProductPrice").where("productId", productId,
                    "productPricePurposeId", "USAGE_CHARGE", "productPriceTypeId", "DEFAULT_PRICE").queryFirst();
            ShoppingCartItem productItem = ShoppingCartItem.makeItem(Integer.valueOf(0), product, BigDecimal.ONE, BigDecimal.ONE, productPrice.getBigDecimal("price"), null,
                    null, null, null, null, null, null, null,
                    null, "PRODUCT_ORDER_ITEM", null, null, cart, Boolean.FALSE, Boolean.FALSE, null,
                    Boolean.TRUE, Boolean.TRUE);

            // save the order (new tx)
            Map<String, Object> createResp;
            createResp = dispatcher.runSync("createOrderFromShoppingCart", UtilMisc.toMap("shoppingCart", cart), 90, true);
            if (ServiceUtil.isError(createResp)) {
                return createResp;
            }

            orderId = (String) createResp.get("orderId");
            Map<String, Object> statusChangeRequestMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ORDER_APPROVED", "setItemStatus", "Y", "userLogin", userLogin);
            Map<String, Object> newSttsResult = null;

            newSttsResult = dispatcher.runSync("changeOrderStatus", statusChangeRequestMap);
            if (ServiceUtil.isError(newSttsResult)) {
                return newSttsResult;
            }
            sendResp = ServiceUtil.returnSuccess();
            sendResp.put("orderId", orderId);
        } catch (GenericServiceException | CartItemModifyException | GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Unable to create order, error: " + e.getMessage());
        }

        //Create subscription
        if (UtilValidate.isEmpty(productValidity)) {
            Debug.logError("productValidity is empty", module);
            return ServiceUtil.returnError("", null, UtilMisc.toMap("productValidity", "empty"), null);
        }
        try {
            Calendar calendar = Calendar.getInstance();
            GenericValue newSubscription = delegator.makeValue("Subscription");
            newSubscription.set("partyId", academyId);
            calendar.add(Calendar.MONTH,Integer.parseInt(productValidity));
            newSubscription.set("thruDate", UtilDateTime.getTimestamp(calendar.getTime().getTime()));
            newSubscription.set("productId", productId);
            newSubscription.set("orderId", orderId);
            newSubscription.set("fromDate", UtilDateTime.nowTimestamp());

            Map<String, Object> createSubscriptionMap = dctx.getModelService("createSubscription").makeValid(newSubscription, ModelService.IN_PARAM);
            createSubscriptionMap.put("userLogin", EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").queryOne());

            Map<String, Object> createSubscriptionResult = dispatcher.runSync("createSubscription", createSubscriptionMap);
            if (ServiceUtil.isError(createSubscriptionResult)) {
                return createSubscriptionResult;
            }
            sendResp.put("subscriptionId", createSubscriptionResult.get("subscriptionId"));
        } catch (GenericEntityException | GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Unable to create subscription model, error: " + e.getMessage());
        }
        return sendResp;
    }

    /**
     * Service to Update Student Info.
     * based on academy subscription
     *
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        //String academyId = (String) context.get("academyId");
        String academyId = (String) context.get("academyId");
        String email = (String) context.get("email");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String mobile = (String) context.get("mobile");
        String countryGeoId = (String) context.get("countryGeoId");

        Map<String, Object> sendResp = ServiceUtil.returnSuccess();

        //1. Update Person firstName, lastName (if available)
        if (UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName)) {
            try {
                Map<String, Object> updatePersonResult = dispatcher.runSync("updatePerson",
                        UtilMisc.<String, Object>toMap("partyId", academyId,
                                "firstName", firstName,
                                "lastName", lastName, "userLogin", userLogin));
                if (!ServiceUtil.isSuccess(updatePersonResult)) {
                    return updatePersonResult;
                }
            } catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to update Person info, error: " + e.getMessage());
            }
        }

        // 2. Update Phone
        if (UtilValidate.isNotEmpty(mobile)) {
            try {
                GenericValue telecomContactMech = PartyWorker.findPartyLatestTelecomNumber(academyId, delegator);
                if (UtilValidate.isNotEmpty(telecomContactMech)) {
                    System.out.println(telecomContactMech);
                    // Update existing one
                    telecomContactMech.setString("contactNumber", mobile);

                    delegator.store(telecomContactMech);

                } else {
                    Map<String, Object> createTelecomNumberResp = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap(
                            "contactNumber", mobile,
                            "userLogin", userLogin));
                    if (!ServiceUtil.isSuccess(createTelecomNumberResp)) {
                        return createTelecomNumberResp;
                    }
                    String telecomNumContactMechId = (String) createTelecomNumberResp.get("contactMechId");
                    String contactMechPurposeTypeId = "PHONE_MOBILE";
                    dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", academyId, "contactMechId", telecomNumContactMechId,
                            "contactMechTypeId", "TELECOM_NUMBER",
                            "contactMechPurposeTypeId", contactMechPurposeTypeId,
                            "userLogin", userLogin));

                }
            } catch (GenericEntityException | GenericServiceException e) {
                e.printStackTrace();
            }
        }

        if (UtilValidate.isNotEmpty(countryGeoId)) {
            try {
                GenericValue partyLatestPostalAddress = PartyWorker.findPartyLatestPostalAddress(academyId, delegator);
                if (UtilValidate.isNotEmpty(partyLatestPostalAddress)) {
                    System.out.println(partyLatestPostalAddress);
                    // Update existing one
                    partyLatestPostalAddress.setString("countryGeoId", countryGeoId);

                    delegator.store(partyLatestPostalAddress);

                } else {
                    Map<String, Object> postalAddress = UtilMisc.toMap("userLogin", userLogin); // casting is here necessary for some compiler versions
                    postalAddress.put("countryGeoId", countryGeoId);
                    postalAddress.put("address1", "NA");
                    postalAddress.put("postalCode", "NA");
                    postalAddress.put("city", "NA");
                    Map<String, Object> createPostalAddress = dispatcher.runSync("createPostalAddress", postalAddress);
                    String postalContactMechId = (String) createPostalAddress.get("contactMechId");
                    String contactMechPurposeTypeId = "PRIMARY_LOCATION";
                    dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", academyId,
                            "contactMechId", postalContactMechId,
                            "contactMechTypeId", "POSTAL_ADDRESS",
                            "contactMechPurposeTypeId", contactMechPurposeTypeId,
                            "userLogin", userLogin));
                }
            } catch (GenericEntityException | GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to create academy postal address for countryGeoId, error: " + e.getMessage());
            }
        }
        sendResp.put("academyId", academyId);
        return sendResp;
    } // End of updateStudent


    private static String getPartyForEmailAddress(Delegator delegator, String email) {
        // <ContactMech contactMechId="9202" contactMechTypeId="EMAIL_ADDRESS" infoString="ofbiztest@example.com"/>
        // Find any ContactMech by infoString
        try {
            List<GenericValue> contactMechs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "infoString", email), null, false);
            if (UtilValidate.isNotEmpty(contactMechs) && contactMechs.size() > 0) {
                GenericValue contactMech = contactMechs.get(0);

                // If found any ContactMech, find PartyContactMech
                //     <PartyContactMech partyId="DemoCustomer" contactMechId="9125" fromDate="2001-05-13 00:00:00.000" allowSolicitation="Y"/>
                GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where(UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId"))).filterByDate().queryFirst();
                if (UtilValidate.isNotEmpty(partyContactMech)) {
                    return (String) partyContactMech.get("partyId");
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map<String, Object> createAcademyPartyAttrCurrencyInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String attrName = (String) context.get("attrName");
        String attrVal = (String) context.get("attrVal");
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();

        Map<String,Object> partyAttrInputMap = new HashMap<>();
        partyAttrInputMap.put("partyId",partyId);
        partyAttrInputMap.put("attrName",attrName);
        partyAttrInputMap.put("attrValue",attrVal);
        GenericValue inputPartyAttr = null;

        try {
            inputPartyAttr = delegator.makeValue("PartyAttribute",UtilMisc.toMap(partyAttrInputMap));
            inputPartyAttr.create();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnFailure("Unable to create party attribute"+e.getMessage());
        }
        sendResp.put(ModelService.RESPONSE_MESSAGE,"Success");
        return sendResp;
    }
    public static Map<String,Object> updateAcademyPartyAttrCurrencyInfo(DispatchContext dctx,Map<String,?extends Object> context){
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String attrName = (String) context.get("attrName");
        String attrVal = (String) context.get("attrVal");

        Map<String,Object> partyAttrInputMap = new HashMap<>();
        partyAttrInputMap.put("partyId",partyId);
        partyAttrInputMap.put("attrName",attrName);
        partyAttrInputMap.put("attrValue",attrVal);

        GenericValue partyAttrGv = null;
        try {
            Map<String,Object> checkPartyAttrMap = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId",partyId,"attrName",attrName),false);
            if (UtilValidate.isNotEmpty(checkPartyAttrMap)){
                partyAttrGv = delegator.findOne("PartyAttribute",UtilMisc.toMap("partyId",partyId,"attrName",attrName),false);
                if (UtilValidate.isNotEmpty(partyAttrGv)){
                    partyAttrGv.putAll(partyAttrInputMap);
                    partyAttrGv.store();
                }
            }else {
                GenericValue inputPartyAttr = delegator.makeValue("PartyAttribute",UtilMisc.toMap(partyAttrInputMap));
                inputPartyAttr.create();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return sendResp;
    }
    public static Map<String,Object> readAcademyPartyAttrCurrencyInfo(DispatchContext dctx,Map<String,Object> context){
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String attrName = (String) context.get("attrName");
        Map<String,Object> partyAttrCurrencyInfo = new HashMap<>();
        try {
            GenericValue partyAttrGv = EntityQuery.use(delegator).from("PartyAttribute").where("partyId",partyId,"attrName",attrName).queryOne();
            if (UtilValidate.isNotEmpty(partyAttrGv)){

                partyAttrCurrencyInfo.put("partyId",partyAttrGv.get("partyId"));
                partyAttrCurrencyInfo.put("attrName",partyAttrGv.get("attrName"));
                partyAttrCurrencyInfo.put("attrValue",partyAttrGv.get("attrValue"));

            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("partyAttrCurrencyInfo",partyAttrCurrencyInfo);
        return sendResp;
    }
}
