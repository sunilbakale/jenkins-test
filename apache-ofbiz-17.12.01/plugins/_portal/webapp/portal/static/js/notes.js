$(document).ready(function () {
    $(function () {
        if ($('body').is('.notePage')) {
            $('#studentsNote').fastselect({placeholder: 'Choose Student'});
            CKEDITOR.replace("pvtNoteSummary", {height: 400});
            getAllNotes();
        }
    });

    $('#pvtNoteTitle').on("keypress", function () {
        $(this).removeClass('border-danger');
    })
    $(document).ajaxStart(function () {
        $("#loadImage").css("display", "block");
        $("#loadImage2").css("display", "block");
    });
    $(document).ajaxComplete(function () {
        $("#loadImage").css("display", "none");
        $("#loadImage2").css("display", "none");
    });
    $('#newNoteButton').click(function () {
        clearNoteFormData();
        getAllNotes();
        $('#changeToUpdate').css('display', 'none');
        $('#changeToCreate').css('display', 'block');
        $('#shareBtnUpdate').css('display', 'none');
        $('#shareBtnCreate').css('display', 'block');
    })
    $('#listOfNotes').click(function () {
        $('#changeToCreate').css('display', 'none');
        $('#changeToUpdate').css('display', 'block');
        $('#shareBtnCreate').css('display', 'none');
        $('#shareBtnUpdate').css('display', 'block');
    })
    $('#createBtn').click(function () {
        if ($("#pvtNoteTitle").hasClass('border-danger')) {
            $('#changeToCreate').css('display', 'block');
        } else {
            $('#changeToCreate').css('display', 'none');
            $('#changeToUpdate').css('display', 'block');
            $('#shareBtnCreate').css('display', 'none');
            $('#shareBtnUpdate').css('display', 'block');
        }

    })
    $("#searchNoteInput").click(function () {
        $("#searchNoteInput").val("");
        getAllNotes();
    });
});

function highlightNote() {
    var listNotes = document.getElementById("listOfNotes");
    var btns = listNotes.getElementsByClassName("listBtn");
    for (var i = 0; i < btns.length; i++) {
        btns[i].addEventListener("click", function () {
            var current = document.getElementsByClassName("active");
            if (current.length > 0) {
                current[0].className = current[0].className.replace(" active", "active");
            }
            this.className += " active";
        });
    }
}

var isClickedOnCreateShare = false;

