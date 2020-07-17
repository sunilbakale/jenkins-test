<div class="p-5" style="margin: 0px;padding: 10px!important;width:100%;" xmlns="http://www.w3.org/1999/html"
     xmlns="http://www.w3.org/1999/html">
    <div class="card" style="width: 100%">
        <div class="card-body p-2">
            <div id="calendar"></div>
        </div>
    </div>
</div>

<!-- calendar modal -->
<div id="modal-view-event" class="modal modal-top fade calendar-modal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content cal_event_modal">
            <div class="modal-header">
                <div class="page-title"><i class="fa fa-tasks" aria-hidden="true"></i> Event Details</div>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="modal-body p-0">
                    <form id="edit-event-form">
                        <div class="form-group">
                            <label for="eName">Event name</label>
                            <input type="text" class="event-title form-control" id="eName" required>
                            <input type="hidden" id="event_no" class="eventId">
                        </div>
                        <div class="row pl-3 pr-2 mb-2">
                            <div class="form-check">
                                <label class="form-check-label" for="editEvtPrivacy">
                                    <input type="checkbox" class="form-check-input" id="editEvtPrivacy">Private Event
                                </label>
                            </div>
                        </div>
                        <div class="row pr-2 pl-2">
                            <div class="col-3 p-1">
<#--                                <label for="update_event_start_date">Start Date</label>-->
                                <input type='text' id="update_event_start_date" class="event-start-date form-control" autocomplete="off" required>
                            </div>
                            <div class="col-3 p-1">
<#--                                <label for="updateEvtStatTime">Start Time</label>-->
                                <input type='text' class="event-start-time form-control" id="updateEvtStatTime"required>
                            </div>
                            <div class="col-3 p-1">
<#--                                <label for="update_event_end_date">End Date</label>-->
                                <input type='text' id="update_event_end_date" class="event-end-date form-control" autocomplete="off" required>
                            </div>
                            <div class="col-3 p-1">
