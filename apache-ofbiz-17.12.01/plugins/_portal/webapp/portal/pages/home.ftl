<div class="container-fluid">
    <nav class="nav navbar cth_page_heading bg-light">
        <div class="page-title ">
            Dashboard
        </div>
    </nav>
    <br/>
    <div class="row">
        <div class="col-md-3">
            <div class="card-counter primary">
                <i class="fa fa-users"></i>
                <span class="count-numbers">${students!?size}</span>
                <span class="count-name">
                          <span class="float-right">Students</span> <br/>
                        <button class="btn btn-outline-primary btn-sm float-right" onclick="viewStudents()">
                            View More
                        </button>
                        </span>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-counter danger">
                <i class="fa fa-file-text-o"></i>
                <span class="count-numbers">${invoices!?size}</span>
                <span class="count-name"><span class="float-right">Invoices</span> <br/>
                          <button class="btn btn-outline-danger btn-sm float-right" onclick="viewInvoices()">
                            View More
                          </button>
                        </span>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-counter success">
                <i class="fa fa-calendar fa-5x" aria-hidden="true"></i>
                <span class="count-numbers">${upcomingEventsSize!}</span>
                <span class="count-name">
                          <span class="float-right">Upcoming Events</span> <br/>
                          <button class="btn btn-outline-success btn-sm float-right" onclick="viewEvents()">
                            View More
                          </button>
                        </span>
            </div>
        </div>
        <div class="col-md-3">
            <div class="card-counter info">
                <i class="fa fa-calculator fa-5x" aria-hidden="true"></i>
                <span class="count-numbers">${expenses!?size}</span>
                <span class="count-name">
                          <span class="float-right">Expenses</span> <br/>
                          <button class="btn btn-outline-warning btn-sm float-right" onclick="viewExpenses()">
                            View More
                          </button>
                        </span>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-9">

            <div class="row">
                <div class="col-md-4">
                    <div class="card-counter">
                        <div class="card-header">
                            <div class="row">
                                <div class="col-2"><span class="fa fa-info-circle fa-2x text-muted"
                                                         aria-hidden="true"></span>
                                </div>
                                <div class="col">
                                    <span class="float-right"> Account Info</span><br/>
                                    <span class="float-right">
                                    <button class="btn btn-warning btn-sm float-right text-white"
                                            onclick="viewExpenses()">
                                      View More
                                    </button>
                                  </span></div>
                            </div>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-credit-card" aria-hidden="true"></span>
                              Earned
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = totalPayment isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-calculator" aria-hidden="true"></span>
                                Expense
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = totalExpense isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-exclamation-triangle" aria-hidden="true"></span>
                                Pending earning
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = outstandingAmountTotal isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="card-counter" id="upcEvtDashboardCard">
                        <div class="card-header">
                            <div class="row">
                                <div class="col-2">
                                  <span class="float-left"><span class="text-muted fa fa-tasks fa-2x"
                                                                 aria-hidden="true"></span>
                                    </span>
                                </div>
                                <div class="col  p-0">
                                    <span class="float-left"><h6>Upcoming Events</h6></span>
                                    <span class="float-right"><button class="btn btn-primary btn-sm"
                                                                      onclick="viewEvents()">View More</button> </span>
                                </div>
                            </div>
                        </div>
                        <#if events!?size != 0>
                            <table class="table table-hover">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Title</th>
                                    <th>Start-Date</th>
                                    <th>Start-Time</th>
                                    <th>End-Date</th>
                                    <th>End-Time</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list events as event>
                                    <tr>
                                        <td>${event_index + 1}</td>
                                        <td>${event.title!}</td>
                                        <td>${event.start!?number_to_date}</td>
                                        <td>${event.start!?number_to_time?string.short}</td>
                                        <td>${event.end!?number_to_date}</td>
                                        <td>${event.end!?number_to_time?string.short}</td>
                                    </tr>
                                </#list>
                                </tbody>
                            </table>
                        <#else>
                            <span class="text-muted">No upcoming events</span>
                        </#if>
                    </div>
                </div>
            </div>
            <div class="row">

                <div class="col-md-4">
                    <div class="card-counter">
                        <div class="card-header">
                            <div class="row">
                                <div class="col-2"><span class="fa fa-bar-chart fa-2x text-muted"
                                                         aria-hidden="true"></span>
                                </div>
                                <div class="col"><span class="float-right"> Finance Info(This Month)</span><br/>
                                    <button class="btn btn-warning btn-sm float-right text-white"
                                            onclick="viewExpenses()">
                                        View More
                                    </button>
                                </div>
                            </div>
                        </div>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-user-circle-o" aria-hidden="true"></span>
                              Earned
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = totalPaymentByDate isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-calculator" aria-hidden="true"></span>
                              Expenses
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = totalExpenseByDate!?number isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                            <li class="list-group-item">
                        <span class="float-left">
                            <span class="fa fa-exclamation-triangle" aria-hidden="true"></span>
                              Pending earning
                            </span>
                                <span class="float-right">
                            <@ofbizCurrency amount = outstandingAmountTotalByDate!?number isoCode = preferredCurrency.currencyType!/>
                        </span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-3">
            <div class="card-counter">
                <div class="card-header">
                    <div class="row">
                        <div class="col-2"><span class="fa fa-history fa-2x text-muted" aria-hidden="true"></span>
                        </div>
                        <div class="col">
                            <span class="float-right"> Recent Activity</span><br/>
                        </div>
                    </div>
                </div>
                <ul class="list-group list-group-flush">
                    <#list recentActivities as recentActivity>
                        <#if recentActivity.activityType == "STUDENT">
                            <li class="pl-sm-1 pr-sm-1 list-group-item">
                                <#list recentActivityDateList as studentActivityTime>
                                    <#if recentActivity.action == "CREATED" && recentActivity.activityId == studentActivityTime.activityId>
                                        <#assign studentExist = false>
                                        <#list students as student>
                                            <#if student.studentId == recentActivity.activityTypeId>
                                                <#assign studentExist = true>
                                                <#break>
                                            <#else>
                                                <#assign studentExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink" <#if studentExist> href="<@ofbizUrl>edit_student</@ofbizUrl>?student_id=${recentActivity.activityTypeId}"<#else>data-toggle="popover" data-content="Student was deleted" </#if>>
                                            <div class="row">
                                                <div class="col-sm-2">
                                            <span class="float-left fa fa-user circleForStudentIcon activityTypeIconSize"
                                                  aria-hidden="true"></span></div>
                                                <div class="col-sm-10"> Student <span
                                                            class="activityActionTextColor">${recentActivity.activityTypeInfo!} Added </span>
                                                    <br/>${studentActivityTime.time!}<br/>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as studentActivityTime>
                                    <#if recentActivity.action == "VIEWED" && recentActivity.activityId == studentActivityTime.activityId>
                                        <#assign studentExist = false>
                                        <#list students as student>
                                            <#if student.studentId == recentActivity.activityTypeId>
                                                <#assign studentExist = true>
                                                <#break>
                                            <#else>
                                                <#assign studentExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink" <#if studentExist> href="<@ofbizUrl>edit_student</@ofbizUrl>?student_id=${recentActivity.activityTypeId}"<#else>data-toggle="popover" data-content="Student was deleted" </#if>>
                                            <div class="row">
                                                <div class="col-sm-2">
                                                    <span class="fa fa-user circleForStudentIcon activityTypeIconSize"
                                                          aria-hidden="true"></span></div>
                                                <div class="col-sm-10"> Student <span
                                                            class="activityActionTextColor">${recentActivity.activityTypeInfo!} Viewed <br/>${studentActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as studentActivityTime>
                                    <#if recentActivity.action == "UPDATED" && recentActivity.activityId == studentActivityTime.activityId>
                                        <#assign studentExist = false>
                                        <#list students as student>
                                            <#if student.studentId == recentActivity.activityTypeId>
                                                <#assign studentExist = true>
                                                <#break>
                                            <#else>
                                                <#assign studentExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink" <#if studentExist> href="<@ofbizUrl>edit_student</@ofbizUrl>?student_id=${recentActivity.activityTypeId}"<#else>data-toggle="popover" data-content="Student was deleted" </#if>>
                                            <div class="row">
                                                <div class="col-2">
                                                        <span class="float-left fa fa-user circleForStudentIcon activityTypeIconSize"
                                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> Student <span
                                                            class="activityActionTextColor">${recentActivity.activityTypeInfo!} Updated <br/>${studentActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as studentActivityTime>
                                    <#if recentActivity.action == "DELETED" && recentActivity.activityId == studentActivityTime.activityId>
                                        <a class="activityLink" data-toggle="popover"
                                           data-content="Student was deleted">
                                            <div class="row">
                                                <div class="col-2">
                                                <span class="float-left fa fa-user circleForStudentIcon activityTypeIconSize"
                                                      aria-hidden="true"></span></div>
                                                <div class="col-10"> Student <span
                                                            class="activityActionTextColor">${recentActivity.activityTypeInfo!} Deleted <br/>${studentActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                            </li>
                        <#elseif recentActivity.activityType == "EVENT">
                            <li class="pl-sm-1 list-group-item">
                                <#list recentActivityDateList as eventActivityTime>
                                    <#if recentActivity.action == "CREATED" && recentActivity.activityId == eventActivityTime.activityId>
                                        <#assign eventExist = false>
                                        <#list events as event>
                                            <#if event.eventId == recentActivity.activityTypeId>
                                                <#assign eventExist = true>
                                                <#break>
                                            <#else>
                                                <#assign eventExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if eventExist>href="<@ofbizUrl>calender</@ofbizUrl>" <#else> data-toggle="popover" data-content="Event was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                                        <span class="float-left fa fa-calendar-o circleForEventIcon activityTypeIconSize"
                                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> Event ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Added<br/>${eventActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as eventActivityTime>
                                    <#if recentActivity.action == "VIEWED" && recentActivity.activityId == eventActivityTime.activityId>
                                        <#assign eventExist = false>
                                        <#list events as event>
                                            <#if event.eventId == recentActivity.activityTypeId>
                                                <#assign eventExist = true>
                                                <#break>
                                            <#else>
                                                <#assign eventExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink" <#if eventExist>href="<@ofbizUrl>calender</@ofbizUrl>"
                                           <#else>data-toggle="popover" data-content="Event was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                                            <span class="float-left fa fa-calendar-o circleForEventIcon activityTypeIconSize"
                                                                  aria-hidden="true"></span></div>
                                                <div class="col-10"> Event ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Viewed<br/>${eventActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as eventActivityTime>
                                    <#if recentActivity.action == "UPDATED" && recentActivity.activityId == eventActivityTime.activityId>
                                        <#assign eventExist = false>
                                        <#list events as event>
                                            <#if event.eventId == recentActivity.activityTypeId>
                                                <#assign eventExist = true>
                                                <#break>
                                            <#else>
                                                <#assign eventExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink" <#if eventExist>href="<@ofbizUrl>calender</@ofbizUrl>"
                                           <#else>data-toggle="popover" data-content="Event was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                                <span class="float-left fa fa-calendar-o circleForEventIcon activityTypeIconSize"
                                                      aria-hidden="true"></span></div>
                                                <div class="col-10"> Event ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Updated<br/>${eventActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as eventActivityTime>
                                    <#if recentActivity.action == "DELETED" && recentActivity.activityId == eventActivityTime.activityId>
                                        <a class="activityLink" data-toggle="popover" data-content="Event was deleted">
                                            <div class="row">
                                                <div class="col-2">
                                                    <span class="float-left fa fa-calendar-o circleForEventIcon activityTypeIconSize"
                                                          aria-hidden="true"></span></div>
                                                <div class="col-10"> Event ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Deleted<br/>${eventActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                            </li>
                        <#elseif recentActivity.activityType == "INVOICE">
                            <li class="pl-sm-1 list-group-item">
                                <#list recentActivityDateList as invoiceActivityTime>
                                    <#if recentActivity.action == "CREATED" && recentActivity.activityId == invoiceActivityTime.activityId>
                                        <a class="activityLink"
                                           href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${recentActivity.activityTypeId!}">
                                            <div class="row">
                                                <div class="col-2">
                                                <span class="float-left fa fa-file-text-o circleForInvoiceIcon activityTypeIconSize"
                                                      aria-hidden="true"></span></div>
                                                <div class="col-10"> Invoice ${recentActivity.activityTypeId!} <span
                                                            class="activityActionTextColor">Added<br/>${invoiceActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as invoiceActivityTime>
                                    <#if recentActivity.action == "VIEWED" && recentActivity.activityId == invoiceActivityTime.activityId>
                                        <a class="activityLink"
                                           href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${recentActivity.activityTypeId!}">
                                            <div class="row">
                                                <div class="col-2">
                                            <span class="float-left fa fa-file-text-o circleForInvoiceIcon activityTypeIconSize"
                                                  aria-hidden="true"></span></div>
                                                <div class="col-10"> Invoice ${recentActivity.activityTypeId!} <span
                                                            class="activityActionTextColor"> Viewed<br/>${invoiceActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as invoiceActivityTime>
                                    <#if recentActivity.action == "UPDATED" && recentActivity.activityId == invoiceActivityTime.activityId>
                                        <a class="activityLink"
                                           href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${recentActivity.activityTypeId!}">
                                            <div class="row">
                                                <div class="col-2">
                                    <span class="float-left fa fa-file-text-o circleForInvoiceIcon activityTypeIconSize"
                                          aria-hidden="true"></span></div>
                                                <div class="col-10"> Invoice ${recentActivity.activityTypeId!} <span
                                                            class="activityActionTextColor">Payment Updated<br/>${invoiceActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as invoiceActivityTime>
                                    <#if recentActivity.action == "CANCELLED" && recentActivity.activityId == invoiceActivityTime.activityId>
                                        <a class="activityLink"
                                           href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${recentActivity.activityTypeId!}">
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-file-text-o circleForInvoiceIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> Invoice ${recentActivity.activityTypeId!} <span
                                                            class="activityActionTextColor">Cancelled<br/>${invoiceActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                            </li>
                        <#elseif recentActivity.activityType == "NOTE">
                            <li class="pl-sm-1 list-group-item">
                                <#list recentActivityDateList as noteActivityTime>
                                    <#if recentActivity.action == "CREATED" && recentActivity.activityId == noteActivityTime.activityId>
                                        <#assign noteExist = false>
                                        <#list notes as note>
                                            <#if note.noteId == recentActivity.activityTypeId>
                                                <#assign noteExist = true>
                                                <#break>
                                            <#else>
                                                <#assign noteExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if noteExist>href="<@ofbizUrl>notes</@ofbizUrl>"<#else> data-toggle="popover" data-content="Note was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                            <span class="float-left fa fa-sticky-note circleForNoteIcon activityTypeIconSize"
                                                  aria-hidden="true"></span></div>
                                                <div class="col-10"> Note ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Added<br/>${noteActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as noteActivityTime>
                                    <#if recentActivity.action == "VIEWED" && recentActivity.activityId == noteActivityTime.activityId>
                                        <#assign noteExist = false>
                                        <#list notes as note>
                                            <#if note.noteId == recentActivity.activityTypeId>
                                                <#assign noteExist = true>
                                                <#break>
                                            <#else>
                                                <#assign noteExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if noteExist>href="<@ofbizUrl>notes</@ofbizUrl>"<#else> data-toggle="popover" data-content="Note was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-sticky-note circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> Note ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Viewed<br/>${noteActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as noteActivityTime>
                                    <#if recentActivity.action == "UPDATED" && recentActivity.activityId == noteActivityTime.activityId>
                                        <#assign noteExist = false>
                                        <#list notes as note>
                                            <#if note.noteId == recentActivity.activityTypeId>
                                                <#assign noteExist = true>
                                                <#break>
                                            <#else>
                                                <#assign noteExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if noteExist>href="<@ofbizUrl>notes</@ofbizUrl>"<#else> data-toggle="popover" data-content="Note was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-sticky-note circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> Note ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Updated<br/>${noteActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as noteActivityTime>
                                <#if recentActivity.action == "DELETED" && recentActivity.activityId == noteActivityTime.activityId>
                                <a class="activityLink" data-toggle="popover" data-content="Note was deleted">
                                    <div class="row">
                                        <div class="col-2">
                                              <span class="float-left fa fa-sticky-note circleForNoteIcon activityTypeIconSize"
                                                    aria-hidden="true"></span></div>
                                        <div class="col-10"> Note ${recentActivity.activityTypeInfo!} <span
                                                    class="activityActionTextColor">Deleted<br/>${noteActivityTime.time!}</span>
                                        </div>
                                    </div>
                                    </#if>
                                    </#list>
                            </li>
                            <#else>
                            <li class="pl-sm-1 list-group-item">
                                <#list recentActivityDateList as fileActivityTime>
                                    <#if recentActivity.action == "UPLOAD" && recentActivity.activityId == fileActivityTime.activityId>
                                        <#assign fileExist = false>
                                        <#list contentList as file>
                                            <#if file.contentId == recentActivity.activityTypeId>
                                                <#assign fileExist = true>
                                                <#break>
                                            <#else>
                                                <#assign fileExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if fileExist>href="<@ofbizUrl>files</@ofbizUrl>"<#else> data-toggle="popover" data-content="File was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                            <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                                  aria-hidden="true"></span></div>
                                                <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Uploaded<br/>${fileActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as fileActivityTime>
                                    <#if recentActivity.action == "DOWNLOAD" && recentActivity.activityId == fileActivityTime.activityId>
                                        <#assign fileExist = false>
                                            <#list contentList as file>
                                                <#if file.contentId == recentActivity.activityTypeId>
                                                    <#assign fileExist = true>
                                                    <#break>
                                                <#else>
                                                    <#assign fileExist = false>
                                                </#if>
                                            </#list>
                                        <a class="activityLink"
                                           <#if fileExist>href="<@ofbizUrl>files</@ofbizUrl>"<#else> data-toggle="popover" data-content="File was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Downloaded<br/>${fileActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as fileActivityTime>
                                    <#if recentActivity.action == "UPDATED" && recentActivity.activityId == fileActivityTime.activityId>
                                        <#assign fileExist = false>
                                        <#list contentList as file>
                                            <#if file.contentId == recentActivity.activityTypeId>
                                                <#assign fileExist = true>
                                                <#break>
                                            <#else>
                                                <#assign fileExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if fileExist>href="<@ofbizUrl>files</@ofbizUrl>"<#else> data-toggle="popover" data-content="File was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Updated<br/>${fileActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as fileActivityTime>
                                    <#if recentActivity.action == "SHARED" && recentActivity.activityId == fileActivityTime.activityId>
                                        <#assign fileExist = false>
                                        <#list contentList as file>
                                            <#if file.contentId == recentActivity.activityTypeId>
                                                <#assign fileExist = true>
                                                <#break>
                                            <#else>
                                                <#assign fileExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if fileExist>href="<@ofbizUrl>files</@ofbizUrl>"<#else> data-toggle="popover" data-content="File was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Shared<br/>${fileActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as fileActivityTime>
                                    <#if recentActivity.action == "UNSHARED" && recentActivity.activityId == fileActivityTime.activityId>
                                        <#assign fileExist = false>
                                        <#list contentList as file>
                                            <#if file.contentId == recentActivity.activityTypeId>
                                                <#assign fileExist = true>
                                                <#break>
                                            <#else>
                                                <#assign fileExist = false>
                                            </#if>
                                        </#list>
                                        <a class="activityLink"
                                           <#if fileExist>href="<@ofbizUrl>files</@ofbizUrl>"<#else> data-toggle="popover" data-content="File was deleted"</#if>>
                                            <div class="row">
                                                <div class="col-2">
                                        <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                              aria-hidden="true"></span></div>
                                                <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                            class="activityActionTextColor">Unshared<br/>${fileActivityTime.time!}</span>
                                                </div>
                                            </div>
                                        </a>
                                    </#if>
                                </#list>
                                <#list recentActivityDateList as fileActivityTime>
                                <#if recentActivity.action == "DELETED" && recentActivity.activityId == fileActivityTime.activityId>
                                <a class="activityLink" data-toggle="popover" data-content="File was deleted">
                                    <div class="row">
                                        <div class="col-2">
                                              <span class="float-left fa fa-file-o circleForNoteIcon activityTypeIconSize"
                                                    aria-hidden="true"></span></div>
                                        <div class="col-10"> File ${recentActivity.activityTypeInfo!} <span
                                                    class="activityActionTextColor">Deleted<br/>${fileActivityTime.time!}</span>
                                        </div>
                                    </div>
                                    </#if>
                                    </#list>
                            </li>
                        </#if>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</div>