function saveNote() {
    var pvtNoteTitle = $('#pvtNoteTitle').val();
    if (pvtNoteTitle != "") {
        var editorInstance = CKEDITOR.instances["pvtNoteSummary"];
        var noteSummaryData = editorInstance.getData();
        if (isClickedOnCreateShare) {
            var studentList = $('#studentsNote').val();
        }

        var saveNoteData = {pvtNoteTitle: pvtNoteTitle, pvtNoteSummary: noteSummaryData, noteParty: studentList};
        var savePvtNoteUrl = getCompleteUrl("savePvtNote");
        $.ajax({
            type: "POST",
            url: savePvtNoteUrl,
            data: saveNoteData,
            beforeSend: function () {
                $("#loadImage2").css("display", "none");
            },
            success: function (response) {
                if (response.STATUS === "SUCCESS") {
                    isClickedOnCreateShare = false;
                    getAllNotes();
                    getNote(lastNoteId)
                    bs4pop.notice('Notes creates successfully', {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    })
                } else {
                    bs4pop.notice('Not able to create', {
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
    } else {
        $('#pvtNoteTitle').addClass('border-danger');
    }
}

var studentlistFromNote;
var isClickedOnUpdateShare = false;

function updateNote() {
    var privateNoteId = $('#privateNoteId').val();
    var pvtNoteTitle = $('#pvtNoteTitle').val();

    if (privateNoteId !== "" && pvtNoteTitle !== "") {
        var editorInstance = CKEDITOR.instances["pvtNoteSummary"];
        var noteSummaryData = editorInstance.getData();
        if (isClickedOnUpdateShare) {
            var studentList = $('#studentsNote').val();
        }
        var updatePvtNoteData = {
            pvtNoteTitle: pvtNoteTitle,
            pvtNoteSummary: noteSummaryData,
            privateNoteId: privateNoteId,
            noteParty: isClickedOnUpdateShare ? studentList : studentlistFromNote
        };

        var updatePvtNoteUrl = getCompleteUrl("updatePvtNote");
        $.ajax({
            type: "POST",
            url: updatePvtNoteUrl,
            data: updatePvtNoteData,
            beforeSend: function () {
                $("#loadImage2").css("display", "none");
            },
            success: function (response) {
                if (response.STATUS === "SUCCESS") {
                    isClickedOnUpdateShare = false;
                    bs4pop.notice('Note saved', {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    });
                    getAllNotes();
                } else {
                    bs4pop.notice('Error saving the note, please try again', {
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
    } else {
        $('#pvtNoteTitle').addClass('border-danger');
    }
}

function deleteNote() {
    var privateNoteId = $('#privateNoteId').val();
    var removePvtNote = getCompleteUrl("removePvtNote");
    $.ajax({
        type: "POST",
        url: removePvtNote,
        data: {privateNoteId: privateNoteId},
        beforeSend: function () {
            $("#loadImage2").css("display", "none");
        },
        success: function (response) {
            if (response.STATUS === "SUCCESS") {
                $('#noteDeleteModal').modal('hide');
                getAllNotes();
                bs4pop.notice('Note deleted successfully', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            } else {
                $('#noteDeleteModal').modal('hide');
                bs4pop.notice('Not able to delete ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
            }
            getNote(firstNoteId)
            $("#noteDocument").addClass("active");
            if (noteData.length == 0) {
                clearNoteFormData()
                $('#changeToUpdate').css('display', 'none');
                $('#changeToCreate').css('display', 'block');
                $('#shareBtnUpdate').css('display', 'none');
                $('#shareBtnCreate').css('display', 'block');
            }
        },
        error: function (response) {
        },
        datatype: "json"
    })
}

var lastNoteId;

function getNote(noteId) {
    var privateNoteId = noteId;
    var inputNoteId = {noteId: privateNoteId};
    var getNoteDataUrl = getCompleteUrl("getPrivateNote");

    function noteData(response) {

        $("#studentsNote").data('fastselect').destroy();
        $('#privateNoteId').val(response.noteId);
        $('#pvtNoteTitle').val(response.title);
        var guestList = response.guestListIds;
        studentlistFromNote = response.guestListIds;
        $('#studentsNote').val(guestList);
        $("#studentsNote").fastselect().trigger('change');
        $('#pvtNoteSummary').val(response.noteSummary);
        var editorInstance = CKEDITOR.instances['pvtNoteSummary'];
        editorInstance.setData(response.noteSummary);
        var convertingTimestamptoDate = new Date(response.noteDateTime);
        var day = convertingTimestamptoDate.getDate();
        var month = convertingTimestamptoDate.getMonth() + 1;
        var year = convertingTimestamptoDate.getFullYear();
        var noteCreatedDateValue = day + '/' + month + '/' + year;
        $('#createdOn').val(noteCreatedDateValue);

        var studentName = $('#studentNames');
        studentName.empty();
        if (response.guestListNames == undefined) {
            studentName.append('')
        } else {
            if (response.guestListNames.length > 0 && response.guestListNames.length <= 2) {
                studentName.append('Shared with ' + response.guestListNames)
            } else if (response.guestListNames.length > 2) {
                var length = response.guestListNames.length - 2;
                var stdArray = [];
                for (var i = 0; i <= 1; i++) {
                    stdArray.push(response.guestListNames[i])
                }
                studentName.append(stdArray + ' and ' + length + ' others ')
            }
        }

        var noteCreatedDate = $('#createdOn');
        noteCreatedDate.empty();
        noteCreatedDate.append('Created on ' + noteCreatedDateValue)
    }

    $.ajax({
        type: "GET",
        data: inputNoteId,
        url: getNoteDataUrl,
        beforeSend: function () {
            $("#loadImage").css("display", "none");
            $("#loadImage2").css("display", "bock");
        },
        success: function (response) {
            noteData(response);
        },
        complete: function () {
            $("#loadImage2").css("display", "none");
        },
        error: function () {
        },
        dataType: "json",
    });
}

var firstNoteId;
var noteData;

function getAllNotes() {

    var fetchAllNotesDataUrl = getCompleteUrl("fetchAllNotes");
    $.ajax({
        async: false,
        type: "GET",
        url: fetchAllNotesDataUrl,
        success: function (data) {
            var listBody = $('#emptyNotes');
            listBody.empty();
            noteData = data;
            if (data.length == 0) {
                $("#listOfNotes").css("display", "none");
                $("#emptyNotes").css("display", "block");
                listBody.append('<li class="page-title list-group-item">Notes you add appear here</li>');
            } else {
                firstNoteId = data[0].noteId;
                var listBody = $('#listOfNotes');
                listBody.empty();
                $(data).each(function (index, note) {
                    var id = note.noteId;
                    lastNoteId = id;
                    $("#emptyNotes").css("display", "none");
                    $("#listOfNotes").css("display", "block");
                    listBody.append('<li class="page-title list-group-item listBtn" onclick="getNote(' + id + ');highlightNote();" value=' + id + ' name="+noteId+" id="noteDocument">' + note.title + '</li>');

                })
            }
        },
        error: function (error) {
        },
        dataType: "json",
    });
}

function clearNoteFormData() {
    $('#studentsNote').val([]);
    $("#studentsNote").data('fastselect').destroy();
    $("#studentsNote").fastselect();
    var studentName = $('#studentNames');
    studentName.empty();
    document.getElementById('pvtNoteTitle').value = '';
    document.getElementById('createdOn').value = '';
    var editorInstance = CKEDITOR.instances['pvtNoteSummary'];
    editorInstance.setData('')
}

function searchNoteFunction() {
    var input = document.getElementById("searchNoteInput");
    var filter = input.value.toUpperCase();
    var divList = document.getElementById("listOfNotes");
    var li = divList.getElementsByTagName("li");
    for (var i = 0; i < li.length; i++) {
        var textContent = li[i]
        var searchValue = textContent.textContent || textContent.innerText;
        if (searchValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "block";
        } else {
            li[i].style.display = "none";
        }
    }
}

function shareButtonForUpdating() {
    isClickedOnUpdateShare = true;
    var privateNoteId = $('#privateNoteId').val();
    var pvtNoteTitle = $('#pvtNoteTitle').val();
    if (privateNoteId !== "" && pvtNoteTitle !== "") {
        var editorInstance = CKEDITOR.instances["pvtNoteSummary"];
        var noteSummaryData = editorInstance.getData();
        var studentList = $('#studentsNote').val();

        var updatePvtNoteData = {
            pvtNoteTitle: pvtNoteTitle,
            pvtNoteSummary: noteSummaryData,
            privateNoteId: privateNoteId,
            noteParty: studentList
        };
        var updatePvtNoteUrl = getCompleteUrl("updatePvtNote");
        $.ajax({
            type: "POST",
            url: updatePvtNoteUrl,
            data: updatePvtNoteData,
            beforeSend: function () {
                $("#loadImage").css("display", "none");
            },
            success: function (response) {
                getNote(privateNoteId);
            },
            datatype: "json"
        })
    }
}

function shareButtonForCreating() {
    isClickedOnCreateShare = true;
    var fetchStudentData = getCompleteUrl("fetchAllStudents");
    $.ajax({
        type: "GET",
        url: fetchStudentData,
        beforeSend: function () {
            $("#loadImage").css("display", "none");
        },
        success: function (response) {
            var std = response;
            var studentList = $('#studentsNote').val();
            var studentName = $('#studentNames');
            studentName.empty();
            var stdList = [];
            for (var i = 0; i < studentList.length; i++) {
                for (var j = 0; j < std.length; j++) {
                    if (std[j].studentId === studentList[i]) {
                        stdList.push(std[j].firstName + " " + std[j].lastName);
                    }
                }
            }
            if (stdList.length > 0 && stdList.length <= 2) {
                studentName.append('Shared with ' + stdList)
            } else if (stdList.length > 2) {
                var length = stdList.length - 2;
                var stdArray = [];
                for (i = 0; i <= 1; i++) {
                    stdArray.push(stdList[i])
                }
                studentName.append(stdArray + ' and ' + length + ' others ')
            } else {
                studentName.append('')
            }
        },
        dataType: "json",
    });
}