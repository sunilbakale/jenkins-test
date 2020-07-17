$(function() {
    // initializeInvoiceData();
});
$(document).ready(function () {

    $('[data-toggle="popover"]').popover();
    $("#collapseBtn ").click(function(){
        $("#hiddenRow").toggle();
    });
    $("#searchInvoice").on("keyup", function() {
        var value = $(this).val().toLowerCase();
        $(".cthtableInvoice *").filter(function() {
            $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
        });
    });
    jQuery('#print_invoice').click(function(){
        window.print();
        return false;
    });
    $('#amount').on('keypress',function () {
        $(this).removeClass('border-danger')
    })
    jQuery('#save_payment').click(function(){
        var amount = $('#amount').val();
        if(amount == ""){
            $('#amount').addClass('border-danger')
        }else {
            $('#amount').removeClass('border-danger')
            applyInvoicePayment()
        }
        location.reload();
    });

    $('#cancelInvoiceBtn').click(function () {

        var invoiceId = $('#invoiceId').val();
        var cancelAcademyInvoice = getCompleteUrl('cancelAcademyInvoice');
        $.ajax({
                type: "POST",
                url: cancelAcademyInvoice,
                data: {invoiceId:invoiceId},
                success: function (response) {
                    if (response.STATUS == 'SUCCESS') {

                        $('#cancelInvoice').modal('hide');
                        bs4pop.notice('Invoice Cancelled', {
                            type: 'success',
                            position: 'topright',
                            appendType: 'append',
                            autoClose: 5000,
                            width: 6000
                        })
                        $('#invoices_tab').load(getCompleteUrl("invoices_tab"));
                    } else {
                        bs4pop.notice('Unable to cancel Invoice', {
                            type: 'danger',
                            position: 'topright',
                            appendType: 'append',
                            autoClose: 5000,
                            width: 6000
                        })
                        $('#invoices_tab').load(getCompleteUrl("invoices_tab"));
                    }
                },
                error: function (response) {
                },
                datatype: "json"
            }
        )
    })
    $('#viewInvoices').click(function () {
        $('#navInvoiceBtn').tab('show')
    })
    $('#viewExpenses').click(function () {
        $('#navExpenseBtn').tab('show')
    })
    $('#viewTx').click(function () {
        $('#navTxBtn').tab('show')
    })
});

function applyInvoicePayment() {
    var edit_invoiceInfo = {};

    var invoiceId = $('#invoiceId');
    edit_invoiceInfo.invoiceId = invoiceId.val();

    var studentId = $('#studentId');
    edit_invoiceInfo.studentId = studentId.val();

    var amount = $('#amount');
    edit_invoiceInfo.amount = amount.val();

    var payment_method = $('#Payment_Method').val();
    edit_invoiceInfo.payment_method = payment_method

    var paymentDesc = $('#payment_desc');
    edit_invoiceInfo.paymentDesc = paymentDesc.val();

    var updateInvoiceUrl = getCompleteUrl("updateInvoicePayment");

    $.ajax({
        type: "POST",
        url: updateInvoiceUrl,
        data: edit_invoiceInfo,
        success:function (response) {
            if(response.STATUS == "SUCCESS"){
                $('#addPayment_modal').modal('hide');

                bs4pop.notice('Invoice Updated Successfully ',{
                    type: 'success',
                    position: 'topright',
                    appendType: 'append',
                    autoClose :5000
                })
                $('#invoices_tab').load(getCompleteUrl("invoices_tab"));
                $('#addPayment_modal').modal('hide');
            }
            else
            {
                $('#addPayment_modal').modal('hide');
                bs4pop.notice('Invoice Update Error ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
                $('#invoices_tab').load(getCompleteUrl("invoices_tab"));
            }
        },
        error:function () {
        }
    })
}
function applyInvoicesFilter() {
    var studentList = [];
    $('#checkedStudents:checked').each(function() {
        studentList.push($(this).val())
    })
    var startDate = $('#startDate').val();
    var endDate = $('#endDate').val()

    var statusList = [];
    $('#paidOrUnpaid:checked').each(function() {
        statusList.push($(this).val())
    })
    loadInvoices(startDate,endDate,studentList,statusList)
}
function loadInvoices(startDate,endDate,studentList,statusList) {
    // var loadInvoiceInput = {startDate:startDate,endDate:endDate,studentList:studentList,statusList:statusList};
    // var loadInvoiceUrl = getCompleteUrl("invoices_tab")
         $('#invoices_tab').load(getCompleteUrl("invoices_tab?startDate="+startDate+"&endDate="+endDate+"&studentList="+studentList+"&statusList="+statusList),
             function () {
                 $('.datepickerBs').datepicker({
                     autoClose: true,
                     language: 'en',
                 })
                 $('#startDate').val(startDate)
                 $('#endDate').val(endDate)
             })
}
function initInvoiceData(invoiceId,studentId) {
    $('#studentId').val(studentId).trigger('change')
    $('#invoiceId_show').text(invoiceId).trigger('change')
    $('#invoiceId').val(invoiceId).trigger('change')
    // $('#invoiceIdHidden').val(invoiceId).trigger('change')

}