package com.cth.academy.api.controllers;

import com.cth.academy.api.filters.SecuredEndpoint;
import com.cth.academy.model.AcademyVO;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/academy")
public class AcademyApi implements BaseApi {

    @Context
    HttpHeaders httpHeaders;

    @POST
    @Path("/authenticate")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response authenticate(@HeaderParam("email") String email, @HeaderParam("password") String password) throws Exception {
        System.out.println("Authenticate user: " + email);
        String token;
        try {
            Map<String, Object> result = dispatcher.runSync("authenticate",
                    UtilMisc.<String, Object>toMap("email", email, "password", password));
            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageList")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessage")).build();
            }
            token = (String) result.get("accessToken");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to authenticate " + e.getMessage());
        }
        return Response.ok(token).build();
    }

    @GET
    @Path("/logout")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @SecuredEndpoint
    public Response logout(@HeaderParam("accessToken") String accessToken) throws Exception {
        System.out.println("Logging out user: ");

        try {
            Map<String, Object> result = dispatcher.runSync("logout",
                    UtilMisc.<String, Object>toMap("accessToken", accessToken));
            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageMap")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to authenticate academy").build();
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to logout" + e.getMessage());
        }
        return Response.ok("Logged out successfully").build();
    }

    @GET
    @Path("{academyId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @SecuredEndpoint
    public Response read(@PathParam("academyId") String academyId, @HeaderParam("accessToken") String accessToken) throws Exception {
        System.out.println("Getting user details: " + academyId);
        AcademyVO academyVO = null;
        String partyId = httpHeaders.getHeaderString("partyId");
        if(!partyId.equals(academyId)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Academy Id is required").build();
        }

        try {
            Map<String, Object> result = dispatcher.runSync("readAcademy",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageMap")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to read academy").build();
            }
            academyVO = (AcademyVO) result.get("academyVO");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to read academy " + e.getMessage());
        }
        return Response.ok(academyVO).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AcademyVO academyVO) throws Exception {
        System.out.println("Inside create user");
        String academyId = null;
        try {
            Map<String, Object> result = dispatcher.runSync("createAcademy",
                    UtilMisc.<String, Object>toMap("email", academyVO.getEmail(),
                            "firstName", academyVO.getFirstName(),
                            "lastName", academyVO.getLastName(),
                            "mobile", academyVO.getMobile(),
                            "countryGeoId", academyVO.getCountryGeoId(),
                            "password", academyVO.getPassword(),
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageMap")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to create academy").build();
            }
            academyId = (String) result.get("academyId");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to add academy " + e.getMessage());
        }

        try {
            Map<String, Object> result = dispatcher.runSync("assignSubscriptionToAcademy",
                    UtilMisc.<String, Object>toMap(
                            "academyId", academyId,
                            "productId", academyVO.getProduct(),
                            "productValidity", academyVO.getProductValidity(),
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageMap")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to create academy").build();
            }
        } catch (GenericServiceException e) {
            throw new Exception("Unable to add academy " + e.getMessage());
        }
        return Response.ok(academyId).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @SecuredEndpoint
    public Response update(@HeaderParam("accessToken") String accessToken, AcademyVO academyVO) throws Exception {
        System.out.println("updating user");
        String academyId = academyVO.getAcademyId();
        String partyId = httpHeaders.getHeaderString("partyId");
        if(!partyId.equals(academyId)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized token").build();
        }
        try {
            Map<String, Object> result = dispatcher.runSync("updateAcademy",
                    UtilMisc.<String, Object>toMap("academyId", academyId,
                            "firstName", academyVO.getFirstName(),
                            "lastName", academyVO.getLastName(),
                            "mobile", academyVO.getMobile(),
                            "countryGeoId", academyVO.getCountryGeoId(),
                            "userLogin", getUserLogin(delegator, "system"))
            );

            if (!ServiceUtil.isSuccess(result)) {
                throw new Exception("Unable to update academy" + result);
            }
            academyId = (String) result.get("academyId");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to update academy " + e.getMessage());
        }
        return Response.ok(academyId).build();
    }
}
