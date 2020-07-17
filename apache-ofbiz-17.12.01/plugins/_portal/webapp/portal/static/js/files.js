$(document).ready(function () {

    $(function () {
        if ($('div').is('.filesPage')) {
            $('#studentsFile').fastselect({placeholder: 'Choose Student'});
            getAllContentRecords();
        }
        $('#sharedFileTab').click(function () {
            fetchSharedFiles();
        });
    });

    $(".custom-file-input").on("change", function () {
        var fileName = $(this).val().split("\\").pop();
        $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
    });
    $('input[name=filesDispType]').change(function () {
        var dispType = $('input[name=filesDispType]:checked').val();
        console.log(dispType)
        if (dispType === "thumbnailsView") {
            $('#thumbnailsView').removeClass('d-none');
            $('#listView').addClass('d-none')
        } else {
            $('#listView').removeClass('d-none');
            $('#thumbnailsView').addClass('d-none')
        }
    });
    $('#newFileModal').on('hidden.bs.modal', function () {
        $('#file').val('');
        $('#newFileDesc').val('');
    })
});

var contentIdValue;
var fileIdValue;
var fileContents;
var fileName;
var studentData;
var studentNames;

function getContentIds(contentId, fileId) {
    contentIdValue = contentId;
    fileIdValue = fileId;
    $(fileContents).each(function (index, file) {
        if (file.contentId == contentId) {
            fileName = file.contentName;
        }
    })
}

function convertTimeStampToDate(dateTime) {
    var convertingTimestampToDate = new Date(dateTime);
    var day = convertingTimestampToDate.getDate();
    var month = convertingTimestampToDate.getMonth() + 1;
    var year = convertingTimestampToDate.getFullYear();
    return day + '/' + month + '/' + year;
}

function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return '0 Byte';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return Math.round(bytes / Math.pow(1024, i), 2) + ' ' + sizes[i];
}

function usedStorageTrack(files) {
    var filesSize = [];

    $(files).each(function (index, file) {
        filesSize.push(file.fileSize)
    });

    var totalSize = 0;

    if (files.length === 0) {
        $("#diskSize").append("Used storage : 0 MB");
    } else {
        $(filesSize).each(function (index, fileSize) {
            totalSize = totalSize + fileSize
        });

        var usedStorage = bytesToSize(totalSize);
        $("#diskSize").append('Used storage : ' + usedStorage);
    }
}

function getStudentNames(studentIds) {
    var fetchStudentData = getCompleteUrl("fetchAllStudents");
    $.ajax({
        async: false,
        type: "GET",
        url: fetchStudentData,
        success: function (response) {
            var student = response;
            let studentList = [];
            for (var i = 0; i < studentIds.length; i++) {
                for (var j = 0; j < student.length; j++) {
                    if (student[j].studentId === studentIds[i]) {
                        studentList.push(" "+student[j].firstName + " " + student[j].lastName);
                    }
                }
            }
            if (studentList.length > 0 && studentList.length <= 2) {
                studentNames = studentList
            } else if (studentList.length > 2) {
                var length = studentList.length - 2;
                var stdArray = [];
                for (i = 0; i <= 1; i++) {
                    stdArray.push(studentList[i])
                }
                studentNames = stdArray + ' & ' + ' <span class="border  border-success rounded-circle circleForStudentCountIcon">' + '+' + length + '</span>'
            }
        },
        dataType: "json",
    });
}

// uploading file using ajax code
/*function uploadFile() {
    var file = document.querySelector('[type=file]').files[0];
    var formData = new FormData();
    formData.append("uploadFile", file);
    console.log(file);
    console.log(formData);
    var xhr = new XMLHttpRequest();
    // xhr.open("POST", "uploadFile");
    // xhr.send(formData);

    var uploadFileUrl = getCompleteUrl("uploadFile");
    var description = $("#newFileDesc").val();
    console.log("description" + description);
    $.ajax({
        type: "POST",
        url: uploadFileUrl,
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        cache: false,
        ContentType : 'multipart/form-data',
        processData: false,
        contentType: false,
        encType : "multipart/form-data",
        beforeSend: function () {
            $("#loadImage2").css("display", "none");
        },
        success: function (response) {
            console.log(response);
            if (response.STATUS === "SUCCESS") {
                // $('#fileDeleteModel').modal('hide');
                bs4pop.notice('File uploaded successfully', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            } else {
                // $('#fileDeleteModel').modal('hide');
                bs4pop.notice('Not able to upload ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
            }
        },
        error: function (response) {
        },
        datatype: "json"
    })
}*/

