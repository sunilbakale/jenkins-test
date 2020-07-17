$(function () {
    // initialize things..
});

function showSuccessToast(msg) {
    if(!msg) msg = "Transaction completed successfully."
    $("#success_toaster_message").text(msg)
    $('#success_toaster').toast('show');
}

function showErrorToast(msg) {
    if(!msg) msg = "Error performing this transaction."
    $("#error_toaster_message").text(msg)
    $('#error_toaster').toast('show');
}