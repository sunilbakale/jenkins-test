package com.cth.portal;

import com.cth.academy.utils.UserLoginUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.FileUtil;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import static org.apache.ofbiz.base.util.FileUtil.getFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;


public class FilesEvents {
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String module = FilesEvents.class.getName();

    public static String newFileUpload(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLoginSystem = UserLoginUtils.getSystemUserLogin(delegator);
        GenericValue userLoginId = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLoginId.get("partyId");
        String ofbizHome = System.getProperty("ofbiz.home");

        if (ServletFileUpload.isMultipartContent(request)) {
            try {
                String contentId = null;
                String contentName = null;
                File directory = FileUtil.getFile(ofbizHome+'/' + "runtime/uploads/");
                List<FileItem> multiFiles = new ServletFileUpload(
                        new DiskFileItemFactory(10240, FileUtil.getFile("runtime/uploads/"))).parseRequest(request);
                for (FileItem file : multiFiles) {
                    if (!file.isFormField()) {
                        File fileSaveDir = new File(String.valueOf(directory));
                        if (!fileSaveDir.exists()) {
                            fileSaveDir.mkdir();
                        }
                        String name = new File(file.getName()).getName();
                        String uploadLocation = directory + File.separator + name;

                        file.write(new File(uploadLocation));

                        Map<String, Object> uploadFileData = UtilMisc.toMap("textData", "textData");
                        uploadFileData.put("userLogin", userLoginSystem);

                        Map<String, Object> createDataResourceResult = dispatcher.runSync("createDataResource", uploadFileData);
                        String dataResourceId = (String) createDataResourceResult.get("dataResourceId");

                        Map<String, Object> createContent = dispatcher.runSync("createContent", UtilMisc.toMap("userLogin", userLoginSystem));
                        contentId = (String) createContent.get("contentId");
                        contentName = file.getName();

                        GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId", contentId).queryOne();
                        content.set("dataResourceId", dataResourceId);
                        content.set("contentName", file.getName());
                        content.set("mimeTypeId", file.getContentType());
                        content.store();

                        GenericValue DataResource = EntityQuery.use(delegator).from("DataResource").where("dataResourceId", dataResourceId).queryOne();
                        DataResource.set("dataResourceTypeId", "CONTEXT_FILE");
                        DataResource.set("dataResourceName", file.getName());
                        DataResource.set("objectInfo", uploadLocation);
                        DataResource.set("mimeTypeId", file.getContentType());
                        DataResource.store();

                        GenericValue storeFileDetails = delegator.makeValue("FileUpload");
                        String fileId = delegator.getNextSeqId("FileUpload");
                        if (UtilValidate.isNotEmpty(fileId)) {
                            storeFileDetails.set("fileId", fileId);
                            storeFileDetails.set("fileName", file.getName());
//                            storeFileDetails.set("fileType", file.getContentType());
                            storeFileDetails.set("fileSize", file.getSize());
                            storeFileDetails.set("createdByUserLogin", userLoginSystem.get("userLoginId"));
                            storeFileDetails.create();
                        }

                        GenericValue electronicText = delegator.makeValue("ElectronicText");
                        electronicText.set("dataResourceId", dataResourceId);
                        electronicText.set("textData", "textData");
                        electronicText.create();
                    }
                }
                String fileName = FilenameUtils.removeExtension(contentName);
                createRecentActivity(dispatcher,academyId,contentId,"UPLOAD",fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.setAttribute("STATUS", "SUCCESS");
        return SUCCESS;
    }

    public static String downloadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLoginId = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLoginId.get("partyId");
        String contentId = request.getParameter("contentId");
        Map<String, Object> contentValue = new HashMap<>();
        try {
            contentValue = dispatcher.runSync("getContent", UtilMisc.toMap("contentId", contentId));
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return "error";
        }

        Map<String, Object> content = (Map<String, Object>) contentValue.get("view");

        String fileName = (String) content.get("contentName");

        String path = (String) content.get("drObjectInfo");

        ServletOutputStream out = response.getOutputStream();

        FileInputStream fileInputStream = new FileInputStream(path);

        MimetypesFileTypeMap mimeType = new MimetypesFileTypeMap();

        response.setContentType(mimeType.getContentType(fileName));
        response.setHeader("Content-Disposition", "attachment; filename = " + fileName);
        response.setContentLength(fileInputStream.available());

        int i;
        while ((i = fileInputStream.read()) != -1) {
            out.write(i);
        }
        out.flush();
        out.close();
        fileInputStream.close();

        String contentName = FilenameUtils.removeExtension(fileName);
        createRecentActivity(dispatcher,academyId,contentId,"DOWNLOAD",contentName);

        request.setAttribute("STATUS", "SUCCESS");
        return SUCCESS;
    }

    public static String deleteContentRecord(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLoginSystem = UserLoginUtils.getSystemUserLogin(delegator);
        GenericValue userLoginId = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLoginId.get("partyId");
        String contentId = request.getParameter("contentId");
        String fileId = request.getParameter("fileId");

        GenericValue DataResource = null;

        try {
            GenericValue content = EntityQuery.use(delegator).from("Content").where("contentId", contentId).queryOne();
            DataResource = EntityQuery.use(delegator).from("DataResource").where("dataResourceId", content.get("dataResourceId")).queryOne();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String fileName = (String) DataResource.get("dataResourceName");
        String uploadedLocation = (String) DataResource.get("objectInfo");
        File file = new File(uploadedLocation);
        file.delete();

        try {
            List<GenericValue> sharedFiles = EntityQuery.use(delegator).from("PartyContent").where("contentId", contentId).queryList();
            if (UtilValidate.isNotEmpty(sharedFiles)) {
                for (GenericValue sharedFile : sharedFiles) {
                    sharedFile.remove();
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        try {
            dispatcher.runSync("removeContentAndRelated", UtilMisc.toMap("contentId", contentId, "userLogin", userLoginSystem));
            dispatcher.runSync("deleteFileDetails", UtilMisc.toMap("fileId", fileId));
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        String contentName = FilenameUtils.removeExtension(fileName);
        createRecentActivity(dispatcher,academyId,contentId,"DELETED",contentName);

        request.setAttribute("STATUS", "SUCCESS");
        return SUCCESS;
    }

    public static String createContentSharing(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher localDispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String academyId = (String) userLogin.get("partyId");
        String contentId = request.getParameter("contentId");
        String studentList[] = request.getParameterValues("partyId[]");
        String fileName =  request.getParameter("fileName");

        Map<String, Object> sharingMapData = new HashMap<>();

        if (UtilValidate.isNotEmpty(studentList)) {
            try {
                List<GenericValue> sharedFiles = EntityQuery.use(delegator).from("PartyContent").where("contentId", contentId).queryList();
                if (UtilValidate.isNotEmpty(sharedFiles)) {
                    for (GenericValue sharedFile : sharedFiles) {
                        sharedFile.remove();
                    }
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }

            try {
                for (String studentId : studentList) {
                    sharingMapData = localDispatcher.runSync("createPartyContent", UtilMisc.toMap("contentId", contentId,
                            "partyId", studentId, "partyContentTypeId", "USERDEF",
                            "userLogin", UserLoginUtils.getSystemUserLogin(delegator)));
                }

                if (!ServiceUtil.isSuccess(sharingMapData)) {
                    String errorMessage = (String) sharingMapData.get("errorMessage");
                    Debug.logError(errorMessage, module);
                    request.setAttribute("_ERROR_MESSAGE_", errorMessage);
                    request.setAttribute("STATUS", "ERROR");
                    return ERROR;
                }
            } catch (GenericServiceException e) {
                e.printStackTrace();
            }

            String contentName = FilenameUtils.removeExtension(fileName);
            createRecentActivity(localDispatcher,academyId,contentId,"SHARED",contentName);

        } else {
            try {
                List<GenericValue> sharedFiles = EntityQuery.use(delegator).from("PartyContent").where("contentId", contentId).queryList();
                if (UtilValidate.isNotEmpty(sharedFiles)) {
                    for (GenericValue sharedFile : sharedFiles) {
                        sharedFile.remove();
                    }
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }

            String contentName = FilenameUtils.removeExtension(fileName);
            createRecentActivity(localDispatcher,academyId,contentId,"UNSHARED",contentName);

        }
        request.setAttribute("STATUS", "SUCCESS");
        return SUCCESS;
    }

    public static String createRecentActivity(LocalDispatcher dispatcher, String academyId, String contentId, String action,String fileName)
    {
        Date now = new Date();
        Timestamp activityCreatedDate = new Timestamp(now.getTime());
        Map<String, Object> createRecentActivity = new HashMap<>();
        createRecentActivity.put("academyId", academyId);
        createRecentActivity.put("activityType", "FILE");
        createRecentActivity.put("activityTypeId", contentId);
        createRecentActivity.put("activityTypeInfo", fileName);
        createRecentActivity.put("activityCreatedDate", activityCreatedDate);
        createRecentActivity.put("action", action);
        try {
            dispatcher.runSync("createRecentActivity", UtilMisc.toMap(createRecentActivity));
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
}