function getAllContentRecords() {
    function getAllFile(files) {
        var isFileShared = false;
        var sharedStudentCount = 0;
        fileContents = files;

        $(files).each(function (index, file) {
            getSharedStudentIds(file.contentId);
            if (studentData.length === 0) {
                isFileShared = false;
            } else {
                isFileShared = true;
                sharedStudentCount = studentData.length;
            }

            var createdDate = convertTimeStampToDate(file.createdDate);
            var fileSize = bytesToSize(file.fileSize);
            isFileShared ?
                $("#contents").append('<tr>' +
                    '<td>' + ++index + '</td>' +
                    '<td><i id="fileTypeImage" class="'+ file.fileTypeImage +'"i/>&nbsp&nbsp'+ file.contentName + '</td>' +
                    '<td>' + (file.contentName).split('.').pop().toUpperCase() + '</td>' +
                    '<td>' + createdDate + '</td>' +
                    '<td>' + fileSize + '</td>' +
                    '<td><i class="dropdown"><button class="btn btn-outline-primary btn-sm dropdown-toggle" data-toggle="dropdown"  onclick="getContentIds(' + file.contentId + ',' + file.fileId + ')"> <i class="fa fa-list" aria-hidden="true"></i></button>' +
                    '<div class="dropdown-menu"><a class="dropdown-item" onclick="downloadUploadedFile(' + file.contentId + ')" download><i class="fa fa-download text-primary" aria-hidden="true"></i> Download</a>' +
                    '<a class="dropdown-item" data-toggle="modal" data-target="#shareModelInFiles" onclick="getSharedStudentIds(' + file.contentId + ')"><i class="fa fa-share text-secondary" aria-hidden="true"></i> Share</a>' +
                    '<a class="dropdown-item" data-toggle="modal" data-target="#fileDeleteModel"><i class="fa fa-trash-o text-danger" aria-hidden="true"></i> Delete</a>' +
                    '</div><span class="pl-3 sharedIcon" data-toggle="modal" data-target="#shareModelInFiles"  onclick="getSharedStudentIds(' + file.contentId + ');getContentIds(' + file.contentId + ',' + file.fileId + ')" ><i class="fa fa-share-alt" id="shareIconIndicator"><span class="badge badge-notify badgeIcon badge-pill shareBadge">' + sharedStudentCount + '</span></i></span></a></div></td>' +
                    '</tr>')
                :
                $("#contents").append('<tr>' +
                    '<td>' + ++index + '</td>' +
                    '<td><i id="fileTypeImage" class="'+ file.fileTypeImage +'"i/>&nbsp&nbsp' + file.contentName + '</td>' +
                    '<td>' + (file.contentName).split('.').pop().toUpperCase() + '</td>' +
                    '<td>' + createdDate + '</td>' +
                    '<td>' + fileSize + '</td>' +
                    '<td><div class="dropdown"><button class="btn btn-outline-primary btn-sm dropdown-toggle" data-toggle="dropdown"  onclick="getContentIds(' + file.contentId + ',' + file.fileId + ')"> <i class="fa fa-list" aria-hidden="true"></i></button>' +
                    '<div class="dropdown-menu"><a class="dropdown-item" onclick="downloadUploadedFile(' + file.contentId + ')" download><i class="fa fa-download text-primary" aria-hidden="true"></i> Download</a>' +
                    '<a class="dropdown-item" data-toggle="modal" data-target="#shareModelInFiles" onclick="getSharedStudentIds(' + file.contentId + ')"><i class="fa fa-share text-secondary" aria-hidden="true"></i> Share</a>' +
                    '<a class="dropdown-item" data-toggle="modal" data-target="#fileDeleteModel"><i class="fa fa-trash-o text-danger" aria-hidden="true"></i> Delete</a>' +
                    '</div><span class="pl-3 unSharedIcon" data-toggle="modal" data-target="#shareModelInFiles"  onclick="getSharedStudentIds(' + file.contentId + ');getContentIds(' + file.contentId + ',' + file.fileId + ')"><i class="fa fa-share-alt" id="shareIconIndicator"><span</div></td>' +
                    '</tr>')
        })
    }

    $.ajax({
        type: "GET",
        url: "fetchAllContentRecords",
        datatype: "json",
        success: function (response) {

            $('#filesTabPage').css("display","none");
            $('#filesTable').css("display","block");

            var files = JSON.parse(response);
            if(files.length != 0) {
                getAllFile(files);
                $("#diskSize").empty();
                usedStorageTrack(files)
            }
            else{
                $('#filesTabPage').css("display","block");
                $('#filesTable').css("display","none")
            }
        }
    })
}

