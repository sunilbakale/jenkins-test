<body id="editStudentPage" xmlns="http://www.w3.org/1999/html">
<div class="container-fluid">
    <div class="row pt-2">
        <div class="row col-11">
            <div class="pl-4">
                <h3 id="studentName"></h3>
            </div>
            <div class="col-0 pl-4 " id="studentEditBtn">
                <button type="button" class="btn btn-sm btn-outline-primary" data-toggle="modal"
                        data-target="#editStudentModel" onclick="getStudentForEdit(studentId)">
                    Edit
                </button>
            </div>
            <div class="col-3" id="loadingIcon">
                <img src='../static/images/loading.gif' alt="Loading.." width="15" height="15"/>
            </div>
        </div>
        <div class="col-1">
            <a class="btn btn-secondary" href="<@ofbizUrl>students</@ofbizUrl>">
                <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>
        </div>
    </div>
    <div class="pl-3">
        <div class="row pl-3 pt-1">
            <div>
                <i class="fa fa-phone text-info" aria-hidden="true"></i>
            </div>
            <div class="pl-2">
                <p id="studentMobileNumber"></p>
            </div>
        </div>
        <div class="row pl-3">
            <div>
                <i class="fa fa-envelope text-info" aria-hidden="true"></i>
            </div>
            <div class="pl-2">
                <p id="studentEmail"></p>
            </div>
        </div>
    </div>
    <div class="modal fade" id="editStudentModel" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editStudentModelTitle">Edit Student</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body p-0">
                    <div class="col-md-12">
                        <input type="hidden" name="studentId" value="${studentId!}" id="studentId"/>
                        <div class="form-group">
                            <label for="StudentFirstName">First Name <i
                                        class="fa fa-asterisk cthpage_asterisk"
                                        aria-hidden="true"></i></label>
                            <input type="text" class="form-control" id="StudentFirstName"
                                   value="" name="studentfirstname">
                        </div>
                        <div class="form-group ">
                            <label for="StudentSecondName">Last Name <i
                                        class="fa fa-asterisk cthpage_asterisk"
                                        aria-hidden="true"></i></label>
                            <input type="text" class="form-control" id="StudentSecondName"
                                   value="" name="studentlastname">
                        </div>
                        <div id="loadingIcon2">
                            <center>
                                <img src='../static/images/loading.gif' alt="Loading.." width="20" height="20"/>
                            </center>
                        </div>
                        <div class="form-group">
                            <label for="Studentmail">Email</label>
                            <input type="email" class="form-control" id="Studentemail"
                                   value="" name="studentemail"
                                   readonly>
                        </div>
                        <div class="form-group ">
                            <label for="StudentPhone">Phone</label>
                            <input type="text" class="form-control" id="StudentPhone"
                                   value=""
                                   name="studentmobile">
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary" onclick="updateStudent()">Save
                            </button>
                            <button type="button" class="btn btn-secondary ml-1 " data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="container row">
    <div class="col-4 pt-3">
        <div class="card shadow-none border border-secondary">
            <div class="card-header">
                <div class="pull-left">Upcoming Events</div>
                <div class="pull-right">
                    <button data-toggle="modal" data-target="#createEventMdl" id="newEvent"
                            title="Add new event" class="btn btn-sm btn-primary">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i>&nbsp;New Event
                    </button>
                </div>
            </div>
            <#list studentEvents as event>
                <ul class="list-group list-group-flush p-0">
                    <li class="list-group-item mb-1 pl-4 ">
                        <div class="pt-1">
                            <div class="text-capitalize h6"><b>${event.title!}</b></div>
                            <div class="text-muted">
                                <i class="fa fa-map-marker text-primary" aria-hidden="true"></i>
                                ${event.location!}</div>
                            <div class="pt-1">
                                <i class="fa fa-clock-o text-warning" aria-hidden="true"></i>
                                ${event.start?number_to_date?date}, &nbsp;&nbsp;
                                ${event.start?number_to_time?string.short}
                                - ${event.end!?number_to_time?string.short}
                            </div>
                        </div>
                    </li>
                </ul>
            <#else>
                <div class="card-body ">
                    <span class="text-dark"><center>No Events found</center></span>
                </div>
            </#list>
        </div>
    </div>
    <div class="col-4 pt-3">
        <div class="card shadow-none border border-secondary">
            <div class="card-header">
                <div class="pull-left">Pending invoices</div>
                <div class="pull-right">
                    <button data-toggle="modal" data-target="#invoiceModel" id="invoiceModelButton"
                            title="Add new invoice" class="btn btn-sm btn-primary">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i>&nbsp;New Invoice
                    </button>
                </div>
            </div>
            <#list invoices as invoice>
                <ul class="list-group list-group-flush p-0">
                    <li class="list-group-item mb-1 pl-4 ">
                        <div class="pt-1">
                            <div class="h6"><i class="fa fa-file-invoice"></i>Invoice number
                                :<b>${invoice.invoiceId!}</b></div>
                            <div class="h6"><i class="fa fa-info-circle text-info"></i>&nbsp;
                                Status : ${invoice.statusId!}</div>
                            <div>
                                <i class="fa fa-clock-o text-primary" aria-hidden="true"></i>
                                &nbsp;Invoice Due-Date : ${invoice.dueDate?date}</div>
                            <div class="pt-1">
                                <#if preferredCurrency.currencyType??>
                                    <#if preferredCurrency.currencyType == "INR">
                                        <i class="fa fa-inr text-warning" aria-hidden="true"></i>
                                    <#elseif preferredCurrency.currencyType == "USD">
                                        <i class="fa fa-usd text-warning" aria-hidden="true"></i>
                                    <#elseif preferredCurrency.currencyType == "EUR">
                                        <i class="fa fa-eur text-warning" aria-hidden="true"></i>
                                    </#if>
                                <#else>
                                    <i class="fa fa-usd text-warning" aria-hidden="true"></i>                                </#if>
                                &nbsp;&nbsp;Balance : ${invoice.outstandingAmount!}
                            </div>
                        </div>
                    </li>
                </ul>
            <#else>
                <div class="card-body ">
                    <span class="text-dark"><center>No Pending invoices found</center></span>
                </div>

            </#list>
        </div>
    </div>
