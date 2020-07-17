/*
$(document).ready(function () {
    jQuery('#save_payment').click(function(){
        applyInvoicePayment()
    });

    $('#cancelInvoice').click(function () {
        console.log("To be implemented..."); // TODO:
    })
});



function applyInvoicePayment() {
    var edit_invoiceInfo = {};

    var invoiceId = $('#invoiceId');
    console.log("invoiceId"+invoiceId.val());
    edit_invoiceInfo.invoiceId = invoiceId.val();

    var studentId = $('#studentId');
    console.log("studentId"+studentId.val());
    edit_invoiceInfo.studentId = studentId.val();

    var amount = $('#amount');
    edit_invoiceInfo.amount = amount.val();

    var payment_method = $('Payment_Method').val();

    var paymentDesc = $('#payment_desc');
    edit_invoiceInfo.paymentDesc = paymentDesc.val();

    var updateEventUrl = getCompleteUrl("updateInvoicePayment");

    $.ajax({
        type: "POST",
        url: updateEventUrl,
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
                location.reload();
                // $('#addPayment_modal').modal('hide');
            }
            else
            {m
                console.log("ERROR")
                /!*window.location="edit_invoice";*!/
                $('#addPayment_modal').modal('hide');

                bs4pop.notice('Invoice Update Error ', {
                    type: 'danger',
                    position: 'topright',
                    appendType: 'append',
                    autoClose: 5000,
                    width: 6000
                });
            }
        },
        error:function () {
            console.log("error");
        }
    })
}
*/