<#--                                <label for="updateEvtEndTime">End Time</label>-->
                                <input type='text' class="event-end-time form-control" autocomplete="off" id="updateEvtEndTime"required>
                            </div>
                        </div>
                        <div class="row pr-2 pl-2">
                            <div class="col-2 p-1 form-group">
                                <label for="repeat_select">Repeat</label>
                                <select class="div-toggle form-control" data-target=".editEvt_rptInfo" id="edit-event-repeat-select">
                                    <option value="NEVER" data-show="">No Repeat</option>
                                    <option value="DAILY" data-show=".dailyEdit">Daily</option>
                                    <option value="WEEKLY" data-show=".weeklyEdit">Weekly</option>
                                    <option value="MONTHLY" data-show=".monthlyEdit">Monthly</option>
                                </select>
                            </div>
                            <div class="col p-1 form-group">
                                <div class="editEvt_rptInfo">
                                    <div class="dailyEdit hide row pl-2 pr-2">
                                        <div class="container col">
                                            <label for="untilSeletr">Ends</label>
                                            <select class="div-toggle form-control untilSeletr" data-target=".editUntilSel_daily_info" id="editUntilSel_daily">
                                                <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                                <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                                <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                            </select>
                                        </div>
                                        <div class="container col">
                                            <div class="editUntilSel_daily_info">
                                                <#--<div class="FOR_EVER hide">FOR_EVER is...</div>-->
                                                <div class="OCCURRENCE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="daily_ocr_edit" min="0"/>
                                                </div>
                                                <div class="TILL_DATE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="text" id="daily_TDateEdit" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col"></div>
                                    </div>
                                    <div class="weeklyEdit hide row pr-1 pl-0">
                                        <div class="container col-6 ">
                                            <label for="weekSelector">Select days</label>
                                            <div class="weekDays-selector" id="<#--weekSelector-->">
                                                <input type="checkbox" id="edit-weekday-sun" class="weekday" value="onSunday" name="week"/>
                                                <label for="edit-weekday-sun">S</label>
                                                <input type="checkbox" id="edit-weekday-mon" class="weekday" value="onMonday" name="week"/>
                                                <label for="edit-weekday-mon">M</label>
                                                <input type="checkbox" id="edit-weekday-tue" class="weekday" value="onTuesday" name="week"/>
                                                <label for="edit-weekday-tue">T</label>
                                                <input type="checkbox" id="edit-weekday-wed" class="weekday" value="onWednesday" name="week"/>
                                                <label for="edit-weekday-wed">W</label>
                                                <input type="checkbox" id="edit-weekday-thu" class="weekday" value="onThursday" name="week"/>
                                                <label for="edit-weekday-thu">T</label>
                                                <input type="checkbox" id="edit-weekday-fri" class="weekday" value="onFriday" name="week"/>
                                                <label for="edit-weekday-fri">F</label>
                                                <input type="checkbox" id="edit-weekday-sat" class="weekday" value="onSaturday" name="week"/>
                                                <label for="edit-weekday-sat">S</label>
                                            </div>
                                        </div>
                                        <div class="container col-2 pl-0 pr-0">
                                            <label for="endsOn">Ends</label>
                                            <select class="div-toggle form-control untilSeletr" data-target="editUntilSel_weekly_info" id="editUntilSel_weekly">
                                                <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                                <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                                <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                            </select>
                                        </div>
                                        <div class="container col-3 pr-0 pl-0">
                                            <div class="editUntilSel_weekly_info">
                                                <div class="OCCURRENCE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="weekly_ocr_edit" min="0"/>
                                                </div>
                                                <div class="TILL_DATE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="text" id="weekly_TDateEdit" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                                </div>
                                            </div>
                                        </div>
                                        <#--<div class="col"></div>-->
                                    </div>
                                    <div class="monthlyEdit hide row pl-0 pr-0">
                                        <div class="container col-4 form-group">
                                            <label for="">Starts Date</label>
                                            <label for="onMonthDay">Monthly on<input type="text" class="form-control col-3"  id="onMonthDay_edit" <#--placeholder="Choose Start Date"--> readonly/>
                                            </label>
                                        </div>
                                        <div class="col-4">
                                            <label for="endsOn">Ends</label>
                                            <label for="endsOn">Ends</label>
                                            <select class="div-toggle form-control untilSeletr" data-target="editUntilSel_monthly_info" id="editUntilSel_monthly">
                                                <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                                <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                                <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                            </select>
                                        </div>
                                        <div class="container col-3">
                                            <div class="editUntilSel_monthly_info">
                                                <#--                                            <div class="FOR_EVER hide">FOR_EVER is...</div>-->
                                                <div class="OCCURRENCE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="monthly_ocr_edit" min="0"/>
                                                </div>
                                                <div class="TILL_DATE hide">
                                                    <label for="recDailyOcur">Until</label>
                                                    <input type="text" id="monthly_TDateEdit" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row pl-2 pr-2">
                            <div class="col-6 p-1">
                                <label for="classRadios"><#--Class Type --></label>
                                <div class="form-group">
                                    <div class="form-check form-check-inline" id="classRadios">
                                        <input class="form-check-input active_status" type="radio" name="exampleRadios" id="centerClass" value="CENTER" >
                                        <label class="form-check-label" for="centerClass">Center</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input active_status" type="radio" name="exampleRadios" id="homeClass" value="HOME">
                                        <label class="form-check-label" for="homeClass">Home</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input active_status" type="radio" name="exampleRadios" id="onlineClass" value="ONLINE">
                                        <label class="form-check-label" for="onlineClass">Online</label>
                                    </div>
                                </div>
                            </div>
                            <div class="col-6 p-1 form-group">
                                <label for="studentSelectedList"><#--Choose Student--></label><br>
                                <select class="multipleSelect form-control" multiple id="studentSelectedDropDown" name="language" style="font-size: 15px;width: 100%" type="text">
                                    <#list studentslist as stdList>
                                        <option value="${stdList.studentId!}" >${stdList.firstName!} ${stdList.lastName!} </option>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label>Event Description</label>
                            <textarea class="form-control" id="updateEventDesc"></textarea>
                        </div>
                        <div class="form-group">
                            <p class="errMsg"></p>
                        </div>
                        <input id="startDateTimeFromDb" type="hidden"/>
                        <input id="endDateTimeFromDb" type="hidden"/>
                        <div class="form-group">

                            <a href="#" class="btn btn-outline-danger btn-sm"  data-toggle="modal" data-target="#delEvnt"><i class="fa fa-trash-o" aria-hidden="true"></i></a>

                            <span class="float-right">
                                        <button type="button" class="btn btn-primary" id="submitBtn" data-toggle="modal" data-target="#updateEvnt">Update</button>
                                    &nbsp;<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                    </span>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="updateEvnt" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered" >
        <div class="modal-content"style="box-shadow: 0px 3px 15px 5px #888888;border: 0px">
            <div class="modal-header">
                <div class="page-title">
                    <i class="fa fa-list-alt" aria-hidden="true"></i> Edit recurring event
                </div>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">

                <div class="container" id="updateEvt">
                    <center>
                        <label>
                            Are you sure you want to Update this event ?
                        </label>
                    </center>
                </div>
                <div class="container" id="updateRecEvt">
                    <div class="form-check">
                        <label class="form-check-label" for="curEvtUpdate">
                            <input type="radio" class="form-check-input" id="curEvtUpdate" name="updateEvt" value="CURRENT_EVT" checked>Only this event
                        </label>
                    </div>
                    <div class="form-check">
                        <label class="form-check-label" for="allEvtUpdate">
                            <input type="radio" class="form-check-input" id="allEvtUpdate" name="updateEvt" value="ALL_EVT">All events
                        </label>
                    </div>
                </div>
                <div class="float-right"><input type="hidden" id="" class="eventId">
                    <button type="button" class="btn btn-primary btn-sm" data-dismiss="modal" id="update-event-button">OK</button>&nbsp;
                    <button type="button" class="btn btn-secondary btn-sm" data-dismiss="modal">Cancel</button></div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="delEvnt" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered" >
        <div class="modal-content"style="box-shadow: 0px 3px 15px 5px #888888;border: 0px">
            <div class="modal-header">
                <div class="page-title">
                    <i class="fa fa-exclamation-triangle" aria-hidden="true" style="color: #ffc107"></i> Delete Event
                </div>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">

                <div class="container" id="delEvt">
                    <center>
                        <label>
                            Are you sure you want to delete this event ?
                        </label>
                    </center>
                </div>
                <div class="container" id="delRecEvt">
                        <div class="form-check">
                            <label class="form-check-label" for="curEvt">
                                <input type="radio" class="form-check-input" id="curEvt" name="delEvt" value="CURRENT_EVT" checked>Only this event
                            </label>
                        </div>
                        <div class="form-check">
                            <label class="form-check-label" for="allEvt">
                                <input type="radio" class="form-check-input" id="allEvt" name="delEvt" value="ALL_EVT">All event
                            </label>
                        </div>
                </div>
                    <div class="float-right"><input type="hidden" id="del_event" class="eventId">
                        <button type="button" class="btn btn-danger btn-sm" data-dismiss="modal" id="del_event_btn">Yes</button>&nbsp;
                        <button type="button" class="btn btn-secondary btn-sm" data-dismiss="modal">No</button></div>
            </div>
        </div>
    </div>
