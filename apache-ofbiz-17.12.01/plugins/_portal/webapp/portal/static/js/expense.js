$(function() {
    initializeExpenseData();
});

$(document).ready(function () {
    var curDateForTx = new Date();
    $('#fromDateTx').val(curDateForTx.toLocaleDateString()).trigger('change');
    $('#thruDateTx').val(new Date(curDateForTx.setMonth(curDateForTx.getMonth()+1)).toLocaleDateString());

    $('#createExp').on('click', function () {
        var createExpenseInfo = {};

        var expTitle = $('#expTitle').val();
        if (expTitle == "") {
            console.log("fill the fields")
        } else {

            createExpenseInfo.expTitle = expTitle;

            var expDate = new Date($('#expDate').val());
            createExpenseInfo.expDate = expDate.getTime();

            var expAmount = $('#expAmount').val();
            createExpenseInfo.expAmount = expAmount;

            var paymentMethod = $('#expPaymentMode').children('option:selected').val();
            createExpenseInfo.expPayMode = paymentMethod;
            console.log(paymentMethod)

            var expDesc = $('#expDesc').val();
            createExpenseInfo.expDesc = expDesc;

            var createExpenseUrl = getCompleteUrl('createExpense')
            $.ajax({
                    type: "POST",
                    url: createExpenseUrl,
                    data: createExpenseInfo,
                    success: function (response) {
                        if (response.STATUS == 'SUCCESS') {
                            window.location.href = "invoices";
                            // $('#expenses').addClass('active'); Need to select expenses tab
                            bs4pop.notice('Expense created successful', {
                                type: 'success',
                                position: 'topright',
                                appendType: 'append',
                                autoClose: 5000,
                                width: 6000
                            })
                        } else {
                            bs4pop.notice('Unable to create Expense', {
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
                    datatype: "json"
                }
            )
        }
    });
    $('#updateExpense').on('click', function () {

        var updateExpenseInfo = {};

        var expenseId = $('#expenseId').val();
        updateExpenseInfo.expenseId = expenseId;
        var title = $('#updateExpTitle').val();
        updateExpenseInfo.title = title;
        var date = new Date($('#updateExpDate').val()).getTime();
        updateExpenseInfo.date = date;
        var amount = $('#updateExpAmount').val();
        updateExpenseInfo.amount = amount;
        var payMode = $('#updateExpPaymentMode').val();
        updateExpenseInfo.paymentMode = payMode;
        var desc = $('#updateExpDesc').val();
        updateExpenseInfo.description = desc;

        console.log(title + "" + date + "" + amount + "" + payMode + "" + desc);
        var updateExpenseUrl = getCompleteUrl("updateExpense");


        $.ajax({
            type: "POST",
            url: updateExpenseUrl,
            data: updateExpenseInfo,
            success: function (response) {
                if (response.STATUS == "SUCCESS") {

                    bs4pop.notice('Expense Updated Successfully ', {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000
                    })
                } else {
                    console.log("ERROR")
                    bs4pop.notice('Unable to Update Expense ', {
                        type: 'danger',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    });
                }
            }
        });
    });

});
function deleteExpense() {
    var expenseId = $("#deleteExpenseId").val()
    var postData = {expenseId: expenseId};
    var deleteExpenseUrl = getCompleteUrl("remove_expense");
    $.ajax({
            url: deleteExpenseUrl,
            type: "POST",
            data: postData,
            success: function (response) {
                if (response.STATUS == "SUCCESS"){
                    $('#expDelModal').modal('hide');
                    bs4pop.notice('Expense deleted successfully ',
                        {
                            type: 'success',
                            position: 'topright',
                            appendType: 'append',
                            autoClose: 5000,
                            width: 6000
                        })
                    location.reload();
                }else {
                    $('#expDelModal').modal('hide');
                    bs4pop.notice('Unable to delete expense ',
                        {
                            type: 'danger',
                            position: 'topright',
                            appendType: 'append',
                            autoClose: 5000,
                            width: 6000
                        })
                }

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error: " + errorThrown);
            }
        });
}
function transactionDate(fromDate,thruDate){
    var inputTransaction = {};
    inputTransaction.fromDate = fromDate;
    inputTransaction.thruDate = thruDate;
    var getTransactionUrl = getCompleteUrl("transaction");
    $.ajax({
        type: "POST",
        data: inputTransaction,
        url: getTransactionUrl,
        success:function () {
            bs4pop.notice('Transactions loaded ',
                            {
                                type: 'success',
                                position: 'topright',
                                appendType: 'append',
                                autoClose: 5000,
                                width: 6000
                            })
        },
        error: function (response) {
        },
        dataType: "json",
    });
}
function updateTxDates(fromDate,thruDate){
    $('#fromDateTx').val(fromDate).trigger('change');
    $('#thruDateTx').val(thruDate).trigger('change');
}

function txLoadByDate() {
    var fromDateValtx = $('#fromDateTx').val();
    var thruDateValtx = $('#thruDateTx').val()
    if(fromDateValtx == "" || thruDateValtx == ""){
            bs4pop.notice('Please choose the date',
                {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                })
        }else {
        $('#transactions').load(getCompleteUrl("transaction?fromDate=" + new Date($('#fromDateTx').val()).toLocaleDateString() + "&thruDate=" + new Date($('#thruDateTx').val()).toLocaleDateString()),
            function () {
                bs4pop.notice('Transactions list updated ',
                    {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    }),
                    $('.datepickerBs').datepicker({
                        autoClose: true,
                        language: 'en'
                    }),
                    updateTxDates(fromDateValtx,thruDateValtx )
            })    }


}

$('#fromDateTx').datepicker({
    onSelect:function () {
        var startDateVal = new Date($('#fromDateTx').val());
        var endDateVal = new Date(startDateVal.setMonth(startDateVal.getMonth()+1)).toLocaleDateString() ;
        var startDateValAgain = new Date($('#fromDateTx').val()).toLocaleDateString();
        $('#thruDateTx').val(endDateVal).trigger('change')

        $('#transactions').load(getCompleteUrl("transaction?fromDate=" + new Date($('#fromDateTx').val()).toLocaleDateString() + "&thruDate=" + endDateVal),
            function () {
                bs4pop.notice('Transactions loaded ',
                    {
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width: 6000
                    }),
                    $('.datepickerBs').datepicker({
                        autoClose: true,
                        language: 'en'
                    }),
                    updateTxDates(startDateValAgain,endDateVal)
            })
    }
});
$('#thruDateTx').datepicker({
    onSelect:function () {
        var endDateVal = new Date($('#thruDateTx').val()).getTime();
        var startDateVAl = new Date($('#fromDateTx').val()).getTime();
        if(endDateVal >= startDateVAl){
            $('#thruDateTx').css({'border-color':'#ced4da','color':'#495057'});
            transactionDate(new Date(startDateVAl).toLocaleDateString(),new Date(endDateVal).toLocaleDateString())
        }else {
            $('#thruDateTx').css({'border-color':'red','color':'red'});
        }
    }
});
function initializeExpenseData() {
    $('#expDelModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var expenseId = button.data('expense-id');
        console.log(expenseId)
        // var deletePartyName = button.data('party-name');
        // if(deletePartyName == null) deletePartyName = "";

        // var modal = $(this)
        // modal.find('#deleteAdminUser_partyName').text(deletePartyName);
        $("#deleteExpenseId").val(expenseId)
    });
}