<div class="row">
    <div class="col-6">
        <div class="card border rounded-0 shadow-none">
            <div class="card-body">
                <h5 class="card-title">Unpaid
                </h5>
                <div><span class="float-left" title="Invoices unpaid amount">
<#--                        <#if preferredCurrency.currencyType??>-->
                        <@ofbizCurrency amount = outstandingAmountTotal isoCode = preferredCurrency.currencyType!/>
<#--                        </#if>-->
                    </span>
                    <span class="float-right" title="Invoices total amount"><@ofbizCurrency amount = invoicesTotalAmount isoCode = preferredCurrency.currencyType!/></span></div>
                <br/>
                <div class="progress">
                    <div class="progress-bar bg-warning" role="progressbar" style="width:${unpaidTotalPercentage!}%" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <p class="card-text"><small class="text-muted">Unpaid total amount from all invoices</small></p>
            </div>
        </div></div>
    <div class="col-6">
        <div class="card border rounded-0 shadow-none">
            <div class="card-body">
                <h5 class="card-title">Paid</h5>
                <div><span class="float-left" title="Invoice paid total"><@ofbizCurrency amount = totalPayment isoCode = preferredCurrency.currencyType!/></span>
                    <span class="float-right" title="Invoices total amount"><@ofbizCurrency amount = invoicesTotalAmount isoCode = preferredCurrency.currencyType!/></span></div>
                <br/>
                <div class="progress">
                    <div class="progress-bar bg-success" role="progressbar" style="width: ${paidTotalPercentage!}%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>
                </div>
                <p class="card-text"><small class="text-muted">Paid total amount from all invoices</small></p>
            </div>
        </div></div>
