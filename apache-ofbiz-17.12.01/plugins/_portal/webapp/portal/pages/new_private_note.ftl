<div >
    <nav class="nav navbar cth_page_heading bg-light" >
        <div class="page-title " id="loadNoteTilteChange">Note Details</div>
    </nav>
    <br/>
    <div class="container float-left">
        <div class="row">
            <div class="col-4">
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text">
                                <i class="fa fa-tag"></i>
                            </div>
                        </div>
                        <input type="text" class="form-control" placeholder="Title" id="pvtNoteTitle" required>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="form-group">
                    <div class="input-group">
                        <div class="input-group-prepend" style="font-size: 15px;width: 100%">
                            <div class="input-group-text">
                                <i class="fa fa-user" aria-hidden="true"></i>
                            </div>
                            <select class="multipleSelect form-control" multiple id="studentsNote" type="text">
                                <#if studentslist!?size !=0>
                                    <#list studentslist as stdList>
                                        <option value="${stdList.studentId!}">${stdList.firstName!} ${stdList.lastName!} </option>
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

    </div>
</div>
<div id="changeToNewNoteCreate">
    <button type="button" class="btn btn-primary float-right" onclick="saveNote();">Create</button>
    <a class="btn btn-secondary" href="<@ofbizUrl>notes</@ofbizUrl>">
        <i class="fa fa-caret-left" aria-hidden="true"></i> Cancel</a>
</div>
<div class="page-title" id="loadNewNoteTilteChange">New Note</div>