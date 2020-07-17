<div class="cth-tab-content">

    <div class="nav navbar bg-light">
        <a href="<@ofbizUrl>new_expense</@ofbizUrl>" class="btn btn-primary">
            <i class="fa fa-plus-circle" aria-hidden="true"></i> New Expense</a>

        <div class="input-group-append float-right">
            <input type="text" class="form-control col-md-8" placeholder="Search Expenses.." id="searchExpenses"/>
            <button class="btn btn-secondary" type="button">
                <i class="fa fa-search"></i>
            </button>
        </div>
    </div>
    <div class="">
        <#if expenses?? && expenses?size &gt; 0>
            <table class="table table-hover cthtable">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Expense No</th>
                    <th>Title</th>
                    <th>Date</th>
                    <th>Payment mode</th>
                    <th>Amount</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="invoices_table" class="bg-white border-bottom-0">
                <#list expenses as expense>
                    <tr>
                        <td>${expense_index + 1}</td>
                        <td>
                            <a href="<@ofbizUrl>editExpense</@ofbizUrl>?expenseId=${expense.expenseId}">${expense.expenseId}</a>
                        </td>
                        <td>${expense.title!}</td>
                        <td>
                            <#if expense.date??>
                                ${expense.date!?date}
                            </#if>
                        </td>
                        <td>${expense.paymentMode!}</td>
                        <td>
                            <@ofbizCurrency amount = expense.amount!?number isoCode = preferredCurrency.currencyType!/>
                        </td>
                        <td>
                            <input type="hidden" value="${expense.expenseId}" id="expenseId"/>
                            <a href="<@ofbizUrl>editExpense</@ofbizUrl>?expenseId=${expense.expenseId}" class="btn btn-outline-success">
                                <i class="fa fa-pencil" aria-hidden="true"></i></a>
                            <a href=""
                                    class="btn btn-outline-danger"
                                    data-toggle="modal"
                                    data-target="#expDelModal"
                                    data-expense-id="${expense.expenseId}"
                                    id="delModalBtn">
                                <i class="fa fa-trash-o" aria-hidden="true"></i></a>
                        </td>
                    </tr>
                </#list>
                </tbody>
                <caption class="sticky">
                    <span class="float-left"> Total Expenses: ${expenses!?size}</span>
                    <span class="float-right"> Total Expense Amount: <@ofbizCurrency amount = totalExpense?number isoCode = preferredCurrency.currencyType!/></span>
                </caption>
            </table>
            <form id="delete_expense_form" action="<@ofbizUrl>remove_expense</@ofbizUrl>">
                <input type="hidden" id="deleteExpenseId">
            </form>
        <#else>
            <div style="text-align: center" class="p-4">
                <img src="../static/lottiefiles/629-empty-box.gif" width="300" class="center" />
                <div class="text-muted">No expenses found</div>
                <div>
                    <a href="<@ofbizUrl>new_expense</@ofbizUrl>" class="btn btn-link">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i> Add Now</a>
                </div>
            </div>
        </#if>
    </div>
</div>
<div class="modal" id="expDelModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <div class="page-title">Delete expense</div>
                <button class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <center><p id="expId"></p>
                    <div class="label">Are you sure you want to delete this expense ?</div>
                    <div class="">

                        <button class="btn btn-danger btn-sm" onclick="deleteExpense()">Yes</button>
                        <button class="btn btn-secondary btn-sm" data-dismiss="modal">No</button>
                    </div>
                </center>
            </div>
        </div>
    </div>
</div>