</div>

<br/><br/>
<div class="modal fade" id="invoiceModel" role="dialog">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">New Invoice</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="container row">
                    <div class="col-12">
                        <div class="row">
                            <div class="col-xs-12 col-md-12">
                                <div class="row">
                                    <div class="col-xs-6 col-md-6">
                                        <label for="stdForInvoice">Choose Student:</label><br>
                                        <select class="custom-select" id="stdForInvoice" required>
                                            <#list students as student>
                                                <option value="${student.studentId!}">${student.firstName!} ${student.lastName!}</option>
                                            </#list>
                                        </select>
                                    </div>
                                    <div class="col-xs-6 col-md-6 ">
                                        <label for="invoice_date"> Due-Date:</label><br>
                                        <input type='text' class="datepickerBs form-control" id="invoice_date"
                                               autocomplete="off" required>
                                    </div>
                                </div>
                                <br/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="table-responsive tabclass">
                                    <table class="table table-sm table-active">
                                        <thead>
                                        <tr class="">
                                            <th>Item</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Total</th>
                                            <th>Action</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr class="item-row">
                                            <td class="item-name">
                                                <div class="delete-btn">
                                                    <input type="text" class="form-control form-control-sm item"
                                                           placeholder="Item" type="text">

                                                </div>
                                            </td>
                                            <td>
                                                <input class="form-control form-control-sm price" placeholder="Price"
                                                       type="number">
                                            </td>
                                            <td>
                                                <input class="form-control form-control-sm qty" placeholder="Quantity"
                                                       type="number" min="1">
                                            </td>
                                            <td>
                                                <span class="total">0.00</span>
                                            </td>
                                            <td>
                                                <i class="fa fa-trash-o text-danger" id="delete"></i>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="row">
                                    <div class="col"><a id="addRow" href="javascript:;" title="Add a row"
                                                        class="btn btn-success btn-sm"><i class="fa fa-plus-square"
                                                                                          aria-hidden="true"></i>
                                            Add row</a>
                                        <strong class="float-right">Sub-total</strong>
                                    </div>
                                    <div class="col-2">
                                <span>
                                    <#if preferredCurrency.currencyType??>
                                        <#if preferredCurrency.currencyType == "INR">
                                            <i class="fa fa-inr" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "USD">
                                            <i class="fa fa-usd" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "EUR">
                                            <i class="fa fa-eur" aria-hidden="true"></i>
                                        </#if>
                                    <#else>
                                        <i class="fa fa-usd" aria-hidden="true"></i>
                                    </#if>
                                </span>
                                        <strong class="" id="subtotal">0.00</strong>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col"><strong class="float-right">Discount</strong></div>
                                    <div class="col-2"><input class="form-control form-control-sm float-right"
                                                              id="discount" value="0" type="text"></div>
                                </div>
                                <div class="row">
                                    <div class="col">
                                        <strong class="float-right"><strong>Grand Total</strong></strong></div>
                                    <div class="col-2">
                                <span>
                                    <#if preferredCurrency.currencyType??>
                                        <#if preferredCurrency.currencyType == "INR">
                                            <i class="fa fa-inr" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "USD">
                                            <i class="fa fa-usd" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "EUR">
                                            <i class="fa fa-eur" aria-hidden="true"></i>
                                    </#if>
                                    <#else>
                                        <i class="fa fa-usd" aria-hidden="true"></i>>
                                    </#if>
                                </span>
                                        <span id="grandTotal">0</span></div>
                                </div>
                                <hr/>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div class="row m-1">
                    <input class="btn btn-primary" type="button" value="Submit" id="getval">
                    <button type="button" class="btn btn-secondary ml-1 " data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal" id="createEventMdl" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content cal_event_modal">
            <form id="add-event-form">
                <div class="modal-header">
                    <div class="page-title"><i class="fa fa-calendar" aria-hidden="true"></i> New Event</div>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="row pl-2 pr-2">
                        <div class="input-group input-group-sm">
                            <div class="input-group-prepend">
                                                <span class="input-group-text border-0 text-primary">
                                                    <i class="fa fa-tag" aria-hidden="true"></i>
                                                </span>
                            </div>
                            <input type="text" class="form-control border-0" id="createEvtName"
                                   placeholder="Event Title" required>
                        </div>
                        <div class="form-group col p-1">
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
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend">
                                                    <span class="input-group-text border-0">
                                                        <i class="fa fa-calendar" aria-hidden="true"></i>
                                                    </span>
                                </div>
                                <input type='text' id="new_event_start_date"
                                       class="datepickerBs border-0 form-control"
                                       autocomplete="off" required>
                            </div>
                        </div>
                        <div class="col-3 form-group p-1">
                            <div class="input-group input-group-sm">
                                <#--<div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                        <i class="fa fa-clock-o" aria-hidden="true"></i>
                                    </span>
                                </div>-->
                                <input type='text' id="new_event_start_time"
                                       class="datetimepicker border-0 form-control" autocomplete="off" required>
                            </div>
                        </div>

                        <div class="col-3 form-group p-1">
                            <div class="input-group input-group-sm">
                                <#--<div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                        <i class="fa fa-calendar" aria-hidden="true"></i>
                                    </span>
                                </div>-->
                                <input type='text' class="form-control border-0" id="new_event_end_date"
                                       autocomplete="off" required>
                            </div>
                        </div>

                        <div class="col-3 form-group p-1">
                            <div class="input-group input-group-sm">
                                <#--<div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                        <i class="fa fa-clock-o" aria-hidden="true"></i>
                                    </span>
                                </div>-->
                                <input type='text' class="form-control border-0" id="new_event_end_time"
                                       autocomplete="off" required>
                            </div>
                        </div>
                    </div>
                    <div class="row pr-2 pl-2">
                        <div class="col-2 p-1 form-group">
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                        <i class="fa fa-repeat" aria-hidden="true"></i>
                                    </span>
                                </div>
                                <select class="div-toggle form-control form-control-sm border-0"
                                        data-target=".my-info-1" id="repeat_select">
                                    <option value="NEVER" data-show="">No Repeat</option>
                                    <option value="DAILY" data-show=".daily">Daily</option>
                                    <option value="WEEKLY" data-show=".weekly">Weekly</option>
                                    <option value="MONTHLY" data-show=".monthly">Monthly</option>
                                </select>
                            </div>


                        </div>
                        <div class="col p-1 form-group">
                            <div class="my-info-1">
                                <div class="daily hide row pl-2 pr-2">
                                    <div class="container col">
                                        <label for="untilSeletr">Ends</label>
                                        <select class="div-toggle form-control untilSeletr border-0 form-control-sm"
                                                data-target=".my-info-2" id="untilSeletr_daily">
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
                                                <input type="number" placeholder="Eg:10"
                                                       class="OCCURRENCE form-control-sm border-0 form-control mb-1"
                                                       id="daily_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="daily_TDate"
                                                       class="TILL_DATE datepickerBs form-control-sm border-0 form-control"
                                                       placeholder="Choose end Date"/>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col"></div>
                                </div>
                                <div class="weekly hide row pr-1 pl-0 mr-0">
                                    <div class="container col-6 ">
                                        <label for="weekSelector">Select days</label>
                                        <div class="weekDays-selector" id="weekSelector">
                                            <input type="checkbox" id="weekday-sun" class="weekday" value="onSunday"
                                                   name="week"/>
                                            <label for="weekday-sun">S</label>
                                            <input type="checkbox" id="weekday-mon" class="weekday" value="onMonday"
                                                   name="week"/>
                                            <label for="weekday-mon">M</label>
                                            <input type="checkbox" id="weekday-tue" class="weekday"
                                                   value="onTuesday"
                                                   name="week"/>
                                            <label for="weekday-tue">T</label>
                                            <input type="checkbox" id="weekday-wed" class="weekday"
                                                   value="onWednesday"
                                                   name="week"/>
                                            <label for="weekday-wed">W</label>
                                            <input type="checkbox" id="weekday-thu" class="weekday"
                                                   value="onThursday"
                                                   name="week"/>
                                            <label for="weekday-thu">T</label>
                                            <input type="checkbox" id="weekday-fri" class="weekday" value="onFriday"
                                                   name="week"/>
                                            <label for="weekday-fri">F</label>
                                            <input type="checkbox" id="weekday-sat" class="weekday"
                                                   value="onSaturday"
                                                   name="week"/>
                                            <label for="weekday-sat">S</label>
                                        </div>
                                    </div>
                                    <div class="container col-3 pl-0 pr-0">
                                        <label for="endsOn">Ends</label>
                                        <select class="div-toggle form-control untilSeletr border-0 form-control-sm"
                                                data-target=".my-info-2" id="untilSeletr_weekly">
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
                                                <input type="number" placeholder="Eg:10"
                                                       class="OCCURRENCE form-control-sm border-0 form-control mb-1"
                                                       id="weekly_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="weekly_TDate"
                                                       class="TILL_DATE datepickerBs form-control-sm border-0 form-control"
                                                       placeholder="Choose end Date"/>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="monthly hide row pl-0 pr-0">
                                    <div class="container col-4 form-group">
                                        <label for="">Starts Date</label>
                                        <label for="onMonthDay">Monthly on<input type="text"
                                                                                 class="border-0 form-control-sm form-control col-3"
                                                                                 id="onMonthDay" <#--placeholder="Choose Start Date"-->
                                                                                 readonly/>
                                        </label>
                                    </div>
                                    <div class="col-4">
                                        <label for="endsOn">Ends</label>
                                        <label for="endsOn">Ends</label>
                                        <select class="div-toggle form-control border-0 untilSeletr form-control-sm"
                                                data-target=".my-info-2" id="untilSeletr_monthly">
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
                                                <input type="number" placeholder="Eg:10"
                                                       class="OCCURRENCE form-control-sm border-0 form-control mb-1"
                                                       id="monthly_ocr" min="0"/>
                                            </div>
                                            <div class="TILL_DATE hide">
                                                <label for="recDailyOcur">Until</label>
                                                <input type="text" id="monthly_TDate"
                                                       class="TILL_DATE datepickerBs form-control-sm border-0 form-control"
                                                       placeholder="Choose end Date"/>
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
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                     <i class="fa fa-map-marker" aria-hidden="true"></i>
                                    </span>
                                </div>
                                <div class="form-group border-0 p-1 pt-2">
                                    <div class="form-check-inline">
                                        <label class="form-check-label">
                                            <input type="radio" class="form-check-input active_status"
                                                   name="optradio"
                                                   id="centerClass" value="CENTER" checked>Center
                                        </label>
                                    </div>
                                    <div class="form-check-inline">
                                        <label class="form-check-label">
                                            <input type="radio" class="form-check-input active_status"
                                                   name="optradio"
                                                   id="homeClass" value="HOME">Home
                                        </label>
                                    </div>
                                    <div class="form-check-inline">
                                        <label class="form-check-label">
                                            <input type="radio" class="form-check-input active_status"
                                                   name="optradio"
                                                   id="onlineClass" value="ONLINE">Online
                                        </label>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <div class="col-6 p-1 form-group">
                            <div class="input-group input-group-sm">
                                <div class="input-group-prepend">
                                    <span class="input-group-text border-0">
                                        <i class="fa fa-user" aria-hidden="true"></i>
                                    </span>
                                </div>
                                <input type="hidden" id="guestStudent" value="${studentInfo.studentId}">
                                <input type="text" class="form-control form-control-sm border-0"
                                       value="${studentInfo.firstName} ${studentInfo.lastName}" readonly disabled>
                            </div>
                        </div>
                    </div>

                    <div class="row pr-2 pl-2">
                        <div class="col p-1">
                            <div class="form-group m-0">
                                <div class="input-group input-group-sm">
                                    <div class="input-group-prepend">
                                        <span class="input-group-text border-0">
                                            <i class="fa fa-bars" aria-hidden="true"></i>
                                        </span>
                                    </div>
                                    <textarea class="form-control form-control-sm border-0" id="edesc"
                                              placeholder="Event Description"></textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <p id="errMsg"></p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary border-0" <#--id="create-event-button"-->
                            onclick="saveEvent()">Save
                    </button>
                    <button type="button" class="btn btn-secondary border-0 " data-dismiss="modal">Close</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>