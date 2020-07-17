package com.cth.academy.services;

import com.cth.academy.model.StudentVO;
import com.cth.academy.utils.StudentUtils;
import com.cth.academy.utils.UserLoginUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.party.contact.ContactHelper;
import org.apache.ofbiz.party.contact.ContactMechWorker;
import org.apache.ofbiz.party.party.PartyHelper;
import org.apache.ofbiz.party.party.PartyRelationshipHelper;
import org.apache.ofbiz.party.party.PartyWorker;
import org.apache.ofbiz.service.*;

import java.sql.Timestamp;
import java.util.*;

public class StudentServices {

    public static Map<String, Object> fetchStudentsOfAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String academyId = (String) context.get("academyId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();


        String roleTypeIdFrom = "TEACHER";
        String roleTypeIdTo = "STUDENT";
        String partyRelationshipTypeId = "STUDENT";

        List<EntityCondition> condList = new ArrayList<>();
        condList.add(EntityCondition.makeCondition("partyIdFrom", academyId));
        condList.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
        condList.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
        condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
        EntityCondition condition = EntityCondition.makeCondition(condList);

        List<GenericValue> studentsRels = null;
        try {
            studentsRels = delegator.findList("PartyRelationship", condition, null, null, null, false);
            studentsRels = EntityUtil.filterByDate(studentsRels);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem finding PartyRelationships.");
        }

        List<StudentVO> students = new ArrayList<>();

        if(UtilValidate.isNotEmpty(studentsRels)) {
            for(GenericValue studentRelGv: studentsRels) {
                StudentVO studentVO = new StudentVO();
                String studentPartyId = studentRelGv.getString("partyIdTo");
                studentVO.setStudentId(studentPartyId);

                try {
                    GenericValue partyObject = EntityQuery.use(delegator).from("PartyNameView").where("partyId", studentPartyId).queryOne();
                    studentVO.setFirstName(partyObject.getString("firstName"));
                    studentVO.setLastName(partyObject.getString("lastName"));

                    List<Map<String, Object>> contactMechs = ContactMechWorker.getPartyContactMechValueMaps(delegator, studentPartyId, false);
                    if(UtilValidate.isNotEmpty(contactMechs) && contactMechs.size()>0) {
                        for(Map<String,Object> contactMechEntry: contactMechs) {
                            GenericValue contactMechTypeGv = (GenericValue) contactMechEntry.get("contactMechType");
                            GenericValue contactMechGv = (GenericValue) contactMechEntry.get("contactMech");

                            String contactMechType = contactMechTypeGv.getString("contactMechTypeId");
                            switch (contactMechType) {
                                case "EMAIL_ADDRESS":
                                    studentVO.setEmail(contactMechGv.getString("infoString"));
                                    break;
                                case "TELECOM_NUMBER":
                                    GenericValue telecomNumberGv = (GenericValue) contactMechEntry.get("telecomNumber");
                                    studentVO.setMobile(telecomNumberGv.getString("contactNumber"));
                                    break;
                                case "POSTAL_ADDRESS":
                                    GenericValue postalAddressGv = (GenericValue) contactMechEntry.get("postalAddress");
                                    String countryGeoId = postalAddressGv.getString("countryGeoId");
                                    GenericValue country = delegator.findOne("CountryCode", UtilMisc.toMap("countryCode", countryGeoId), false);
                                    studentVO.setCountryName(country.getString("countryName"));
                                    studentVO.setCountryGeoId(countryGeoId);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                students.add(studentVO);
            }
        }
        sendResp.put("students",students);
        return sendResp;
    }


    /**
     * Service to Add a new student.
     * This service should check if given academy has permission/allowed to add a new student
     * based on academy subscription
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createStudent(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String academyId = (String) context.get("academyId");
        String email = (String) context.get("email");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String mobile = (String) context.get("mobile");
        String countryGeoId = (String) context.get("countryGeoId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        // TODO: Check Academy Subscription and ability to add student

        String studentId;

        // Check if party already exists for this email...
        String partyId = getPartyForEmailAddress(delegator, email);
        if(UtilValidate.isNotEmpty(partyId)) {
            System.out.println("Student with this email Id already exists");
            sendResp.put("studentId", partyId);
            studentId = partyId;

            try {
                addStudentToAcademy(dispatcher, userLogin, academyId, studentId);
            } catch (Exception e) {
                return ServiceUtil.returnFailure("Unable to create Person, error: " + e.getMessage());
            }
            return sendResp;
        }


        // 1. Add Party / Person
        try {
            Map<String,Object> createPersonResult = dispatcher.runSync("createPerson",
                    UtilMisc.<String, Object>toMap("firstName", firstName, "lastName", lastName));
            if(!ServiceUtil.isSuccess(createPersonResult)) {
                return createPersonResult;
            }
            studentId = (String) createPersonResult.get("partyId");
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Person, error: " + e.getMessage());
        }

        // 2. Add Party Role
        try {
            Map<String, Object> partyRole = UtilMisc.toMap(
                    "partyId", studentId,
                    "roleTypeId", "STUDENT",
                    "userLogin", userLogin
            );
            Map<String,Object> createPartyRoleResp = dispatcher.runSync("createPartyRole", partyRole);
            if(!ServiceUtil.isSuccess(createPartyRoleResp)) {
                return createPartyRoleResp;
            }
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Party Role (STUDENT), error: " + e.getMessage());
        }

        // 3. Add PartyRelationship with Academy
        try {
            addStudentToAcademy(dispatcher, userLogin, academyId, studentId);
        } catch (Exception  e) {
            return ServiceUtil.returnFailure("Unable to create Teacher-Student Relationship, error: " + e.getMessage());
        }

        // 4. Add ContactMech & PartyContactMech for "email"
        try {
            String contactMechTypeId = "EMAIL_ADDRESS";
            String contactMechPurposeTypeId = "PRIMARY_EMAIL";

            Map<String,Object> createPartyContactMechResp = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", studentId,
                    "contactMechTypeId", contactMechTypeId,
                    "infoString", email,
                    "contactMechPurposeTypeId", contactMechPurposeTypeId,
                    "userLogin", userLogin));
            if(!ServiceUtil.isSuccess(createPartyContactMechResp)) {
                return createPartyContactMechResp;
            }
        } catch (GenericServiceException e) {
            return ServiceUtil.returnFailure("Unable to create Student Email Address, error: " + e.getMessage());
        }

        // 5. Add ContactMech for "mobile"
        if(UtilValidate.isNotEmpty(mobile)) {
            try {
                Map<String,Object> createTelecomNumberResp = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap(
                        "contactNumber", mobile,
                        "userLogin", userLogin));
                if(!ServiceUtil.isSuccess(createTelecomNumberResp)) {
                    return createTelecomNumberResp;
                }
                String telecomNumContactMechId = (String) createTelecomNumberResp.get("contactMechId");
                String contactMechPurposeTypeId = "PHONE_MOBILE";
                dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", studentId, "contactMechId", telecomNumContactMechId,
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
                dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", studentId,
                        "contactMechId", postalContactMechId,
                        "contactMechTypeId", "POSTAL_ADDRESS",
                        "contactMechPurposeTypeId", contactMechPurposeTypeId,
                        "userLogin", userLogin));
            } catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to create academy postal address for countryGeoId, error: " + e.getMessage());
            }
        }
        String studentName = firstName+ " "+lastName ;
        studentRecentActivity(dctx,academyId,studentId,"CREATED",studentName);

        //6.send email notification
        if(UtilValidate.isNotEmpty(email)){
            Debug.log("Sending email to"+email);
            Map<String,Object> emailNotificationCtx = UtilMisc.toMap(
                    "userLogin", UserLoginUtils.getSystemUserLogin(delegator),
                    "sendTo", email,
                    "studentName",firstName+lastName,
                    "studentId",studentId
            );
            try {
                dispatcher.runSync("studentEmailNotification", emailNotificationCtx);
            }
            catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable send email notification, error: " + e.getMessage());
            }
        }
        sendResp.put("studentId", studentId);
        return sendResp;
    }

    /**
     * Service to Update Student Info.
     * based on academy subscription
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateStudent(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String academyId = (String) context.get("academyId");
        String studentId = (String) context.get("studentId");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String mobile = (String) context.get("mobile");
        String countryGeoId = (String) context.get("countryGeoId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        //1. Update Person firstName, lastName (if available)
        if(UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName)) {
            try {
                Map<String,Object> updatePersonResult = dispatcher.runSync("updatePerson",
                        UtilMisc.<String, Object>toMap("partyId", studentId,
                                "firstName", firstName,
                                "lastName", lastName, "userLogin", userLogin));
                if(!ServiceUtil.isSuccess(updatePersonResult)) {
                    return updatePersonResult;
                }
            } catch (GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to update Person info, error: " + e.getMessage());
            }
        }

        // 2. Update Phone
        if(UtilValidate.isNotEmpty(mobile)) {
            GenericValue telecomContactMech = PartyWorker.findPartyLatestTelecomNumber(studentId, delegator);
            if(UtilValidate.isNotEmpty(telecomContactMech)) {
                System.out.println(telecomContactMech);
                // Update existing one
                telecomContactMech.setString("contactNumber",mobile);
                try {
                    delegator.store(telecomContactMech);
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Map<String, Object> createTelecomNumberResp = dispatcher.runSync("createTelecomNumber", UtilMisc.toMap(
                            "contactNumber", mobile,
                            "userLogin", userLogin));
                    if (!ServiceUtil.isSuccess(createTelecomNumberResp)) {
                        return createTelecomNumberResp;
                    }
                    String telecomNumContactMechId = (String) createTelecomNumberResp.get("contactMechId");
                    String contactMechPurposeTypeId = "PHONE_MOBILE";
                    dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", studentId, "contactMechId", telecomNumContactMechId,
                            "contactMechTypeId", "TELECOM_NUMBER",
                            "contactMechPurposeTypeId", contactMechPurposeTypeId,
                            "userLogin", userLogin));
                } catch (GenericServiceException e) {
                    return ServiceUtil.returnFailure("Unable to create Student Telecom Number, error: " + e.getMessage());
                }
            }
        }

        // Add ContactMech for "address"
        if (UtilValidate.isNotEmpty(countryGeoId)) {
            try {
                GenericValue partyLatestPostalAddress = PartyWorker.findPartyLatestPostalAddress(studentId, delegator);
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
                        dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", studentId,
                                "contactMechId", postalContactMechId,
                                "contactMechTypeId", "POSTAL_ADDRESS",
                                "contactMechPurposeTypeId", contactMechPurposeTypeId,
                                "userLogin", userLogin));
                }
            } catch (GenericEntityException | GenericServiceException e) {
                return ServiceUtil.returnFailure("Unable to create academy postal address for countryGeoId, error: " + e.getMessage());
            }
        }
        String studentName = firstName+ " "+lastName ;
        studentRecentActivity(dctx,academyId,studentId,"UPDATED",studentName);
        return sendResp;
    } // End of updateStudent


    /**
     * Remove student from the academy
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> removeStudentFromAcademy(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String academyId = (String) context.get("academyId");
        String studentId = (String) context.get("studentId");

        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        // 1. thruDate PartyRelationship with Academy
        try {
            Map<String, Object> partyRelationship = UtilMisc.toMap(
                    "partyIdFrom", academyId,
                    "partyIdTo", studentId,
                    "roleTypeIdFrom", "TEACHER",
                    "roleTypeIdTo","STUDENT",
                    "partyRelationshipTypeId", "STUDENT",
                    "fromDate", UtilDateTime.nowTimestamp()
            );

            List<GenericValue> existingPartyRels = PartyRelationshipHelper.getActivePartyRelationships(dispatcher.getDelegator(), partyRelationship);
            if(UtilValidate.isNotEmpty(existingPartyRels) && existingPartyRels.size()>0) {
                // thruDate
                GenericValue relationshipGv = existingPartyRels.get(0);
                relationshipGv.set("thruDate", UtilDateTime.nowTimestamp());
                delegator.store(relationshipGv);
                try
                {
                GenericValue partyObject = EntityQuery.use(delegator).from("PartyNameView").where("partyId", studentId).queryOne();
                String firstName = partyObject.getString("firstName");
                String lastName = partyObject.getString("lastName");
                String studentName = firstName + " "+lastName;
                studentRecentActivity(dctx,academyId,studentId,"DELETED",studentName);
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }

            } else {
                return ServiceUtil.returnFailure("Student " + studentId + " not associated with this academy");
            }
        } catch (Exception  e) {
            return ServiceUtil.returnFailure("Unable to remove Teacher-Student Relationship, error: " + e.getMessage());
        }

        return sendResp;
    }


    /** Add student to Academy - if already doesn't exists */
    private static void addStudentToAcademy(LocalDispatcher dispatcher, GenericValue userLogin, String academyId, String studentId) throws Exception {

        Map<String, Object> partyRelationship = UtilMisc.toMap(
                "partyIdFrom", academyId,
                "partyIdTo", studentId,
                "roleTypeIdFrom", "TEACHER",
                "roleTypeIdTo","STUDENT",
                "partyRelationshipTypeId", "STUDENT",
                "fromDate", UtilDateTime.nowTimestamp()
        );

        List<GenericValue> existingPartyRels = PartyRelationshipHelper.getActivePartyRelationships(dispatcher.getDelegator(), partyRelationship);
        if(UtilValidate.isNotEmpty(existingPartyRels) && existingPartyRels.size()>0) {
            return; // already exists
        }

        Map<String, Object> createPartyRelnCtx = UtilMisc.toMap(
                "partyIdFrom", academyId,
                "partyIdTo", studentId,
                "roleTypeIdFrom", "TEACHER",
                "roleTypeIdTo","STUDENT",
                "partyRelationshipTypeId", "STUDENT",
                "userLogin", userLogin
        );
        Map<String,Object> createPartyRelnResult = dispatcher.runSync("createPartyRelationship", createPartyRelnCtx);
        if(!ServiceUtil.isSuccess(createPartyRelnResult)) {
            //return createPartyRelnResult;
            throw new Exception ("Unable to create Academy-Student relationship: " + createPartyRelnResult.get("message"));
        }

    }

    private static String getPartyForEmailAddress(Delegator delegator, String email) {
        // <ContactMech contactMechId="9202" contactMechTypeId="EMAIL_ADDRESS" infoString="ofbiztest@example.com"/>
        // Find any ContactMech by infoString
        try {
            List<GenericValue> contactMechs = delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS", "infoString", email), null, false);
            if(UtilValidate.isNotEmpty(contactMechs) && contactMechs.size()>0) {
                GenericValue contactMech = contactMechs.get(0);

                // If found any ContactMech, find PartyContactMech
                //     <PartyContactMech partyId="DemoCustomer" contactMechId="9125" fromDate="2001-05-13 00:00:00.000" allowSolicitation="Y"/>
                GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where( UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")) ).filterByDate().queryFirst();
                if(UtilValidate.isNotEmpty(partyContactMech)) {
                    return (String) partyContactMech.get("partyId");
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> studentRecentActivity(DispatchContext dispatchContext, String academyId, String studentId,String action,String activityTypeInfo ) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Map sendResponse = ServiceUtil.returnSuccess();
        Map<String, Object> createRecentActivity = new HashMap<>();
        Date now = new Date();
        Timestamp activityCreatedDate = new Timestamp(now.getTime()) ;
        createRecentActivity.put("academyId", academyId);
        createRecentActivity.put("activityType", "STUDENT");
        createRecentActivity.put("activityTypeId", studentId);
        createRecentActivity.put("activityTypeInfo", activityTypeInfo);
        createRecentActivity.put("activityCreatedDate", activityCreatedDate);
        createRecentActivity.put("action", action);
        try {
            localDispatcher.runSync("createRecentActivity", UtilMisc.toMap(createRecentActivity));
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return sendResponse;
    }

}
