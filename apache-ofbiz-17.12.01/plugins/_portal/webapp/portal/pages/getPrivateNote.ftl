<div class="container-fluid" id="leftContent">
    <nav class="nav navbar cth_page_heading bg-light">
        <div class="page-title">Note: ${noteInfo.noteId!}</div>
<#--        <a class="nav-link  btn btn-secondary" href="<@ofbizUrl>notes</@ofbizUrl>" >-->
<#--            <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>-->
    </nav><br/>
    <div class="container float-left">
        <div class="row">
            <div class="col-4">
                <input type="hidden" value="${noteInfo.noteId!}" id="privateNoteId">
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text">
                                <i class="fa fa-tag"></i>
                            </div>
                        </div>
                        <input type="text" value="${noteInfo.title!}" class="form-control" placeholder="Title" id="pvtNoteTitleUpdate" required>
                    </div>
                </div>
            </div>
            <div class="col-4">
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-prepend" style="font-size: 15px;width: 100%">
                            <div class="input-group-text">
                                <i class="fa fa-user" aria-hidden="true"></i>
                            </div>
                            <select class="multipleSelect form-control" multiple id="studentsNote" type="text">
                                <#if studentslist!?size !=0>
                                    <#list studentslist as stdList>
                                        <option value="${stdList.studentId!}"
                                                <#if partyNote!?size != 0>
                                                    <#list partyNote as partyId>
                                                        <#if stdList.studentId == partyId>
                                                            selected
                                                        </#if>
                                                    </#list>
                                                </#if>
                                        >${stdList.firstName!} ${stdList.lastName!}
                                        </option>
                                    </#list>
                                <#else>
                                    <span>No students found</span>
                                </#if>
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-8">
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text">
                                <i class="fa fa-align-justify" aria-hidden="true"></i>
                            </div>
                        </div>
                        <textarea class="form-control" rows="15" placeholder="" id="pvtNoteSummaryUpdate">${privateNoteList.summary!}</textarea>
                    </div>

                </div>
                <button type="button" class="btn btn-primary" onclick="updatePvtNote()">Update</button>
                <a href="<@ofbizUrl>notes</@ofbizUrl>" class="btn btn-secondary">Cancel</a>
                <div class="text-muted float-right">
                    <button class="btn btn-sm text-danger" title="Click to delete note ">
                        <span class="fa fa-trash-o"></span>
                    </button>
                    <#if partyNote!?size == 0>
                        <i class="fa fa-user-secret text-primary" aria-hidden="true" title="Private Note"></i>
                    <#else>
                        <i class="fa fa-user-o text-secondary" aria-hidden="true" title="Shared Note"></i>
                    </#if>
                    <span id="createdOn">Created on: ${noteInfo.summary!}</div></div>
            </div>
        </div>
    </div>
</div>