</div>
<br/>
<div class="cth-tab-content">
        <div class="container-fluid">
            <div class="btn-group mb-1 mt-1">
                <div class="dropdown ml-1">
                    <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" id="dropdownStudentButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Student
                    </button>
                    <div class="dropdown-menu" aria-labelledby="dropdownStudentButton" style="height: auto;max-height: 150px;overflow-x: hidden;">
                        <#list students as student>
                            <div class="form-check ml-1">
                                <label class="form-check-label">
                                    <input type="checkbox"
                                           class="form-check-input"
                                           value="${student.studentId}"
                                           id="checkedStudents"
                                            <#-- onclick="applyInvoicesFilter()"-->>
                                    ${student.firstName} ${student.lastName}
                                </label>
                            </div>
                        </#list>
                    </div>
                </div>
                <div class="dropdown ml-1">
                    <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" id="dropdownPaidOrUnpaidButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Paid/Unpaid
                    </button>
                    <div class="dropdown-menu" aria-labelledby="dropdownPaidOrUnpaidButton">
                        <div class="form-check ml-1">
                            <label class="form-check-label">
                                <input type="checkbox" class="form-check-input" value="INVOICE_PAID" id="paidOrUnpaid" <#--onclick="applyInvoicesFilter()"-->>Paid
                            </label>
                        </div>
                        <div class="form-check ml-1">
                            <label class="form-check-label">
                                <input type="checkbox" class="form-check-input" value="INVOICE_IN_PROCESS" id="paidOrUnpaid" <#--onclick="applyInvoicesFilter()"-->>UnPaid
                            </label>
                        </div>
                        <#--<div class="form-check ml-1">
                            <label class="form-check-label">
                                <input type="checkbox" class="form-check-input" value="INVOICE_CANCELLED" id="paidOrUnpaid" &lt;#&ndash;onclick="applyInvoicesFilter()"&ndash;&gt;>Cancelled
                            </label>
                        </div>-->
                    </div>
                </div>
                <div class="dropdown ml-1">
                    <button class="btn btn-outline-secondary btn-sm dropdown-toggle" type="button" id="dropdownDateButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Due-Date
                    </button>
                    <div class="dropdown-menu" aria-labelledby="dropdownDateButton">
                        <div class="m-1"><input type="text" class="form-control datepickerBs" title="Start Date" placeholder="Start date" id="startDate" autocomplete="off"></div>
                        <div class="m-1"><input type="text" class="form-control datepickerBs" title="End Date" placeholder="End date" id="endDate" autocomplete="off"></div>
                    </div>
                </div>
                <div class="">
                    <button class="btn btn-success btn-sm ml-1" onclick="applyInvoicesFilter()">Apply</button>
                </div>
            </div>
            <span class="float-right mt-1">
                    <a href="<@ofbizUrl>new_invoice</@ofbizUrl>" class="btn btn-primary btn-sm float-right">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i> New Invoice</a>
                    </span>
        </div>
    <#if invoices?? && invoices?size &gt; 0>
            <table class="table table-hover table-sm ml-3">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Invoice No</th>
                    <th>Invoice For</th>
                    <th>Invoice Date</th>
                    <th>Invoice Due-Date</th>
                    <th>Balance</th>
                    <th>Amount</th>
                    <th>Status</th>

                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="" class="bg-white border-bottom-0">
                <#list invoices as invoice>
                    <tr>
                        <td>${invoice_index + 1}</td>
                        <td>${invoice.invoiceId}</td>
                        <td>
                            <a href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${invoice.invoiceId}">
                                ${Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.toParty, false)}
                            </a>
                            <input type="hidden" value="${invoice.invoiceId}" data-invoice-id ="${invoice.invoiceId}">
                            <input type="hidden" value="${Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.toParty, false)}"
                                   data-invoice-id ="${Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.toParty, false)}">
                        </td>
                        <td>${invoice.createdDate!?date}</td>
                        <td>
                            <#if invoice.dueDate??>
                                ${invoice.dueDate!?date}
                            </#if>
                        </td>
                        <td>
                            <@ofbizCurrency amount = invoice.outstandingAmount isoCode = preferredCurrency.currencyType!/>
                        </td>
                        <td>
                            <#assign invoiceTotal = Static["org.apache.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator, invoice.invoiceId)!/>
                            <@ofbizCurrency amount = invoiceTotal!?number isoCode = preferredCurrency.currencyType!/>
                        </td>
                        <td>
                        <#if invoice.statusId == "INVOICE_CANCELLED"><span class="text-danger">Invoice cancelled</span>
                        <#elseif invoice.statusId == "INVOICE_IN_PROCESS"><span class="text-primary">Invoice in process</span>
                        <#elseif invoice.statusId == "INVOICE_PAID"><span class="text-success">Invoice paid</span>
                            <#else><span>${invoice.statusId!}</span>
                        </#if>
                        </td>
                        <td>
                            <#assign invoiceId = invoice.invoiceId/>
                            <#assign studentId = invoice.toParty/>
                            <#assign studentName = Static["org.apache.ofbiz.party.party.PartyHelper"].getPartyName(delegator, invoice.toParty, false)/>
                            <button class="btn btn-outline-success dropdown-toggle btn-sm" data-toggle="dropdown" id="actionDropdown" data-student-id="${studentName}"
                                    onclick="initInvoiceData(${invoiceId},${studentId})">
                                <i class="fa fa-pencil" aria-hidden="true"></i>
                            </button>
                            <div class="dropdown-menu">
                                <div class="dropdown-item">
                                    <i class="fa fa-print text-secondary" aria-hidden="true"></i>
                                    Print invoice
                                </div>
                                <div class="">
                                    <a class="dropdown-item text-dark" href="<@ofbizUrl>edit_invoice</@ofbizUrl>?invoice_id=${invoice.invoiceId}">
                                        <i class="fa fa-file-text-o text-info" aria-hidden="true"></i>
                                        View invoice
                                    </a>
                                </div>
                                <#if invoice.statusId != "INVOICE_CANCELLED" && invoice.statusId != "INVOICE_PAID">
                                <div class="">
                                    <a class="dropdown-item"
                                       data-toggle="modal"
                                       data-target="#addPayment_modal">
                                    <i class="fa fa-calculator text-primary" aria-hidden="true"></i>
                                    Receive payment</a>
                                </div>
                                </#if>
                                <#if invoice.statusId != "INVOICE_CANCELLED" && invoice.outstandingAmount == invoiceTotal>
                                    <div class="dropdown-item">
                                        <a class="" data-toggle="modal" data-target="#cancelInvoice">
                                            <i class="fa fa-times-circle-o text-danger" aria-hidden="true"></i>
                                            Cancel invoice</a>
                                    </div>
                                </#if>
                                <div class="dropdown-item">
                                    <i class="fa fa-share text-secondary" aria-hidden="true"></i>
                                    Share
                                </div>
                            </div>
                        </td>
                    </tr>
                </#list>

                </tbody>

                <caption class="sticky">
                    <span class="float-left"> Total invoices: ${invoices?size}</span>
                </caption>
            </table>
        <#else>
        <div style="text-align: center" class="p-4">
                <img src="../static/lottiefiles/629-empty-box.gif" width="300" class="center" />
                <div class="text-muted">No invoices found</div>
                <div>
                    <a href="<@ofbizUrl>new_invoice</@ofbizUrl>" class="btn btn-link">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i> Add Now</a>
                </div>
            </div>
        </#if>
</div>

<div class="modal" tabindex="-1" id="addPayment_modal">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title page-title bg-blue">Add Payment : <span class="page-title" id="invoiceId_show"></span> </h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body modal_payment p-0">
                    <div class="modal-body bg-white">
                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" class="form-control" id="amount" placeholder="Eg:20,000" required>
                            <input type="hidden" id="invoiceId">
                            <input type="hidden" id="studentId">
                        </div>
                        <div class="form-group">
                            <label for="Payment_Method">Payment Method</label>
                            <select class="custom-select" id="Payment_Method" required>
                                <option value="CASH">Cash</option>
                                <option value="ONLINE_BANKING">Online Banking</option>
                                <option value="EFT_ACCOUNT">EFT</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="payment_desc">Payment Description</label>
                            <textarea class="form-control mb-3" rows="3" id="payment_desc"  ></textarea>
                        </div>
                    </div>
            </div>
            <div class="modal-footer">
                <a href="#" id="save_payment" class="btn btn-success">Save Payment</a>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade"  id="cancelInvoice" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Do you want to cancel <span class="page-title" id="delInvoiceId_show"></span> Invoice?</h4>
                <input type="hidden" id="invoiceId">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body ">
                <center>
                    <button type="button" class="btn btn-danger" id="cancelInvoiceBtn">Cancel Invoice</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                </center>
            </div>
        </div>
    </div>
</div>