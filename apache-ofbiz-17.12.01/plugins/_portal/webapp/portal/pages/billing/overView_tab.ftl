<div class="container-fluid">
    <div class="row">
        <div class="col-8">
            <div class="row">
                <div class="col">
                    <div class="card border rounded-0 shadow-none">
                        <div class="card-body">
                            <div class="row">
                                <div class="col">
                                    <div class="card bg-light border p-1 text-center">
                                        <div class="card-title m-0">Invoices</div>
                                            <h1 class="m-0">${invoices!?size}</h1>
                                            <button class="btn btn-outline-secondary btn-sm" id="viewInvoices">View</button>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="card bg-light border p-1 text-center">
                                        <div class="card-title m-0">Expenses</div>
                                            <h1 class="m-0">${expenses!?size}</h1>
                                            <button class="btn btn-outline-secondary btn-sm" id="viewExpenses">View</button>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="card bg-light border p-1 text-center">
                                        <div class="card-title m-0">Transactions</div>
                                            <h1 class="m-0">${txInfo!?size}</h1>
                                            <button class="btn btn-outline-secondary btn-sm" id="viewTx">View</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row mt-2">
                <div class="col">
                    <div class="card border rounded-0 shadow-none">
                        <div class="card-body">
                            <h5 class="card-title">Unpaid</h5>
                            <div><span class="float-left" title="Invoices unpaid amount"><@ofbizCurrency amount = outstandingAmountTotal isoCode = preferredCurrency.currencyType!/></span>
                                <span class="float-right" title="Invoices total amount"><@ofbizCurrency amount = invoicesTotalAmount isoCode = preferredCurrency.currencyType!/></span></div>
                            <br/>
                            <div class="progress">
                                <div class="progress-bar bg-warning" role="progressbar" style="width:${unpaidTotalPercentage!}%" aria-valuenow="50" aria-valuemin="0" aria-valuemax="100"></div>
                            </div>
                            <p class="card-text"><small class="text-muted">Unpaid total amount from all invoices</small></p>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card border rounded-0 shadow-none">
                        <div class="card-body">
                            <h5 class="card-title">Paid</h5>
                            <div><span class="float-left" title="Invoice paid total"><@ofbizCurrency amount = totalPayment isoCode = preferredCurrency.currencyType!/></span>
                                <span class="float-right" title="Invoices total amount"><@ofbizCurrency amount = invoicesTotalAmount isoCode = preferredCurrency.currencyType!/></span></div>
                            <br/>
                            <div class="progress">
                                <div class="progress-bar bg-success" role="progressbar" style="width: ${paidTotalPercentage!}%" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100"></div>
                            </div>
                            <p class="card-text"><small class="text-muted">Piad total amount from all invoices</small></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-4">
            <div class="row border">
                <div class="col">
                    <div class="card rounded-0">
                        <div class="card-body">
                            <div class="card-title"><center>
                            <a class="btn btn-outline-secondary rounded-circle btn-lg" href="<@ofbizUrl>new_invoice</@ofbizUrl>">
                                <i class="fa fa-file-text-o"></i>
                            </a> </center>
                            </div>
                            <p class="card-text m-0"><center><a href="<@ofbizUrl>new_invoice</@ofbizUrl>">New Invoice</a> </center></p>
                        </div>
                    </div>
                </div>
                <div class="col">
                    <div class="card rounded-0">
                        <div class="card-body">
                            <div class="card-title"><center>
                                    <a class="btn btn-outline-secondary rounded-circle btn-lg" href="<@ofbizUrl>new_expense</@ofbizUrl>">
                                        <i class="fa fa-calculator"></i>
                                    </a> </center></div>
                            <p class="card-text m-0"><center><a href="<@ofbizUrl>new_expense</@ofbizUrl>">New Expense</a></center></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>