</div>
<div id="modal-view-event-add" class="modal modal-top fade calendar-modal" tabindex='-1'>
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content cal_event_modal">
            <form id="add-event-form" action="<@ofbizUrl>createCalendarEvent</@ofbizUrl>" method="POST" onsubmit="return false;">
                <div class="modal-header">
                    <div class="page-title"><i class="fa fa-calendar" aria-hidden="true"></i> New Event</div>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="row pl-2 pr-2">
                        <div class="form-group col p-1">
<#--                            <label for="eName">Event name</label>-->
                            <input type="text" class="form-control"  required id="createEvtName" placeholder="Event Title">
                        </div>
                    </div>
                    <div class="row pl-3 pr-2 mb-2">
                        <div class="form-check">
                            <label class="form-check-label" for="evtPrivacy">
                                <input type="checkbox" class="form-check-input" id="evtPrivacy">Private Event
                            </label>
                        </div>
                    </div>

                    <div class="row pl-2 pr-2">
                        <div class="col-3 form-group p-1">
<#--                            <label for="new_event_start_date">Start Date</label>-->
                            <input type='text' id="new_event_start_date" class="datetimepicker form-control" name="sdate" required>
                        </div>
                        <div class="col-3 form-group p-1">
<#--                            <label for="new_event_start_time">Start Time</label>-->
                            <input type='text' id="new_event_start_time" class="datetimepicker form-control" required>
                        </div>
                        <div class="col-3 form-group p-1">
<#--                            <label for="new_event_end_date">End Date</label>-->
                            <input type='text' class="form-control" autocomplete="off" id="new_event_end_date" required>
                        </div>
                        <div class="col-3 form-group p-1">
