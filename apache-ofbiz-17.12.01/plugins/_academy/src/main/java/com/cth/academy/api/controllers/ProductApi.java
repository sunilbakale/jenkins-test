package com.cth.academy.api.controllers;

import com.cth.academy.model.ProductVO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.ServiceUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/products")
public class ProductApi implements BaseApi {

    public final static String module = ProductApi.class.getName();

    @Context
    HttpHeaders httpHeaders;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAvailableProducts() throws Exception {
        Debug.logInfo("getting product details", module);
        String token;
        List<ProductVO> productList = null;
        try {
            Map<String, Object> result = dispatcher.runSync("getAvailableProducts", UtilMisc.<String, Object>toMap());
            if (ServiceUtil.isError(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity(result.get("errorMessageList")).build();
            }
            if (!ServiceUtil.isSuccess(result)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to fetch product details").build();
            }
            productList = (List<ProductVO>) result.get("productList");
        } catch (GenericServiceException e) {
            throw new Exception("Unable to fetch product details " + e.getMessage());
        }
        return Response.ok(productList).build();
    }
}
