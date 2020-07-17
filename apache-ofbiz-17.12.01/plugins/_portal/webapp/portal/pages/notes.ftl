<body class="notePage">
    <div class="container-fluid pl-1 ">
        <div class="row">
            <div class="col-3 pr-0">
                <div class="card pl-0 pt-o shadow-none border">
                    <ul class="list-group list-group-flush">
                        <li class="list-group-item">
                            <input type="search" class="form-control" placeholder="Search for notes.."
                                   id="searchNoteInput" onkeyup="searchNoteFunction();">
                        </li>
                        <li class="list-group-item pr-0">
                            <button class="btn btn-primary mr-2" id="newNoteButton">
                                <i class="fa fa-plus-circle" aria-hidden="true"></i> New Note
                            </button>
                        </li>
                        <div class="card" id="emptyNotes">
                        </div>
                        <div class="card" id="listOfNotes">
                        </div>
                        <div id="loadImage">
                            <img src='../static/images/loading.gif' alt="Loading.." width="30" height="30"/>
                        </div>
                        <form>
                            <input type="hidden" id="formPrivateNoteId">
                        </form>
                    </ul>
                </div>
            </div>
            <div class="col-9 p-0">
                <div class="col-12 card shadow-none border p-0" id="rightSideNoteDetails">
                    <div class="float-left pt-1 pr-4">
                        <div class="row">
                            <div class="col-6">
                                <input type="hidden" value="" id="privateNoteId">
                                <div class="form-group">
                                    <div class="input-group">
                                        <div class="input-group-prepend">
                                            <div class="input-group-text">
                                                <i class="fa fa-tag"></i>
                                            </div>
                                        </div>
                                        <input type="text" value="" class="form-control" placeholder="Title"
                                               id="pvtNoteTitle" required>
                                    </div>
                                </div>
                            </div>
                            <div class="col-6">
                                <button type="button" class="btn btn-secondary float-right" data-toggle="modal"
                                        data-target="#shareModelInNotes"><i class="fa fa-share"></i>&nbsp; Share
                                </button>

                                <p class="pr-2 text-muted pull-right" id="studentNames"></p>
                                <div class="modal fade" id="shareModelInNotes" role="dialog">
                                    <div class="modal-dialog">

                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h4 class="modal-title">Share Note</h4>
                                                <button type="button" class="close" data-dismiss="modal">&times;
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <p>Choose Students to share note with</p>
                                                <select class="multipleSelect form-control" multiple id="studentsNote"
                                                        type="text">
                                                    <#list studentslist as stdList>
                                                        <option value="${stdList.studentId!}"
                                                        >${stdList.firstName!} ${stdList.lastName!}
                                                        </option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-primary" id="shareBtnCreate"
                                                        data-dismiss="modal" onclick="shareButtonForCreating();">Share
                                                </button>
                                                <button type="button" class="btn btn-primary" id="shareBtnUpdate"
                                                        data-dismiss="modal" onclick="shareButtonForUpdating();"
                                                        >Share
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group pt-3">
                                <textarea class="form-control textAreaforNote" rows="25" placeholder=""
                                          id="pvtNoteSummary"></textarea>
                        </div>
                        <div id="loadImage2">
                            <img src='../static/images/loading.gif' alt="Loading.." width="30" height="30"/>
                        </div>
                        <div id="changeToCreate" class="pl-1">
                            <button type="button" class="btn btn-primary btn-lg" onclick="saveNote()" id="createBtn" return="false">Create
                            </button>
                        </div>
                        <div id="changeToUpdate" class="pl-1">
                            <div class="float-left">
                                <button type="button" class="btn btn-primary btn-lg" onclick="updateNote()" return="false">Save</button>
                                <div class="text-muted">
                                    <button type="button" class="btn text-danger" data-toggle="modal"
                                            data-target="#noteDeleteModal"><span class="fa fa-trash-o"></span></button>
                                    <div class="modal fade" id="noteDeleteModal" role="dialog">
                                        <div class="modal-dialog modal-sm">
                                            <div class="modal-content">
                                                <div class="modal-header">

                                                    <h4 class="modal-title">Delete Note</h4>
                                                    <button type="button" class="close float-right" data-dismiss="modal">
                                                        &times;
                                                    </button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Are you sure you want to delete this Note?</p>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-success btn-sm"
                                                            onclick="deleteNote()">Yes
                                                    </button>
                                                    <button type="button" class="btn btn-danger btn-sm"
                                                            data-dismiss="modal">
                                                        No
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <span id="createdOn"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
