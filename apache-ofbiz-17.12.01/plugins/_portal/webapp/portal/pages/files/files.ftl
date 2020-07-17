<div class="container-fluid pr-0 pl-0" >
    <div class="nav navbar rounded float-right">
        <div class="input-group-append float-left">
            <div class="btn-group btn-group-toggle mr-1" data-toggle="buttons">
                <label class="btn btn-outline-primary active">
                    <input type="radio" name="filesDispType" value="listType" autocomplete="off">
                    <i class="fa fa-bars" aria-hidden="true"></i>
                </label>
                <label class="btn btn-outline-primary">
                    <input type="radio" name="filesDispType" value="thumbnailsView" autocomplete="off">
                    <i class="fa fa-th-large" aria-hidden="true"></i>
                </label>
            </div>
            <button type="button" class="btn btn-primary mr-2" data-toggle="modal"
                    data-target="#newFileModal">
                <i class="fa fa-upload" aria-hidden="true"></i>
                Upload file
            </button>
            <div class="modal fade" id="newFileModal" tabindex="-1">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <span class="page-title">
                                <i class="fa fa-upload text-primary"></i>
                                Upload a new file
                            </span>
                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                        </div>
                        <div class="modal-body">
                            <div class="container-fluid">
                                <form method="post" action="<@ofbizUrl>uploadFile</@ofbizUrl>"
                                      enctype='multipart/form-data' id="uploadForm">
                                    <div class="row mb-3">
                                        <label for="newFile">Choose a file to upload</label>
                                        <div class="custom-file">
                                            <input type="file" class="custom-file-input" name="file"
                                                   id="fileUploader"
                                                   required multiple/>
                                            <label class="custom-file-label" for="customFile">Choose file</label>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <label>Description</label>
                                        <div class="input-group">
                                            <div class="input-group-prepend">
                                                <div class="input-group-text">
                                                    <i class="fa fa-bars"></i>
                                                </div>
                                            </div>
                                            <textarea id="newFileDesc" name="description" placeholder="" rows="3"
                                                      class="form-control"></textarea>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="submit" class="btn btn-primary">Upload</button>
                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Nav tabs -->
    <ul class="pt-3 nav nav-tabs" role="tablist">
        <li class="active">
            <a class="nav-link active" data-toggle="tab" href="#files_tab">Files</a>
        </li>
        <li>
            <a class="nav-link" data-toggle="tab" href="#sharedFiles_tab" id="sharedFileTab">Shared</a>
        </li>
    </ul>

    <!-- Tab panes -->
    <div class="tab-content">
        <div id="files_tab" class="container tab-pane active cth-tab-container">
            ${screens.render("component://portal/widget/PortalScreens.xml#files_tab")}
        </div>
        <div id="sharedFiles_tab" class="container tab-pane fade cth-tab-container">
            ${screens.render("component://portal/widget/PortalScreens.xml#sharedFiles_tab")}
        </div>
    </div>
</div>