function downloadUploadedFile(contentId) {
    var fileNameValue;
    $(fileContents).each(function (index, file) {
        if (file.contentId == contentId) {
            fileNameValue = file.contentName;
        }
    });
    var downloadFileUrl = getCompleteUrl("downloadFile");

    $.ajax({
        type: "GET",
        url: downloadFileUrl,
        data: {contentId: contentId},
        datatype: "json",
        xhrFields: {
            responseType: 'blob'
        },
        success: function (response) {
            var a = document.createElement('a');
            var url = window.URL.createObjectURL(response);
            a.href = url;
            a.download = fileNameValue;
            document.body.append(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        }
    })
}

function deleteUploadedFile() {
    var deleteFileUrl = getCompleteUrl("deleteContentRecord");

    $.ajax({
        type: "POST",
        url: deleteFileUrl,
        data: {contentId: contentIdValue, fileId: fileIdValue},
        datatype: "json",
        success: function (response) {
            if (response.STATUS === "SUCCESS") {
                $('#fileDeleteModel').modal('hide');
                $("#contents").empty();
                getAllContentRecords();
                bs4pop.notice('File deleted successfully', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            } else {
                $('#fileDeleteModel').modal('hide');
                bs4pop.notice('Not able to delete ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
            }
        }
    })
}

function shareContentToStudents() {
    var studentIds = $('#studentsFile').val();
    var contentSharingUrl = getCompleteUrl("createContentSharing");

    $.ajax({
        type: "POST",
        url: contentSharingUrl,
        data: {contentId: contentIdValue, partyId: studentIds, fileName: fileName},
        datatype: "json",
        success: function (response) {
            $("#contents").empty();
            getAllContentRecords();
            if (response.STATUS === "SUCCESS") {
                bs4pop.notice('File shared successfully', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            } else {
                bs4pop.notice('Not able to share the files ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
            }
        }
    })
}

function getSharedStudentIds(contentId) {
    contentIdValue = contentId;
    var getSharedStudentsUrl = getCompleteUrl("getSharedStudents");

    $.ajax({
        async: false,
        type: "POST",
        url: getSharedStudentsUrl,
        data: {contentId: contentIdValue},
        datatype: "json",
        success: function (response) {
            var studentIds = JSON.parse(response);
            studentData = studentIds;
            $("#studentsFile").data('fastselect').destroy();
            $('#studentsFile').val(studentIds);
            $("#studentsFile").fastselect().trigger('change');
        }
    })
}

function fetchSharedFiles() {
    var fetchSharedFilesUrl = getCompleteUrl("fetchSharedFiles");

    $.ajax({
        async: false,
        type: "GET",
        url: fetchSharedFilesUrl,
        datatype: "json",
        beforeSend: function () {
            $('#sharedFiles').empty();
            $('.sharedFilesTabPage').css("display","block");
            $('#noSharedFilesFound').css("display","none")
        },
        success: function (response) {
            var sharedFiles = JSON.parse(response);
            if (sharedFiles.length != 0) {
                $(sharedFiles).each(function (index, file) {
                    var lastModifyDate = convertTimeStampToDate(file.lastModifyDate);
                    var date = new Date(file.lastModifyDate);
                    var time = date.toLocaleTimeString();
                    lastModifyDate = lastModifyDate + " " + time;
                    getStudentNames(file.partyId);
                    $('#sharedFiles').append('<tr>' +
                        '<td><i id="fileTypeImage" class="'+ file.fileTypeImage +'"i/>&nbsp&nbsp' + file.contentName + '</td>' +
                        '<td>' + lastModifyDate + '</td>' +
                        '<td>' + studentNames + '</td>' +
                        '<td><a onclick="downloadUploadedFile(' + file.contentId + ')" download><i class="fa fa-download text-primary downloadIcon" aria-hidden="true"></i></a></td>' +
                        '</div></div></tr>')
                })
            } else {
                $('.sharedFilesTabPage').css("display","none");
                $('#noSharedFilesFound').css("display","block")
            }
        }
    })
}