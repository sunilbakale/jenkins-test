$(document).ready(function () {
    var curDate = new Date();
    curDate.setMonth(new Date().getMonth());
    $('#invoice_date').val(new Date(curDate).toLocaleDateString());

    jQuery().invoice({
        addRow : "#addRow",
        delete : "#delete",
        parentClass : ".item-row",

        price : ".price",
        qty : ".qty",
        total : ".total",
        totalQty: "#totalQty",

        subtotal : "#subtotal",
        discount: "#discount",
        shipping : "#shipping",
        grandTotal : "#grandTotal"
    });
    var $TABLE = $('.tabclass');
    var $BTN = $('#getval');
    jQuery.fn.pop = [].pop;
    jQuery.fn.shift = [].shift;
    $BTN.click(function() {
        var $rows = $TABLE.find(    'tr:not(:hidden)');
        var headers = [];

        var data = [];
        $($rows.shift()).find('th:not(:empty):not([data-attr-ignore])').each(function() {
            headers.push($(this).text().toLowerCase());
        });
        $rows.each(function() {
            var $td = $(this).find('input');
            var h = {};
            var h1={};
            headers.forEach(function(header, i) {
                h[header] = $td.eq(i).val();
            });
            data.push(h);
        });
        var newInvoiceInfo = {};

        newInvoiceInfo.invoiceItems = data;

        var invoiceDatePick = $('#invoice_date');
        var invoiceDate = new Date(invoiceDatePick.val());
        newInvoiceInfo.invoiceDate = invoiceDate.getTime();

        newInvoiceInfo.studentId = $('#stdForInvoice').val();
        var encodedNewInvoiceInfo = btoa(JSON.stringify(newInvoiceInfo))
        var createInvoiceUrl = getCompleteUrl("createNewInvoice")

        var reqInputs = {encodedInvoiceInfo: encodedNewInvoiceInfo}
        $.ajax({
            type: "POST",
            url : createInvoiceUrl,
            data: reqInputs,
            success:function (response) {
                console.log(response);

                if(response.STATUS ==="SUCCESS") {
                    bs4pop.notice('New invoice created successful.',{
                        type:'success',
                        position:'topright',
                        appendType: 'append',
                        autoClose: 5000
                    })
                     location.href = "invoices";
                } else {
                    bs4pop.notice(response._ERROR_MESSAGE_,{
                        type:'danger',
                        position:'topright',
                        appendType: 'append',
                        autoClose: 5000,
                        width:  6000
                    })
                }
            },
            error:function () {
                console.log("response received FAILURE");
            },
            dataType: "json",
        });
    });
    var tbl = $('.tabclass tr').get().map(function(row) {
        return $(row).find('td').get().map(function(cell) {
            return $(cell).html();
        });
    });
})

