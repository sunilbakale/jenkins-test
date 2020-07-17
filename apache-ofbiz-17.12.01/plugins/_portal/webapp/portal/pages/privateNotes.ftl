<div class="container-fluid">
    <div class="nav navbar bg-light pl-0 pr-0">
        <a href="<@ofbizUrl>new_private_note</@ofbizUrl>" class="btn btn-primary">
            <i class="fa fa-plus-circle" aria-hidden="true"></i> New Note</a>

        <div class="input-group-append float-right">
            <input type="text" class="form-control col-md-8" placeholder="Search Notes.." id="searchNotes"/>
            <button class="btn btn-secondary" type="button">
                <i class="fa fa-search"></i>
            </button>
        </div>
    </div>
    <div class="container-fluid">
        <#if privateNote?size !=0>
        <#list privateNote as pvtNote>
                <div class="card-counter border pb-3 pt-3" style="width: 12rem;display: inline-block;" title="Created on : ${pvtNote.createdStamp!?date}">
                    <h4 class="card-header p-0 bg border-bottom" style="text-transform: capitalize">
                        ${pvtNote.title}</h4>
                    <p class="card-text" style="height: 4.0em;overflow: hidden;text-overflow: ellipsis;display: -webkit-box;-webkit-line-clamp: 3;-webkit-box-orient: vertical;">${pvtNote.summary!?string}</p>
                        <a href="#" data-toggle="modal" data-target="#pvtNoteDelModal"
                           class="btn btn-outline-danger btn-sm float-left"
                           title="Delete this note"
                            data-privatenote-Id="${pvtNote.noteId}"><span class="fa fa-trash-o"></span></a>
                        <a href="<@ofbizUrl>edit_private_note</@ofbizUrl>?privateNoteId=${pvtNote.noteId}"
                           class="btn btn-success btn-sm float-right"
                           title="More Info about this note">View Note <span class="fa fa-arrow-right"></span> </a>
                </div>
        </#list>
            <form>
                <input type="hidden" id="formPrivateNoteId">
            </form>
            <#else>
                <div class="page-title text-muted"><center> Notes you add appear here</center></div>
        </#if>
    </div>
</div>
<div class="modal fade" id="pvtNoteDelModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h4 class="page-title">Delete Note</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body">
                Are you sure you want to delete this Note ?
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-sm" onclick="deletePrivateNote()">Yes</button>
                <button type="button" class="btn btn-danger btn-sm" data-dismiss="modal">No</button>
            </div>

        </div>
    </div>
</div>

