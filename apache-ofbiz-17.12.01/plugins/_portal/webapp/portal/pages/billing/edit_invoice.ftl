<div class="container-fluid edit_invoice_container">
    <nav class="nav navbar p-1">
        <div class="page-title">Invoice No: ${invoiceInfo.invoiceId}<br/>
            <span>Status:
                <#if invoiceInfo.statusId == "INVOICE_IN_PROCESS">
                <span class="text-primary">Invoice in process</span>
                    <#elseif invoiceInfo.statusId == "INVOICE_CANCELLED">
                    <span class="text-danger">Invoice cancelled</span>
                    <#elseif invoiceInfo.statusId == "INVOICE_PAID">
                    <span class="text-success">Invoice paid</span>
                </#if>
            </span>
        </div>
        <input type="hidden" id="invoiceId" value="${invoiceInfo.invoiceId}">
        <div class="input-group-append float-right ">

            <a href="#" class="btn btn-outline-primary " id="print_invoice">
                <i class="fa fa-print" aria-hidden="true"></i> Print</a>&nbsp
            <a class="nav-link  btn btn-outline-secondary btn-sm" href="<@ofbizUrl>invoices</@ofbizUrl>" >
                <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>
        </div>
    </nav><br/>
<div class="row">
    <div class="col-8">
        <div class="row">
            <div class="col">
            <div class="float-left">
                Name:  ${invoiceInfo.firstName} ${invoiceInfo.lastName}<br/>
                No: ${invoiceInfo.partyId}
            </div>
            <div class="float-right">
                Date:  ${invoiceInfo.invoiceDate!?date}<br/>
                Due-Date:${invoiceInfo.dueDate!?date}
            </div>
        </div>

                </div>
        <br/>
        <div class="row">
            <div class="col">
            <table class="table table-active table-bordered table-sm table-hover">
                <thead class="">
                <tr class="bg-light">
                    <th colspan="4">Invoice items</th>
                </tr>
                <tr>
                    <th>#</th>
                    <th>Item</th>
                    <th>Quantity</th>
                    <th class="text-right">Amount</th>
                </tr>
                </thead>
                <tbody class="bg-light">
                <#list itemList as item>
                    <tr>
                        <td>${item_index+1}</td>
                        <td >${item.description}</td>
                        <td >${item.quantity}</td>
                        <td >
                            <span class="float-right">
                                <@ofbizCurrency amount = item.unitPrice isoCode = preferredCurrency.currencyType!/>
                            </span>
                        </td>
                    </tr>
                </#list>
                <tr>
                    <td colspan="3">
                        <span class="float-right">Total Amount : </span>
                    </td>
                    <td>
                        <#assign invoiceTotal = Static["org.apache.ofbiz.accounting.invoice.InvoiceWorker"].getInvoiceTotal(delegator, invoiceInfo.invoiceId)!/>
                        <span class="float-right">
                            <@ofbizCurrency amount = invoiceTotal!?number isoCode = preferredCurrency.currencyType!/>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td colspan="3">
                        <span class="float-right">Due Amount : </span>
                    </td>
                    <td>
                        <span class="float-right">
                            <@ofbizCurrency amount = invoiceInfo.outstandingAmount isoCode = preferredCurrency.currencyType!/>
                        </span>
                    </td>
                </tr>
                </tbody>
            </table>
            </div>
        </div>
        <div class="row">
            <div class="col">
                <table class="table table-active table-bordered table-hover table-sm">
                    <thead>
                    <tr class="bg-light">
                        <th colspan="6"><div class="m-1">
                                <label for="payment_method">Payment History</label>
                                <div class="float-right">
                                    <#list invoicePaymentInfoList as payment>
                                    <#if payment.paidAmount == 0 && invoice.statusId != "INVOICE_CANCELLED">
                                        <button type="button" class="btn btn-sm btn-outline-danger" data-toggle="modal" data-target="#cancelInvoice">
                                            <i class="fa fa-times-circle-o"></i> Cancel Invoice</button>
                                    </#if>
                                    </#list>&nbsp
                                    <#list invoicePaymentInfoList as payment>
                                        <#if payment.outstandingAmount!=0 && invoice.statusId != "INVOICE_CANCELLED">
                                            <button type="button" class="btn btn-success btn-sm float-right" data-toggle="modal" data-target="#addPayment_modal">
                                                <i class="fa fa-credit-card-alt" aria-hidden="true"></i> Receive payment
                                            </button>
                                        </#if>
                                    </#list>
                                </div>

                            </div>
                        </th>
                    </tr>
                    <tr>
                        <th>#</th>
                        <th>Payment No</th>
                        <th>Description</th>
                        <th>Paid Date</th>
                        <th>Payment type</th>
                        <th class="text-right">Paid Amount</th>
                    </tr>
                    </thead>
                    <tbody class="bg-white">
                    <#list paymentInfoOnly?sort_by("pmEffectiveDate")?reverse as payment>
                        <tr>
                            <td>${payment_index + 1}</td>
                            <td>${payment.paymentId!}</td>
                            <td>${payment.pmComments!}</td>
                            <td>${payment.pmEffectiveDate!?date}</td>
                            <td>${payment.pmPaymentMethodTypeId!}</td>
                            <td>
                                <@ofbizCurrency amount = payment.pmAmount isoCode = preferredCurrency.currencyType!/>
                            </td>
                        </tr>
                        <#else>
                        <tr>
                            <td colspan="6" class="text-muted"><center>No transactions found</center></td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <div class="col">

    </div>
</div>
</div>
    <#--addPayment_modal-->
    <div class="modal" tabindex="-1" id="addPayment_modal">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h4 class="modal-title bg-blue">Add Payment</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <div class="modal-body modal_payment p-2">
                <form action="save_payment">
                    <div class="modal-body bg-white">
                        <div class="form-group">

                            <div class="row">
                                <div class="col-sm-6">
                                    <label for="student_name">Student Name</label>
                                    <input type="text" class="form-control" id="student_name" value="${invoiceInfo.firstName} ${invoiceInfo.lastName}" readonly>
                                    <input type="hidden" id="studentId" value="${invoiceInfo.partyId!}">
                                </div>
                                <div class="col-sm-6">
                                    <label for="invoiceId">Invoice Id</label>
                                    <input type="text" class="form-control" id="invoiceId" value="${invoiceInfo.invoiceId}" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="amount">Amount</label>
                            <input type="number" class="form-control" id="amount" placeholder="Eg:20,000" required>
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
                </form>
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
                <h4 class="modal-title">Do you want to cancel Invoice?</h4>
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