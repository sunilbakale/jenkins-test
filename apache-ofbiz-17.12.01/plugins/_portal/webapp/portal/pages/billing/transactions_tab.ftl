<div class="cth-tab-content">

    <div>
        <div class="nav navbar bg-light">
            <div class="float-left row">
                <form >
                <div class="input-group">
                    <div class="input-group-prepend">
                        <div class="input-group-text">
                            <i class="fa fa-calendar" aria-hidden="true"></i>
                        </div>
                    </div>
                        <input class="form-control col-3 datepickerBs" id="fromDateTx" type="text" placeholder="From date" autocomplete="off" title="From date" required/>&nbsp;
                    <div class="input-group-prepend">
                        <div class="input-group-text">
                            <i class="fa fa-calendar" aria-hidden="true"></i>
                        </div>
                    </div>
                        <input class="form-control col-3 datepickerBs" id="thruDateTx" type="text" placeholder="Till date" autocomplete="off" title="Till Date" required/>
                    &nbsp;
                    <button class="btn btn-primary btn-sm" type="button" onclick="txLoadByDate()">Apply</button>
                </div>
                </form>
            </div>
            <div class="input-group-append float-right">
                <input type="text" class="form-control" placeholder="Search Transaction.." id="searchTransaction"/>
                <button class="btn btn-secondary" type="button">
                    <i class="fa fa-search"></i>
                </button>
            </div>
        </div>
        <#if transactionList?? && transactionList?size &gt; 0>
        <table class="table">

            <caption class="sticky">
                    Total transactions: ${transactionList!?size}<br/>
                    <span class="text-success ">Total Payment: <@ofbizCurrency amount = totalPayment isoCode = preferredCurrency.currencyType!/></span><br/>
                    <span class="text-danger ">Total Expense: <@ofbizCurrency amount = totalExpense isoCode = preferredCurrency.currencyType!/></span>
                </caption>

            <thead>
            <tr>
                <th>#</th>
                <th>Id</th>
                <th>Date</th>
                <th>Invoice/Expense</th>
                <th>Amount</th>
            </tr>
            </thead>
            <tbody class="bg-white">
            <#list transactionList?sort_by("transactionTime") as txLog>
                <input type="hidden" value="${txLog.transactionId}" id="txId"/>
            <tr>
                    <td>${txLog_index + 1}</td>
                    <td>${txLog.transactionId!}</td>
                    <td>${txLog.transactionTime!?date}</td>
                    <td>${txLog.transactionType!}</td>
                <#if txLog.transactionType=="Payment">
                    <td class="text-success">
                        <@ofbizCurrency amount = txLog.transactionAmount isoCode = preferredCurrency.currencyType!/>
                    </td>
                <#else>
                    <td class="text-danger">
                        <@ofbizCurrency amount = txLog.transactionAmount isoCode = preferredCurrency.currencyType!/>
                    </td>
                </#if>
            </tr>
            </#list>
            </tbody>
        </table>

        <#else>
            <div style="text-align: center" class="p-4">
                <img src="../static/lottiefiles/629-empty-box.gif" width="300" class="center" />
                <div class="text-muted">No Transaction's found. Please choose the date's of transaction</div>
            </div>
        </#if>
    </div>

</div>
