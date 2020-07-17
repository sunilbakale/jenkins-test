package com.cth.academy.api.controllers;


import com.cth.academy.model.InvoicePaymentVO;
import com.cth.academy.model.InvoiceVO;
import com.cth.academy.model.StudentVO;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/academy/{academy}/invoices")
public class InvoiceApi implements BaseApi {

    @PathParam("academy")
    String academyId;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createInvoice(InvoiceVO invoiceVO) throws Exception {
        Map<String, Object> result = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            result = dispatcher.runSync("createAcademyInvoice", UtilMisc.toMap("academyId", academyId,
                    "toParty", invoiceVO.getToParty(),
                    "items", invoiceVO.getItems(),
                    "discount", invoiceVO.getDiscount(),
                    "dueDate", invoiceVO.getDueDate(),
                    "invoiceTypeId", invoiceVO.getInvoiceTypeId(),
                    "userLogin", userLogin));
        } catch (GenericServiceException e) {
            throw new Exception("Unable to create Invoice: " + e.getMessage());
        }
        return Response.ok(UtilMisc.<String, Object>toMap("invoiceId", (String) result.get("invoiceId"))).status(Response.Status.CREATED).build();

    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response invoicePayment(InvoicePaymentVO invoicePaymentVO) throws Exception {
        Map<String, Object> result = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            result = dispatcher.runSync("updateInvoicePayment", UtilMisc.toMap(
                    "academyId", academyId,
                    "invoiceId", invoicePaymentVO.getInvoiceId(),
                    "fromParty", invoicePaymentVO.getFromParty(),
                    "amount", invoicePaymentVO.getAmount(),
                    "comments", invoicePaymentVO.getComments(),
                    "userLogin", userLogin
            ));
        } catch (GenericServiceException e) {
            throw new Exception("Unable to add payment to Invoice: " + e.getMessage());
        }

        return Response.ok(UtilMisc.<String, Object>toMap("paymentId", (String) result.get("paymentId"))).status(Response.Status.CREATED).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoices() throws Exception {
        Map<String, Object> result = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            result = dispatcher.runSync("fetchAcademyInvoice",
                    UtilMisc.toMap(
                            "academyId", academyId,
                            "userLogin", userLogin
                    ));
        } catch (GenericServiceException e) {
            throw new Exception("Unable to get invoices :: " + e.getMessage());
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("/{invoiceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoiceById(@PathParam("invoiceId") String invoiceId) throws Exception {
        Map<String, Object> result = null;
        GenericValue userLogin = getUserLogin(delegator, "system");
        try {
            result = dispatcher.runSync("readAcademyInvoice",
                    UtilMisc.toMap(
                            "invoiceId", invoiceId,
                            "academyId", academyId,
                            "userLogin", userLogin
                    ));
        } catch (GenericServiceException e) {
            throw new Exception("Unable to read invoice details :: " + e.getMessage());
        }
        return Response.ok(result).build();
    }


}