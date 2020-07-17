package com.cth.academy.services;

import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.common.CommonServices;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.*;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

public class NoteServices {
    public final static String module = CommonServices.class.getName();
    public static final String resource = "CommonUiLabels";
    public static boolean recentActivityViewDone = false;
    public static boolean recentActivityDeleteDone = false;
    public static Map<String, Object> createPvtNote(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Timestamp noteDate = (Timestamp) context.get("noteDate");
        String academyId = (String) context.get("academyId");
        String noteName = (String) context.get("noteName");
        String note = (String) context.get("note");
        String noteId = delegator.getNextSeqId("NoteData");
        Locale locale = (Locale) context.get("locale");
        if (noteDate == null) {
            noteDate = UtilDateTime.nowTimestamp();
        }

        Map<String, Object> fields = UtilMisc.toMap("noteId", noteId, "noteName", noteName, "noteInfo", note,
                "noteParty",academyId, "noteDateTime", noteDate);

        try {
            GenericValue newValue = delegator.makeValue("NoteData", fields);
            delegator.create(newValue);
            ArrayList<String> guestList = (ArrayList<String>) context.get("partyId");
            Map<String, Object> partyNote = UtilMisc.toMap(
                    "noteId", noteId
            );
            if (!UtilValidate.isEmpty(guestList)) {
                for (String guestId : guestList) {
                    partyNote.put("partyId", guestId);
                    GenericValue partyNoteGvs = delegator.makeValue("PartyNote", partyNote);
                    delegator.create(partyNoteGvs);
                }
            }
            noteRecentActivity(ctx,academyId,noteId,"CREATED",noteName);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to create Event Role, error: " + e.getMessage());
        }

        result.put("noteId", noteId);
        return result;
    }
    public static Map<String,Object> fetchPvtNotes(DispatchContext dctx,Map<String,?extends Object> context){

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        String partyId = (String) context.get("academyId");
        String academyId = (String) context.get("academyId");
        List<GenericValue> privateNoteGvs = null;
        List<Map<String,Object>> noteInfoList = new ArrayList<>();
        try {
            privateNoteGvs = EntityQuery.use(delegator).from("NoteData").where("noteParty",academyId).queryList();
            if(UtilValidate.isNotEmpty(privateNoteGvs)){
                for (GenericValue privateNoteGv :privateNoteGvs){
                    Map<String,Object> noteInfoMap = new HashMap<>();
                    noteInfoMap.put("noteId",privateNoteGv.get("noteId"));
                    noteInfoMap.put("noteParty",privateNoteGv.get("noteParty"));
                    noteInfoMap.put("title",privateNoteGv.get("noteName"));
                    noteInfoMap.put("summary",privateNoteGv.get("noteInfo"));
                    noteInfoMap.put("createdStamp",privateNoteGv.get("createdStamp"));
                    List<GenericValue> checkNoteData = EntityQuery.use(delegator).from("PartyNote").where("noteId",privateNoteGv.get("noteId")).queryList();
                    if(UtilValidate.isNotEmpty(checkNoteData)){
                        noteInfoMap.put("noteType","shared");
                    }else{
                        noteInfoMap.put("noteType","private");
                    }
                    noteInfoList.add(noteInfoMap);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        sendResp.put("noteInfoList",noteInfoList);
        return sendResp;
    }
    public static Map<String,Object> getPrivateNote (DispatchContext dctx,Map<String,?extends  Object> context){
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String noteParty = (String) context.get("academyId");
        String noteId = (String) context.get("noteId");

        List<GenericValue> privateNoteGv = null;
        List<Map<String,Object>> noteInfoList = new ArrayList<>();
        Map<String,Object> noteInfoMap = new HashMap<>();
        String noteName = null;
        try {
            privateNoteGv = EntityQuery.use(delegator).from("NoteData").where("noteId",noteId).queryList();
            if(UtilValidate.isNotEmpty(privateNoteGv)){
                for (GenericValue privateNote :privateNoteGv){
                    noteName = (String) privateNote.get("noteName");
                    noteInfoMap.put("noteId",privateNote.get("noteId"));
                    noteInfoMap.put("title",privateNote.get("noteName"));
                    noteInfoMap.put("noteParty",privateNote.get("noteParty"));
                    noteInfoMap.put("summary",privateNote.get("noteInfo"));
                    noteInfoMap.put("noteDateTime",privateNote.get("noteDateTime"));
                    noteInfoList.add(noteInfoMap);
                }
            }
            List<GenericValue>  PartyNote= delegator.findByAnd("PartyNote",
                    UtilMisc.toMap( "noteId", noteId), null, false);
            if (UtilValidate.isNotEmpty(PartyNote)) {
                ArrayList<String> guestList = new ArrayList<>();
                Map<String,Object> guestListMap = new HashMap<>();
                for (GenericValue partyNoteGv: PartyNote) {
                    guestList.add((String) partyNoteGv.get("partyId"));
                }
                noteInfoMap.put("guestList",guestList);
                noteInfoList.add(guestListMap);
            }
            List<GenericValue> activities = delegator.findAll("RecentActivity", false);
            String oldNoteId = null;
            String oldNoteAction = null;

            for (GenericValue activity : activities) {
                oldNoteId = (String) activity.get("activityTypeId");
                oldNoteAction = (String) activity.get("action");
            }
            if (noteId.equals(oldNoteId) && oldNoteAction.equals("VIEWED")) {
                System.out.println("should not create recent activity as this note is already viewed");
            } else if(recentActivityDeleteDone){
                recentActivityDeleteDone = false;
            }
            else {
                noteRecentActivity(dctx,noteParty,noteId,"VIEWED",noteName);
                recentActivityViewDone = true;
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        System.out.println("noteInfoList from note service"+noteInfoList);
        sendResp.put("privateNote",noteInfoList);
        return sendResp;
    }
    public static Map<String,Object> updatePrivateNote (DispatchContext dctx,Map<String,?extends  Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String academyId = (String) context.get("academyId");
        String noteId = (String) context.get("noteId");
        ArrayList<String> guestList = (ArrayList<String>) context.get("partyId");
        String noteName = (String) context.get("noteName");
        String note = (String) context.get("note");

        if(!noteId.isEmpty()){
            Map<String,Object> noteInfoMapInput = new HashMap<>();
            noteInfoMapInput.put("noteId",noteId);
            noteInfoMapInput.put("noteName",noteName);
            noteInfoMapInput.put("noteInfo",note);
            noteInfoMapInput.put("noteParty",academyId);
            GenericValue privateNoteGvs = null;
            List<GenericValue> partyNote = null;
            try {
                privateNoteGvs = delegator.findOne("NoteData",UtilMisc.toMap("noteId",noteId),false);
                if(UtilValidate.isNotEmpty(privateNoteGvs)){
                    privateNoteGvs.putAll(noteInfoMapInput);
                    privateNoteGvs.store();
                    try {
                        partyNote = EntityQuery.use(delegator).from("PartyNote").where("noteId",noteId).queryList();
                        if(UtilValidate.isNotEmpty(partyNote)) {
                            for (GenericValue partyNoteGv : partyNote) {
                                partyNoteGv.remove();
                            }
                        }
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    }
                    if(UtilValidate.isNotEmpty(guestList)){
                        Map<String,Object> partyNoteInputMap = new HashMap<>();
                        partyNoteInputMap.put("noteId",noteId);
                        for (String guestId : guestList){
                            partyNoteInputMap.put("partyId", guestId);
                            GenericValue partyNoteGvs = delegator.makeValue("PartyNote", partyNoteInputMap);
                            delegator.create(partyNoteGvs);
                        }
                    }
                }
                List<GenericValue> activities = delegator.findAll("RecentActivity", false);
                String lastActivityId = null;
                for (GenericValue activity : activities) {
                    lastActivityId = (String) activity.get("activityId");
                }
                try {
                    if (recentActivityViewDone) {
                        GenericValue recentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", lastActivityId), false);
                        recentActivityGenValue.remove();
                        noteRecentActivity(dctx,academyId,noteId,"UPDATED",noteName);
                    }
                } catch (GenericEntityException e) {
                    e.printStackTrace();
                }

            } catch (GenericEntityException e) {
                return ServiceUtil.returnFailure("Unable to update "+e.getMessage());
            }
        }
        return sendResp;
    }
    public static Map<String,Object> removePrivateNote (DispatchContext dctx,Map<String,?extends Object> context){
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> sendResp = ServiceUtil.returnSuccess();

        String noteParty = (String) context.get("partyId");
        String noteId = (String) context.get("noteId");
        String academyId = (String) context.get("academyId");
        List<GenericValue> partyNote = null;
        try {
            GenericValue privateNoteGv = delegator.findOne("NoteData",UtilMisc.toMap("noteId",noteId),false);
            if (UtilValidate.isNotEmpty(privateNoteGv)){
                partyNote = EntityQuery.use(delegator).from("PartyNote").where("noteId",noteId).queryList();
                if(UtilValidate.isNotEmpty(partyNote)){
                    for (GenericValue partyNoteGvs : partyNote){
                        partyNoteGvs.remove();
                    }
                }
                privateNoteGv.remove();
            }else {
                return ServiceUtil.returnFailure("Unable to delete note");
            }
            List<GenericValue> activities = delegator.findAll("RecentActivity", false);
            String lastActivityId = null;
            for (GenericValue activity : activities) {
                lastActivityId = (String) activity.get("activityId");
            }
            try {
                if (recentActivityViewDone) {
                    GenericValue recentActivityGenValue = delegator.findOne("RecentActivity", UtilMisc.toMap("activityId", lastActivityId), false);
                    recentActivityGenValue.remove();
                    noteRecentActivity(dctx,noteParty,noteId,"DELETED",privateNoteGv.getString("noteName"));
                    recentActivityDeleteDone = true;
                }
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }

        } catch (GenericEntityException e) {
            return ServiceUtil.returnFailure("Unable to delete note"+e.getMessage());
        }
        sendResp.put(ModelService.RESPONSE_MESSAGE,"note"+noteId+"is deleted");
        return sendResp;
    }

    public static Map<String, Object> noteRecentActivity(DispatchContext dispatchContext, String academyId, String noteId,String action, String activityTypeInfo ) {
        LocalDispatcher localDispatcher = dispatchContext.getDispatcher();
        Map sendResponse = ServiceUtil.returnSuccess();
        Date now = new Date();
        Timestamp activityCreatedDate = new Timestamp(now.getTime());
        Map<String, Object> createRecentActivity = new HashMap<>();
        createRecentActivity.put("academyId", academyId);
        createRecentActivity.put("activityType", "NOTE");
        createRecentActivity.put("activityTypeId", noteId);
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
