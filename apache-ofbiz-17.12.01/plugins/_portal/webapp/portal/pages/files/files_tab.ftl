<div class="filesPage">
    <div class="mt-2">
        <div class="row">
            <div class="col-md">
                <span class="float-left page-title">
                    <i class="fa fa-database text-secondary" aria-hidden="true"></i>
                    Uploaded files
                </span>
            </div>
            <div class="col-md">
                    <span class="float-right text-muted">Total available size ${planMaxDiskSize!} MB /
                        <span class="text-muted" id="diskSize"></span></span>
            </div>
        </div>
        <div class="p-4" id="filesTabPage">
            <img src="../static/lottiefiles/629-empty-box.gif" width="300" class="center"/>
            <div class="text-muted">No uploaded files found</div>
            <div>
                <a href="#" data-toggle="modal"
                   data-target="#newFileModal" class="btn btn-link">
                    <i class="fa fa-plus-circle" aria-hidden="true"></i> Add Now</a>
            </div>
        </div>
        <div class="row" id="filesTable">
            <div class="col-md pt-2">
                <table class="table table-hover border-dark table-responsive-md">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Uploaded date</th>
                        <th>Size</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody id="contents">

                    <#--                        <#list contentList as content>-->
                    <#--                            <tr id="tableRowContent">-->
                    <#--                                <td id="content_index">-->
                    <#--&lt;#&ndash;                                                                                                ${content_index + 1}&ndash;&gt;-->
                    <#--                                </td>-->

                    <#--                                <td id="contentName">-->
                    <#--                                                                                                <i class="fa fa-file-o text-secondary" aria-hidden="true"></i>-->
                    <#--                                                                &lt;#&ndash;                                ${content.contentName!}&ndash;&gt;-->
                    <#--                                </td>-->
                    <#--                                <td id="mimeTypeId">-->
                    <#--&lt;#&ndash;                                    &lt;#&ndash;                            &lt;#&ndash;&lt;#&ndash;                                ${content.mimeTypeId!}&ndash;&gt;&ndash;&gt;&ndash;&gt;&ndash;&gt;-->
                    <#--                                </td>-->
                    <#--                                <td id="createdDate">-->
                    <#--&lt;#&ndash;                                                                                                ${content.createdDate?string.short!}&ndash;&gt;-->
                    <#--                                </td>-->
                    <#--                                <td id="size"></td>-->
                    <#--                                <td id="dropdownBtn">-->
                    <#--                                    <div class="dropdown">-->
                    <#--                                        <button class="btn btn-outline-primary btn-sm dropdown-toggle"-->
                    <#--                                                data-toggle="dropdown">-->
                    <#--                                            <i class="fa fa-list" aria-hidden="true"></i>-->
                    <#--                                        </button>-->
                    <#--                                        <div class="dropdown-menu">-->
                    <#--                                            <a class="dropdown-item" download>-->
                    <#--                                                <i class="fa fa-download text-primary" aria-hidden="true"></i>-->
                    <#--                                                Download-->
                    <#--                                            </a>-->
                    <#--                                            <a class="dropdown-item" data-target="#viewFileModal" data-toggle="modal">-->
                    <#--                                                <i class="fa fa-pencil-square-o text-info" aria-hidden="true"></i>-->
                    <#--                                                View-->
                    <#--                                            </a>-->
                    <#--                                            <a class="dropdown-item">-->
                    <#--                                                <i class="fa fa-share text-secondary" aria-hidden="true"></i>-->
                    <#--                                                Share-->
                    <#--                                            </a>-->
                    <#--                                            <a class="dropdown-item" data-toggle="modal" data-target="#fileDeleteModel"-->
                    <#--                                               onclick="getContentId(${content.contentId!})"-->
                    <#--                                            >-->
                    <#--                                                <i class="fa fa-trash-o text-danger" aria-hidden="true"></i>-->
                    <#--                                                Delete-->
                    <#--                                            </a>-->
                    <#--                                        </div>-->
                    <#--                                    </div>-->
                    <#--                                </td>-->
                    <#--                            </tr>-->
                    <#--                        </#list>-->
                    </tbody>
                </table>
            </div>
        </div>

        <div class="modal fade" id="shareModelInFiles" role="dialog">
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
                        <button type="button" class="btn btn-primary"
                                data-dismiss="modal" onclick="shareContentToStudents()">Share
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal fade" id="viewFileModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <form method="post" enctype='multipart/form-data' id="uploadForm">
                        <div class="modal-header">
                            <span class="page-title">
                                <i class="fa fa-upload text-primary"></i>
                                View/Edit file info
                            </span>
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                        </div>
                        <div class="modal-body">
                            <div class="container-fluid">
                                <div class="row mb-3">
                                    <label for="newFile">Choose a file to upload</label>
                                    <div class="custom-file">
                                        <input type="file" class="custom-file-input" name="file" id="newFile"
                                               required>
                                        <label class="custom-file-label" for="customFile">Choose file</label>
                                    </div>
                                </div>
                                <div class="row">
                                    <label for="newFileDesc">Description</label>
                                    <div class="input-group">
                                        <div class="input-group-prepend">
                                            <div class="input-group-text">
                                                <i class="fa fa-bars"></i>
                                            </div>
                                        </div>
                                        <textarea id="newFileDesc" placeholder="" rows="3"
                                                  class="form-control"></textarea>
                                    </div>
                                </div>
                                <div class="row mt-3">
                                    <div class="col-md-8 pr-0 pl-0">
                                        <select class="form-control">
                                            <option selected disabled>Choose a folder to save</option>
                                            <option>Folder-1</option>
                                            <option>Folder-2</option>
                                        </select>
                                    </div>
                                    <div class="col-md">
                                        <button class="btn btn-outline-primary">
                                            <i class="fa fa-folder-o" aria-hidden="true"></i>
                                            New folder
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary">Upload</button>
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete Modal -->
        <div class="modal fade" id="fileDeleteModel" role="dialog">
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
        <div class="row d-none border-top border-secondary mr-1 ml-1" id="thumbnailsView">
        </div>
    </div>
</div>