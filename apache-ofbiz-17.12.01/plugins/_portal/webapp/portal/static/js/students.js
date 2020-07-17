$(function () {
    initializeStudentData();
});

$(document).ready(function () {
    $(function () {
        if ($('body').is('#editStudentPage')) {
            getStudent()
        }
    });
    $('#createEvtName').on('keypress', function () {
        $("#errMsg").addClass('hide')
    })
    $('#createEventMdl').on('hidden.bs.modal', function () {
        $('#add-event-form')[0].reset();
        $('#add-event-form').trigger('reset');
        $('#repeat_select').prop('selectedIndex', 0).trigger('change');
    });
    $('#newEvent').on('click', function () {
        var curDate = new Date();
        $('#new_event_start_date').val(curDate.toLocaleDateString()).trigger('change')
        $('#new_event_end_date').val(curDate.toLocaleDateString()).trigger('change')
        var curTime = roundQuarter();
        var amOrPm = (curTime.getHours() < 12) ? "AM" : "PM";
        var hours = curTime.getHours();
        hours = hours % 12;
        hours = hours ? hours : 12;
        var startMin = curTime.getMinutes();
        startMin = startMin > 9 ? startMin : '0' + startMin;
        var startTimeDisp = hours + ":" + startMin + " " + amOrPm;
        $('#new_event_start_time').val(startTimeDisp)

        var endhours = curTime.getHours() + 1;
        curTime.setHours(endhours);
        var endTimeAmPm = curTime.getHours() < 12 ? 'AM' : 'PM';
        endhours = endhours % 12;
        endhours = endhours ? endhours : 12;
        var endMin = curTime.getMinutes();
        endMin = endMin > 9 ? endMin : '0' + endMin;

        var endTimeDisp = endhours + ":" + endMin + " " + endTimeAmPm;
        $('#new_event_end_time').val(endTimeDisp)
    })

    $("#searchStudent").on("keyup", function () {
        var value = $(this).val().toLowerCase();
        $("#studentsTable *").filter(function () {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
});

function clearStudentDataBeforeUpdate() {
    var studentName = $('#studentName');
    var studentMobileNumber = $('#studentMobileNumber');
    var studentEmail = $('#studentEmail');
    studentName.empty();
    studentMobileNumber.empty();
    studentEmail.empty();
}

function initializeStudentData() {
    $('#del_std_modal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var studentId = button.data("student-id");
        $("#studentExpenseId").val(studentId)
    })
};

function deleteStudent() {
    var studentId = $("#studentExpenseId").val();
    var postData = {studentId: studentId}
    var remove_student = getCompleteUrl("remove_student");
    $.ajax({
        type: "POST",
        data: postData,
        url: remove_student,
        success: function (response) {
            if (response.STATUS == "SUCCESS") {
                $('#del_std').modal('hide');
                bs4pop.notice('Student Deleted Successful', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
                location.reload();
            } else {
                $('#del_std').modal('hide');
                bs4pop.notice('Unable to Delete Student', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            }
        },
        error: function (response) {
        },
        dataType: "json",
    });
}

function getStudent(studentId) {
    studentId = $('#studentId').val();

    function studentDetails(student) {
        $("#studentEditBtn").css("display", "block");
        $("#studentName").append(student.firstName + ' ' + student.lastName);
        student.mobile !== undefined ? $('#studentMobileNumber').append(student.mobile) : $('#studentMobileNumber').append('N/A')
        student.emailAddress !== undefined ? $('#studentEmail').append(student.emailAddress) : $('#studentEmail').append("N/A")
    }

    var fetchStudentUri = getCompleteUrl('fetchStudentData');
    $.ajax({
        type: "GET",
        data: {student_id: studentId},
        url: fetchStudentUri,
        beforeSend: function () {
            $('#loadingIcon').css('display', 'block');
        },
        success: function (response) {
            clearStudentDataBeforeUpdate();
            studentDetails(response)
        },
        complete: function () {
            $('#loadingIcon').css('display', 'none');
        },
        error: function (response) {
        },
        dataType: "json",
    });
}

function getStudentForEdit(studentId) {
    studentId = $('#studentId').val();

    function editStudent(student) {
        $('#StudentFirstName').val(student.firstName);
        $('#StudentSecondName').val(student.lastName);
        $('#Studentemail').val(student.emailAddress);
        $('#StudentPhone').val(student.mobile);
    }

    var fetchStudentsUri = getCompleteUrl('fetchStudentData');
    $.ajax({
        type: "GET",
        data: {student_id: studentId},
        url: fetchStudentsUri,
        beforeSend: function () {
            $('#loadingIcon2').css('display', 'block');
        },
        success: function (response) {
            editStudent(response)
        },
        complete: function () {
            $('#loadingIcon2').css('display', 'none');
        },
        error: function (response) {
        },
        dataType: "json",
    });
}

function updateStudent() {
    var studentId = $('#studentId').val();
    var firstName = $('#StudentFirstName').val();
    var lastName = $('#StudentSecondName').val();
    var phone = $('#StudentPhone').val();
    if (firstName != "" && lastName != "") {
        $('#StudentFirstName').removeClass('border-danger')
        $('#StudentSecondName').removeClass('border-danger')
        var updateStudentUri = getCompleteUrl('updateStudent');
        var updateStudentData = {
            studentId: studentId,
            firstName: firstName,
            lastName: lastName,
            mobile: phone
        }
        $.ajax({
            type: "POST",
            data: updateStudentData,
            url: updateStudentUri,
            success: function (response) {
                getStudent(studentId)
                if (response.STATUS === "SUCCESS") {
                    $('#editStudentModel').modal('hide');
                    bs4pop.notice('Student details saved', {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    })

                } else {
                    $('#editStudentModel').modal('hide');
                    bs4pop.notice('Error saving the student details, please try again', {
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
            dataType: "json",
        });
    }
    else
    {
        firstName =="" ? $('#StudentFirstName').addClass('border-danger') : $('#StudentFirstName').removeClass('border-danger')
        lastName =="" ? $('#StudentSecondName').addClass('border-danger') : $('#StudentSecondName').removeClass('border-danger')
    }
}

var lastStudentId

function fetchAllStudents() {
    var fetchAllStudentsUri = getCompleteUrl('fetchAllStudents');
    $.ajax({
        type: "GET",
        url: fetchAllStudentsUri,
        async: false,
        success: function (response) {
            // getting last student id of academy
            lastStudentId = response[response.length - 1].studentId
        },
        error: function (response) {
        },
        dataType: "json",
    });
}

function createStudent() {

    var firstName = $('#StudentFirstName').val();
    var lastName = $('#StudentSecondName').val();
    var email = $('#StudentEmail').val();
    var phone = $('#StudentPhone').val();
    if(firstName != "" && lastName !="" && email != "") {
        var createStudentUri = getCompleteUrl('create_student');
        var newStudentData = {
            firstName: firstName,
            lastName: lastName,
            email: email,
            mobile: phone
        }
        $.ajax({
            type: "POST",
            data: newStudentData,
            url: createStudentUri,
            success: function (response) {
                fetchAllStudents()
                if (response.STATUS === "SUCCESS") {
                    $('#newStudentModal').modal('hide');
                    clearStudentDataBeforeCreate()
                    bs4pop.notice('New student created', {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    })
                    location.href = "edit_student?student_id=" + lastStudentId;
                } else {
                    $('#newStudentModal').modal('hide');
                    bs4pop.notice(response._ERROR_MESSAGE_, {
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
            dataType: "json",
        });
    }
    else
    {
        firstName =="" ? $('#StudentFirstName').addClass('border-danger') : $('#StudentFirstName').removeClass('border-danger')
        lastName =="" ? $('#StudentSecondName').addClass('border-danger') : $('#StudentSecondName').removeClass('border-danger')
        email == "" ? $('#StudentEmail').addClass('border-danger') :  $('#StudentEmail').removeClass('border-danger')
    }
}
function  clearStudentDataBeforeCreate() {
    $('#StudentFirstName').val('');
    $('#StudentSecondName').val('');
    $('#StudentEmail').val('');
    $('#StudentPhone').val('');
}
function saveEvent() {
    $('#studentSelectedList').fastselect({placeholder: 'Choose Student'});
    var newEventInfo = {};
    $.each($('#add-event-form').serializeArray(), function (i, field) {
        newEventInfo[field.name] = field.value;
    });
    var ename = $('#createEvtName').val();
    if (ename === '' && $('#edesc').val() == '') {
        $('#errMsg').text("* Check input fields")
        $('#errMsg').addClass('visible')
    } else {
        var startDateTimePicker = $("#new_event_start_date").val();
        var startDate = new Date(startDateTimePicker).toDateString();
        var startTime = $('#new_event_start_time').val();
        var combStartDateTime = new Date(startDate + ' ' + startTime);
        var endDateTimePicker = $("#new_event_end_date").val();
        var endDate = new Date(endDateTimePicker).toDateString();
        var endTime = $('#new_event_end_time').val();
        var combEndDateTime = new Date(endDate + ' ' + endTime);
        newEventInfo.endDate = combEndDateTime.getTime();

        var studentSelectedValues = [];
        studentSelectedValues[0] = $('#guestStudent').val();

        var classType = $('.active_status:checked').val();

        var eventLoc = "ONLINE";
        var isPrivateEvent;
        if ($('#evtPrivacy').is(":checked")) {
            isPrivateEvent = "Y";
        } else {
            isPrivateEvent = "N";
        }
        var startDateVal = new Date($("#new_event_start_date").val()).getTime();
        newEventInfo.startDateVal = startDateVal;

        // var recEndVal = $('.untilSeletr').children("option:selected").val();
        var recurringType = $('#repeat_select').children("option:selected").val();
        switch (recurringType) {
            case "NEVER":
                var isRecurringEvent = "N";
                newEventInfo.isRecurringEvent = isRecurringEvent;
                break;
            case "DAILY":
                isRecurringEvent = "Y";
                newEventInfo.isRecurringEvent = isRecurringEvent;
                recurringType = "DAILY";
                newEventInfo.recurringType = recurringType;
                var until = $('#untilSeletr_daily').children("option:selected").val();
                console.log(until)

                if (until === 'FOR_EVER') {
                    newEventInfo.until = until;
                    var endDateRecurring = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = endDateRecurring.getTime();
                } else if (until === 'OCCURRENCE') {
                    var recOcur = $('#daily_ocr').val();
                    newEventInfo.recOcur = recOcur;
                    newEventInfo.until = until;

                    var recEvtEndDate = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                } else {
                    newEventInfo.until = until;
                    var recSpecDate = new Date($('#daily_TDate').val());
                    recSpecDate.setHours(23);
                    newEventInfo.recSpecDate = recSpecDate.getTime();
                    var recEvtEndDate = recSpecDate;
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                }
                break;
            case "WEEKLY":
                isRecurringEvent = "Y";
                newEventInfo.isRecurringEvent = isRecurringEvent;
                recurringType = 'WEEKLY';
                newEventInfo.recurringType = recurringType;
                var onSunday;
                if ($('#weekday-sun').is(":checked")) {
                    onSunday = "Y"
                    newEventInfo.onSunday = onSunday;
                } else {
                    onSunday = "N"
                    newEventInfo.onSunday = onSunday;
                }
                var onMonday;
                if ($('#weekday-mon').is(":checked")) {
                    onMonday = "Y"
                    newEventInfo.onMonday = onMonday;
                } else {
                    onMonday = "N"
                    newEventInfo.onMonday = onMonday;
                }
                var onTuesday;
                if ($('#weekday-tue').is(":checked")) {
                    onTuesday = "Y"
                    newEventInfo.onTuesday = onTuesday;
                } else {
                    onTuesday = "N"
                    newEventInfo.onTuesday = onTuesday;
                }
                var onWednesday;
                if ($('#weekday-wed').is(":checked")) {
                    onWednesday = "Y"
                    newEventInfo.onWednesday = onWednesday;
                } else {
                    onWednesday = "N"
                    newEventInfo.onWednesday = onWednesday;
                }
                var onThursday;
                if ($('#weekday-thu').is(":checked")) {
                    onThursday = "Y"
                    newEventInfo.onThursday = onThursday;
                } else {
                    onThursday = "N"
                    newEventInfo.onThursday = onThursday;
                }
                var onFriday;
                if ($('#weekday-fri').is(":checked")) {
                    onFriday = "Y"
                    newEventInfo.onFriday = onFriday;
                } else {
                    onFriday = "N"
                    newEventInfo.onFriday = onFriday;
                }
                var onSaturday;
                if ($('#weekday-sat').is(":checked")) {
                    onSaturday = "Y"
                    newEventInfo.onSaturday = onSaturday;
                } else {
                    onSaturday = "N"
                    newEventInfo.onSaturday = onSaturday;
                }
                var until = $('#untilSeletr_weekly').children("option:selected").val();
                if (until === 'FOR_EVER') {
                    newEventInfo.until = until;
                    var endDateRecurring = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = endDateRecurring.getTime();

                } else if (until === 'OCCURRENCE') {
                    var recOcur = $('#weekly_ocr').val();
                    newEventInfo.recOcur = recOcur;
                    newEventInfo.until = until;

                    var recEvtEndDate = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                } else {
                    newEventInfo.until = until;
                    var recSpecDate = new Date($('#weekly_TDate').val());
                    recSpecDate.setHours(23);
                    newEventInfo.recSpecDate = recSpecDate.getTime();

                    var recEvtEndDate = recSpecDate;
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                }
                break;
            case "MONTHLY":
                console.log("MONTHLY")
                isRecurringEvent = "Y";
                newEventInfo.isRecurringEvent = isRecurringEvent;
                recurringType = 'MONTHLY';
                newEventInfo.recurringType = recurringType;
                var onMonthDay = $('#onMonthDay').val();
                newEventInfo.onMonthDay = onMonthDay;
                var until = $('#untilSeletr_monthly').children("option:selected").val();
                if (until === 'FOR_EVER') {
                    newEventInfo.until = until;
                    var endDateRecurring = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = endDateRecurring.getTime();
                } else if (until === 'OCCURRENCE') {
                    var recOcur = $('#monthly_ocr').val();
                    newEventInfo.recOcur = recOcur;
                    newEventInfo.until = until;

                    var recEvtEndDate = new Date("01/12/2030");
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                } else {
                    newEventInfo.until = until;
                    var recSpecDate = new Date($('#monthly_TDate').val());
                    recSpecDate.setHours(23);
                    newEventInfo.recSpecDate = recSpecDate.getTime();

                    var recEvtEndDate = recSpecDate;
                    newEventInfo.recEvtEndDate = recEvtEndDate.getTime();
                }
                break;
        }
        var edesc = $('#edesc').val();
        newEventInfo.edesc = edesc;

        // var endDateVal = new Date($('#new_event_end_date').val()).getTime();
        // newEventInfo.endDateVal = endDateVal;

        $('#errMsg').text("")
        newEventInfo.ename = ename;
        newEventInfo.startDate = combStartDateTime.getTime();

        newEventInfo.studentIds = studentSelectedValues;
        newEventInfo.classType = classType;
        newEventInfo.eventLoc = eventLoc;
        newEventInfo.isPrivateEvent = isPrivateEvent;
        newEventInfo.isRecurringEvent = isRecurringEvent;
        var createEventUrl = getCompleteUrl("createCalendarEvent");
    }
    $.ajax({
        type: "POST",
        url: createEventUrl,
        data: newEventInfo,
        success: function (response) {
            if (response.STATUS == "SUCCESS") {
                $('#createEventMdl').modal('hide');
                $('#calendar').fullCalendar('refetchEvents');

                bs4pop.notice('New Calendar Event Added', {
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
            } else {
                bs4pop.notice('Unable to add event');
            }
        },
        error: function (response) {
        },
        dataType: "json",
    });
};