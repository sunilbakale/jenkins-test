$(document).ready(function () {
    $('#firstName').on('keypress',function () {
        $(this).removeClass('border-danger')
        $('#firstNameErrMsg').addClass('hide')
    })
    $('#lastName').on('keypress',function () {
        $(this).removeClass('border-danger')
        $('#lastNameErrMsg').addClass('hide')
    })
    $('#password').on('keypress',function () {
        $(this).removeClass('border-danger')
    })
    $('#saveMyAccount').click(function () {
        var partyId = $('#partyId').val()
        var firstName = $('#firstName').val()
        var lastName = $('#lastName').val()
        var mailId = $('#mailId').val()
        var mobile = $('#mobile').val()
        var userInfo = {};
        if (firstName.length != 0 && lastName.length != 0){
            userInfo.partyId = partyId;
            userInfo.firstName = firstName;
            userInfo.lastName = lastName;
            userInfo.email = mailId;
            userInfo.mobile = mobile;

            var updateUserUrl = getCompleteUrl("updateAcademy")
            $.ajax({
                type: "POST",
                url: updateUserUrl,
                data: userInfo,
                success:function (response) {
                    if(response.STATUS === 'success'){
                        bs4pop.notice('saved Successfully',{
                            type: 'success',
                            position: 'topright',
                            appendType: 'append',
                            autoClose :5000,
                        })
                    }else {
                        bs4pop.notice('Unable to save',{
                            type: 'danger',
                            position: 'topright',
                            appendType: 'append',
                            autoClose :5000
                        })
                    }
                },
                error:function () {
                },
                datatype:"json"
            })
        }else {
            if(firstName.length == 0){
                $('#firstName').addClass("border-danger")
                $('#firstNameErrMsg').removeClass('hide')
            }
            if(lastName.length == 0){
                $('#lastName').addClass("border-danger")
                $('#lastNameErrMsg').removeClass('hide')
            }
        }
    })
    $('#preferenceSaveBtn').click(function () {
            var currencyType = $('#preferredCurrency').val();
            bs4pop.notice("Saving...",{
            type: 'primary',
            position : 'topright',
            appendType: 'append',
            autoClose :500
        })
        var preferredCurrencyUrl = getCompleteUrl("updatePreferredCurrency");

        $.ajax({
            type: "POST",
            url: preferredCurrencyUrl,
            data: {preferredCurrency: currencyType},
            success:function (response) {
                if(response.STATUS === 'success'){
                    bs4pop.notice('saved Successfully',{
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose :3000,
                    })
                }else {
                    bs4pop.notice('Unable to save',{
                        type: 'danger',
                        position: 'topright',
                        appendType: 'append',
                        autoClose :3000
                    })
                }
            },
            error:function () {
            },
            datatype:"json"
        })
    })
    $('#newPassword').keyup(function () {
        var changePwdBtn = $('#changePwdBtn');
        var newPassword = $('#newPassword');
        var lowerCaseLetters = /[a-z]/g;
        if(!newPassword.val().match(lowerCaseLetters)) {
            $(newPassword).addClass('border-danger')
            $('#lowerCaseLetterErr').addClass('text-danger')
            $('#lowerCaseLetterErrInfo').removeClass('d-none')
            $(changePwdBtn).prop('disabled', true);
        } else {
            $(newPassword).removeClass('border-danger')
            $('#lowerCaseLetterErr').removeClass('text-danger')
            $('#lowerCaseLetterErrInfo').addClass('d-none')
            $(changePwdBtn).prop('disabled', false);
        }
        var upperCaseLetters = /[A-Z]/g;
        if(!newPassword.val().match(upperCaseLetters)) {
            $(newPassword).addClass('border-danger')
            $('#upperCaseLetterErr').addClass('text-danger')
            $('#upperCaseLetterErrInfo').removeClass('d-none')
            $(changePwdBtn).prop('disabled', true);

        } else {
            $(newPassword).removeClass('border-danger')
            $('#upperCaseLetterErr').removeClass('text-danger')
            $('#upperCaseLetterErrInfo').addClass('d-none')
            $(changePwdBtn).prop('disabled', false);
        }
        var numbers = /[0-9]/g;
        if(!newPassword.val().match(numbers)) {
            $(newPassword).addClass('border-danger')
            $('#numberErr').addClass('text-danger')
            $('#numberErrInfo').removeClass('d-none')
            $(changePwdBtn).prop('disabled', true);
        } else {
            $(newPassword).removeClass('border-danger')
            $('#numberErr').removeClass('text-danger')
            $('#numberErrInfo').addClass('d-none')
            $(changePwdBtn).prop('disabled', false);
        }
        if (newPassword.val().length >= 8){
            $(newPassword).removeClass('border-danger')
            $('#characterErr').removeClass('text-danger')
            $('#charErrInfo').addClass('d-none')
            $(changePwdBtn).prop('disabled', false);
        }else {
            $(newPassword).addClass('border-danger')
            $('#characterErr').addClass('text-danger')
            $('#charErrInfo').removeClass('d-none')
            $(changePwdBtn).prop('disabled', true);
        }
        $(newPassword).addClass('border-success')
    })
    $('#confirmPassword').keyup(function () {
        var confirmPwd =  $('#confirmPassword');
        var newPwd = $('#newPassword');
        var changePwdBtn = $('#changePwdBtn');
        if (confirmPwd.val() !== newPwd.val()){
            $(confirmPwd).addClass('border-danger')
            $(changePwdBtn).prop('disabled', true);
        }else {
            $(confirmPwd).removeClass('border-danger')
            $(confirmPwd).addClass('border-success')
            $(changePwdBtn).prop('disabled', false);
        }
    })
    $('#pwdResetBtn').click(function () {
        $('#newPassword').val('')
        $('#confirmPassword').val('')
        $('#currentPassword').val('')
    })
})
function changePwd() {
    var curPwdVal = $('#currentPassword').val()
    var newPwdVal = $('#newPassword').val()
    var verifyPwd = $('#confirmPassword').val()
    var pwdInputInfo = {};
    if (curPwdVal.length !== 0 && newPwdVal.length !== 0){
        pwdInputInfo.currentPassword = curPwdVal;
        pwdInputInfo.newPassword = newPwdVal;
        pwdInputInfo.newPasswordVerify = verifyPwd;

        var updatePwdUrl = getCompleteUrl("updatePassword")
        $.ajax({
            method: "POST",
            url: updatePwdUrl,
            data: pwdInputInfo,
            success: function (response) {
                if(response.message === "success"){
                    bs4pop.notice('Password saved.',{
                        type: 'success',
                        position: 'topright',
                        appendType: 'append',
                        autoClose :3000,
                    })
                    location.reload()
                }else {
                    bs4pop.notice(""+response.info+"",{
                        type: 'danger',
                        position: 'topright',
                        appendType: 'append',
                        autoClose :3000,
                    })
                }
            },
            error: function (response) {

            },
            type: "json"
        })
    }
}
function validateSignUpInputs(){
    var name = $('#name');
    var email = $('#emailId');
    var password = $('#password');
    var passwordVerify = $('#passwordVerify');
    if(name.val() === ""){
        $(name).addClass('border-danger')
        $(name).focus()
        $('#nameErr').removeClass('d-none')
        return false;
    }else {
        $(name).removeClass('border-danger')
        $('#nameErr').addClass('d-none')
    }
    if (email.val() === ""){
        $(email).addClass('border-danger');
        $(email).focus()
        $('#emailErr').removeClass('d-none')
        return false;
    }else {
        $(email).removeClass('border-danger');
        $('#emailErr').addClass('d-none')
    }
    if (password.val() === ""){
        $(password).addClass('border-danger');
        $(password).focus()
        $('#pwdErr').removeClass('d-none')
        return false;
    }else {
        $(password).removeClass('border-danger');
        $('#pwdErr').addClass('d-none')
    }
    if (passwordVerify.val() === ""){
        $(passwordVerify).addClass('border-danger');
        $(passwordVerify).focus()
        $('#pwdVerifyErr').removeClass('d-none')
        return false;
    }else {
        $(passwordVerify).removeClass('border-danger');
        $('#pwdVerifyErr').addClass('d-none')
    }
    if (validatePwdField() !== true){
        $(password).focus()
        return false;
    }
    if (password.val() !== passwordVerify.val()){
        $(password).addClass('border-danger');
        $(passwordVerify).addClass('border-danger');
        $(passwordVerify).focus()
        $('#pwdMatchErr').removeClass('d-none')
        return false;
    }else {
        $(password).removeClass('border-danger');
        $(passwordVerify).removeClass('border-danger');
        $('#pwdMatchErr').addClass('d-none')
    }

    return true;
}
function validateNewPwd(){
    $('#pwdErr').addClass('d-none')
    validatePwdField();
}
function validatePwdField() {
    var password = $('#password');
    var signUpBtn = $('#signUpBtn')
    var lowerCaseLetters = /[a-z]/g;
    if(!password.val().match(lowerCaseLetters)) {
        $(password).addClass('border-danger')
        $('#signUpErrMsg').removeClass('d-none')
        return false;
    } else {
        $(password).removeClass('border-danger')
        $('#signUpErrMsg').addClass('d-none')
    }
    var upperCaseLetters = /[A-Z]/g;
    if(!password.val().match(upperCaseLetters)) {
        $(password).addClass('border-danger')
        $('#signUpErrMsg').removeClass('d-none')
        return false;
    } else {
        $(password).removeClass('border-danger')
        $('#signUpErrMsg').addClass('d-none')
    }
    var numbers = /[0-9]/g;
    if(!password.val().match(numbers)) {
        $(password).addClass('border-danger')
        $('#signUpErrMsg').removeClass('d-none')
        return false;
    } else {
        $(password).removeClass('border-danger')
        $('#signUpErrMsg').addClass('d-none')
    }
    if (password.val().length >= 8){
        $(password).removeClass('border-danger')
        $('#signUpErrMsg').addClass('d-none')
    }else {
        $(password).addClass('border-danger')
        $('#signUpErrMsg').removeClass('d-none')
        return false;
    }
    $(password).addClass('border-success')
    return true;
}
function validatePwdVerify() {
    $('#pwdVerifyErr').addClass('d-none')
    var confirmPwd =  $('#passwordVerify');
    var newPwd = $('#password');
    if (confirmPwd.val() !== newPwd.val()){
        $(confirmPwd).addClass('border-danger')
    }else {
        $(confirmPwd).removeClass('border-danger')
        $('#password').removeClass('border-danger')
        $('#pwdMatchErr').addClass('d-none')
        $(confirmPwd).addClass('border-success')
    }
}
function signUp() {
    var plan = $("input[name=plan]:checked").val();
    var name = $('#name').val();
    var emailId = $('#emailId').val();
    var password = $('#password').val();
    var passwordVerify = $('#passwordVerify').val();

    if (validateSignUpInputs() === true)
    {
        var signUpInputData = {};
        signUpInputData.planType = plan;
        signUpInputData.name = name;
        signUpInputData.emailId = emailId;
        signUpInputData.password = password;
        signUpInputData.passwordVerify = passwordVerify;

        var createThAccountUrl = getCompleteUrl("createTeacherHelperAccount");
        $.ajax({
            url : createThAccountUrl,
            type : "POST",
            data : signUpInputData,
            success : function (response) {
                if(response.status === "success"){
                    console.log("SUCCESS")
                }else{
                    console.log("ERROR")
                }
            },
            error : function () {

            },
            datatype : 'json'
        })
    }

}