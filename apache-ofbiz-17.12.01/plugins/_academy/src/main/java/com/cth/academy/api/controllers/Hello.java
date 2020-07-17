package com.cth.academy.api.controllers;

import com.cth.academy.model.StudentVO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/hello")
public class Hello {

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey";
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map sayJsonHello() {
        Map output = new HashMap();
        output.put("name", "Name");

        StudentVO studentVO = new StudentVO();
        studentVO.setFirstName("John Student");
        studentVO.setEmail("joh.teacher@gmail.com");

        output.put("student", studentVO);
        return output;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map getJsonHello(StudentVO studentVO) {
        System.out.println("Input got " + studentVO);

        Map map = new HashMap();
        map.put("output", "yes");
        map.put("student", studentVO);
        return map;
    }

}