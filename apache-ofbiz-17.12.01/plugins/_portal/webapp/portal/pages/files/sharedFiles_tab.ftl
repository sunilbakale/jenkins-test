<div style="text-align: center" class="p-4" id="noSharedFilesFound">
    <div class="text-muted">No shared files found</div>
</div>
<div class="sharedFilesTabPage">
    <div class="pt-2">
                <span class="float-left page-title">
                    <i class="fa fa-database text-secondary" aria-hidden="true"></i>
                    Shared files
                </span>
    </div>
    <div class="pt-2" id="sharedTabTable">
        <table class="table table-hover border-dark table-responsive-md">
            <thead>
            <tr>
                <th>Name</th>
                <th>Last Modified on</th>
                <th>Shared with</th>
                <th>Download</th>
            </tr>
            </thead>
            <tbody id="sharedFiles">

            </tbody>
        </table>
    </div>

    <div class="modal fade" id="shareModelInSharedTab" role="dialog">
        <div class="modal-dialog">

            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">Share File</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;
                    </button>
                </div>
                <div class="modal-body">
                    <p>Choose Students to share file with</p>
                    <select class="multipleSelect form-control" multiple id="studentsFile"
                            type="text">
                        <#list students as student>
                            <option value="${student.studentId!}"
                            >${student.firstName!} ${student.lastName!}
                            </option>
                        </#list>
                    </select>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" id=""
                            data-dismiss="modal" onclick="shareContentToStudents()">Share
                    </button>
                </div>
            </div>
        </div>
    </div>


    <!-- Delete Modal -->
    <div class="modal fade" id="fileDeleteModelInSharedTab" role="dialog">
        <div class="modal-dialog modal-sm">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">Delete File</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete this file?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success btn-sm"
                            onclick="deleteUploadedFile()">Yes
                    </button>
                    <button type="button" class="btn btn-danger btn-sm"
                            data-dismiss="modal">
                        No
                    </button>
                </div>
            </div>
        </div>
    </div>

</div>