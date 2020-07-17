
<div class="container-fluid p-2 form-group" >
    <nav class="nav navbar cth_page_heading p-1"s>
        <div class="page-title">Expense :${expense.expenseId}</div>
        <a class="nav-link  btn btn-outline-secondary btn-sm" href="<@ofbizUrl>invoices</@ofbizUrl>" >
            <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>
    </nav><br>
    <div class="container float-left bg-light">
        <form action="<@ofbizUrl></@ofbizUrl>" id="editExpenseForm" method="post" class="col-8" onsubmit="return false">
            <div class="row">
                <div class="col form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                    <span class="input-group-text">
                        <i class="fa fa-tag" aria-hidden="true"></i>
                    </span>
                        </div>
                        <input type="hidden" id="expenseId" value="${expense.expenseId}">
                        <input type="text" id="updateExpTitle" class="form-control" value="${expense.title!}" placeholder="Expense Title" required>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-6 form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text"><i class="fa fa-calendar" aria-hidden="true"></i></div>
                        </div>
                        <input type="text" id="updateExpDate" class="form-control datepickerBs" value="${expense.date!?date}" placeholder="Pick Date" autocomplete="off" trequired>
                    </div>
                </div>
                <div class="col-6 form-group">

                </div>
            </div>
            <div class="row">
                <div class="col-6 form-group">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text">
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
                            </div>
                        </div>
                        <input type="number" id="updateExpAmount" class="form-control" placeholder="Amount" value="${expense.amount}" required>
                    </div>
                </div>
                <div class="col-6 form-group">
                    <input type="hidden" value="${expense.paymentMode}" id="paymentModeDispData">
                    <select class="form-control" id="updateExpPaymentMode">
                        <option selected >Payment Mode</option>
                        <option value="CASH" <#if expense.paymentMode?? && expense.paymentMode =="CASH">selected</#if>>Cash</option>
                        <option value="CARD" <#if expense.paymentMode?? && expense.paymentMode =="CARD">selected</#if>>Card</option>
                    </select>
                </div>
            </div>
            <div class="row form-group">
                <div class="col">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="input-group-text">
                                <i class="fa fa-bars"></i>
                            </div>
                        </div>
                        <textarea id="updateExpDesc" rows="6" placeholder="Expense Description" class="form-control">${expense.description}</textarea>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <button class="button btn btn-primary" id="updateExpense">Update</button>
                    <a href="<@ofbizUrl>invoices</@ofbizUrl>" type="button" class="btn btn-secondary">Cancel</a>
                </div>
            </div>
        </form>
    </div>
</div>


