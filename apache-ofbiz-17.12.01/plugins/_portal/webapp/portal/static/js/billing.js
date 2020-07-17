/*
$(document).ready(function(){

    $("#test-table-get-value").click(function() {
    var invoiceNo = $('#invoiceNo').val();
    console.log(invoiceNo);

    var invoiceDatePick = $('#invoice_date');
    var invoiceDate = new Date(invoiceDatePick.val());
    console.log(invoiceDate.getTime());

    var invoiceStudentName = $('#stdForInvoice').val();
    console.log(invoiceStudentName);

    console.log($("#test-table").FullTable("getData"));

    var newInviceInfo = {
        invoiceId : invoiceNo,
        startDate : invoiceDate.getTime()
    };
    $.ajax({
        type: "POST",
        url: newInvoiceEvent,
        data:newInviceInfo ,
        success: function(response) {
            console.log("response received");
            console.log(response);
        },
        error:function(response){
            console.log("Error");
        },
        dataType: "json",
    });
});
});*/
