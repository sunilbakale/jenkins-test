$(document).ready(function () {
    $('[data-toggle="popover"]').popover();
});
function viewStudents() {
    location.href = "students"
}
function viewInvoices() {
    location.href = "invoices"
}
function viewEvents() {
    location.href = "calender"
}
function viewExpenses() {
    location.href = "invoices"
}
document.onreadystatechange = function () {
    if(document.readyState !== "complete"){
        document.querySelector("body").style.visibility = "hidden";
        document.querySelector("#loader").style.visibility = "visible";
    }else {
        document.querySelector("#loader").style.display = "none";
        document.querySelector("body").style.visibility = "visible";
    }
}

$(document).ajaxStart(function(){
    console.log("Ajax loader start")
    //TODO:Need to do ajax loading/processing status
});

$(document).ajaxComplete(function(){
    console.log("Ajax loader completed")
    //TODO:Need to do ajax loading/processing status
});