<#--                            <label for="new_event_end_time">End Time</label>-->
                            <input type='text' class="form-control" autocomplete="off" id="new_event_end_time" required>
                        </div>
                    </div>
                    <div class="row pr-2 pl-2">
                        <div class="col-2 p-1 form-group">
                            <label for="repeat_select">Repeat</label>
                            <select class="div-toggle form-control" data-target=".my-info-1" id="repeat_select">
                                <option value="NEVER" data-show="">No Repeat</option>
                                <option value="DAILY" data-show=".daily">Daily</option>
                                <option value="WEEKLY" data-show=".weekly">Weekly</option>
                                <option value="MONTHLY" data-show=".monthly">Monthly</option>
                            </select>
                        </div>
                        <div class="col p-1 form-group">
                            <div class="my-info-1">
                                <div class="daily hide row pl-2 pr-2">
                                    <div class="container col">
                                        <label for="untilSeletr">Ends</label>
                                        <select class="div-toggle form-control untilSeletr" data-target=".my-info-2" id="untilSeletr_daily">
                                            <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                            <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                            <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                        </select>
                                    </div>
                                    <div class="container col">
                                        <div class="my-info-2">
                                            <#--                                            <div class="FOR_EVER hide">FOR_EVER is...</div>-->
                                            <div class="OCCURRENCE hide">
                                                <label for="daily_ocr">Until</label>
                                                <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="daily_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="daily_TDate" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col"></div>
                                </div>
                                <div class="weekly hide row pr-1 pl-0 mr-0">
                                    <div class="container col-6 ">
                                        <label for="weekSelector">Select days</label>
                                        <div class="weekDays-selector" id="weekSelector">
                                            <input type="checkbox" id="weekday-sun" class="weekday" value="onSunday" name="week"/>
                                            <label for="weekday-sun">S</label>
                                            <input type="checkbox" id="weekday-mon" class="weekday" value="onMonday" name="week"/>
                                            <label for="weekday-mon">M</label>
                                            <input type="checkbox" id="weekday-tue" class="weekday" value="onTuesday" name="week"/>
                                            <label for="weekday-tue">T</label>
                                            <input type="checkbox" id="weekday-wed" class="weekday" value="onWednesday" name="week"/>
                                            <label for="weekday-wed">W</label>
                                            <input type="checkbox" id="weekday-thu" class="weekday" value="onThursday" name="week"/>
                                            <label for="weekday-thu">T</label>
                                            <input type="checkbox" id="weekday-fri" class="weekday" value="onFriday" name="week"/>
                                            <label for="weekday-fri">F</label>
                                            <input type="checkbox" id="weekday-sat" class="weekday" value="onSaturday" name="week"/>
                                            <label for="weekday-sat">S</label>
                                        </div>
                                    </div>
                                    <div class="container col-3 pl-0 pr-0">
                                        <label for="endsOn">Ends</label>
                                        <select class="div-toggle form-control untilSeletr" data-target=".my-info-2" id="untilSeletr_weekly">
                                            <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                            <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                            <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                        </select>
                                    </div>
                                    <div class="container col-2 pr-0 pl-0">
                                        <div class="my-info-2">
                                            <#--                                            <div class="FOR_EVER hide">FOR_EVER is...</div>-->
                                            <div class="OCCURRENCE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="weekly_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="weekly_TDate" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="monthly hide row pl-0 pr-0">
                                    <div class="container col-4 form-group">
                                        <label for="">Starts Date</label>
                                        <label for="onMonthDay">Monthly on<input type="text" class="form-control col-3"  id="onMonthDay" <#--placeholder="Choose Start Date"--> readonly/>
                                        </label>
                                    </div>
                                    <div class="col-4">
                                        <label for="endsOn">Ends</label>
                                        <label for="endsOn">Ends</label>
                                        <select class="div-toggle form-control untilSeletr" data-target=".my-info-2" id="untilSeletr_monthly">
                                            <option value="FOR_EVER" data-show=".FOR_EVER">Never</option>
                                            <option value="OCCURRENCE" data-show=".OCCURRENCE">Occurrences</option>
                                            <option value="TILL_DATE" data-show=".TILL_DATE">On Date</option>
                                        </select>
                                    </div>
                                    <div class="container col-3">
                                        <div class="my-info-2">
                                            <#--                                            <div class="FOR_EVER hide">FOR_EVER is...</div>-->
                                            <div class="OCCURRENCE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="number" placeholder="Eg:10" class="OCCURRENCE form-control mb-1" id="monthly_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="monthly_TDate" class="TILL_DATE datepickerBs form-control" placeholder="Choose end Date"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col"></div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="row pl-2 pr-2">
                        <div class="col-6 p-1">
                            <label for="classRadios">Class Type </label>
                            <div class="form-group">
                                <div class="form-check-inline">
                                    <label class="form-check-label">
                                        <input type="radio" class="form-check-input active_status" name="optradio" id="centerClass" value="CENTER">Center
                                    </label>
                                </div>
                                <div class="form-check-inline">
                                    <label class="form-check-label">
                                        <input type="radio" class="form-check-input active_status" name="optradio" id="homeClass" value="HOME" checked>Home
                                    </label>
                                </div>
                                <div class="form-check-inline">
                                    <label class="form-check-label">
                                        <input type="radio" class="form-check-input active_status" name="optradio" id="onlineClass" value="ONLINE">Online
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="col-6 p-1 form-group">
                            <label for="studentSelectedList"><#--Choose Student--></label>
                                <select class="multipleSelect form-control" id="studentSelectedList" multiple name="language" style="font-size: 15px" type="text">
                                    <#list studentslist as stdList>
                                        <option value="${stdList.studentId!}" >${stdList.firstName!} ${stdList.lastName!} </option>
                                    </#list>
                                </select>
                        </div>
                    </div>

                    <div class="row pr-2 pl-2">
                        <div class="col p-1">
                            <div class="form-group">
                                <textarea class="form-control" <#--name="edesc" -->id="edesc" placeholder="Event Description"></textarea>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <p id="errMsg"></p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" id="create-event-button">Save</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                </div>
            </form>
        </div>
    </div>
</div>