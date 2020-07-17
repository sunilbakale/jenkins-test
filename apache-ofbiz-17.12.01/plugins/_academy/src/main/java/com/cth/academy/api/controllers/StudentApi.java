package com.cth.academy.api.controllers;

import com.cth.academy.api.filters.SecuredEndpoint;
import com.cth.academy.api.filters.ValidAcademyOperation;
import com.cth.academy.api.filters.ValidSubscriptionPlan;
import com.cth.academy.model.StudentVO;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/academy/{academy}/students")
public class StudentApi implements BaseApi {
    public final static String module = StudentApi.class.getName();

    @PathParam("academy")
    String academyId;

    @Context
    HttpHeaders httpHeaders;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response fetchStudentsOfAcademy() throws Exception {
        System.out.println("Getting Students for Academy Id: " + academyId);
        List<StudentVO> studentVOS = new ArrayList<>();
        try {
            Map<String, Object> result = dispatcher.runSync("fetchStudentsOfAcademy",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to fetch students " + result);
            }
            studentVOS = (List<StudentVO>) result.get("students");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to add student " + e.getMessage());
        }

        return Response.ok(studentVOS).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //@ValidSubscriptionPlan
    @SecuredEndpoint
    //@ValidAcademyOperation
    public Response createStudent(StudentVO studentVO) throws Exception {
        System.out.println("Create student to Academy Id: " + academyId);

        String partyId = httpHeaders.getHeaderString("partyId");
        if(!partyId.equals(academyId)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized token").build();
        }

        String newStudentId = null;
        // Invoke service: createStudent
        try {
            Map<String, Object> result = dispatcher.runSync("createStudent",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "email", studentVO.getEmail(),
                            "firstName", studentVO.getFirstName(),
                            "lastName", studentVO.getLastName(),
                            "mobile", studentVO.getMobile(),
                            "countryGeoId", studentVO.getCountryGeoId(),
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to add student " + result);
            }
            newStudentId = (String) result.get("studentId");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to add student " + e.getMessage());
        }

        if (UtilValidate.isEmpty(studentVO)) studentVO = new StudentVO();
        studentVO.setStudentId(newStudentId);
        return Response.ok(studentVO).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{studentId}")
    public Response updateStudent(@PathParam("studentId") String studentId, StudentVO studentVO) throws Exception {
        System.out.println("Update student " + studentId + " from Academy Id: " + academyId);
        System.out.println(studentVO);

        try {
            Map<String, Object> result = dispatcher.runSync("updateStudent",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "studentId", studentId,
                            "firstName", studentVO.getFirstName(),
                            "lastName", studentVO.getLastName(),
                            "mobile", studentVO.getMobile(),
                            "countryGeoId", studentVO.getCountryGeoId(),
                            "userLogin", getUserLogin(delegator, "system"))
            );
            if (!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to update student info. " + result);
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to update student info. " + e.getMessage());
        }
        return Response.ok(studentVO).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{studentId}")
    public Response deleteStudent(@PathParam("studentId") String studentId) throws Exception {
        System.out.println("Remove student " + studentId + " from Academy Id: " + academyId);
        try {
            Map<String, Object> result = dispatcher.runSync("removeStudentFromAcademy",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "studentId", studentId,
                            "userLogin", getUserLogin(delegator, "system"))
            );
            if (!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to remove student " + result);
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to remove student " + e.getMessage());
        }
        return Response.ok().build();
    }

}
