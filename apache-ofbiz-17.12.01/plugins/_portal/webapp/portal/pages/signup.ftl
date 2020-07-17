


<div class="container">

    <div>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="/">Teachers Helper</a>
        </nav>
    </div>

    <div class="row">
        <div class="container container-fluid  reg_header">
            <div style="text-align: center;"><h4>Sign Up</h4></div>
        </div>
    </div>

        <div class="row">
            <div class="col-md-6">
                <div class="row justify-content-center p-3">
                    <div class="col-md-12 reg_from" style="height:350px;">
                        <h5>Choose Your Plan</h5>
                        <div>
                            <div class="row justify-content-start mt-4">
                                <#include "pricing_selection.ftl">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="row justify-content-center p-3">
                    <div class="col-md-12 reg_from" style="height:350px;">
                        <h5>Account Details</h5>
                        <div class="row align-items-center">
                            <div class="col mt-4">
                                <label for="name">Your Name / Your Academy Name</label>
                                <input type="text" class="form-control" placeholder="John" id="name" required>
                                <span class="d-none text-danger" id="nameErr">Please enter name.</span>
                            </div>
                        </div>
                        <div class="row align-items-center mt-4">
                            <div class="col">
                                <label for="Email">Email Address</label>
                                <input type="email" class="form-control" placeholder="john@gmail.com" id="emailId" required>
                                <span class="d-none text-danger" id="emailErr">Please enter email.</span>
                            </div>
                        </div>
                        <div class="row align-items-center mt-4">
                            <div class="col">
                                <label for="password">Enter Password</label>
                                <input type="password" class="form-control" placeholder="" id="password" onkeyup="validateNewPwd()" required>
                                <span class="d-none text-danger" id="pwdErr" >Please enter password.</span>
                            </div>
                            <div class="col">
                                <label for="passwordVerify">Re-enter password</label>
                                <input type="password" class="form-control" placeholder="" id="passwordVerify" onkeyup="validatePwdVerify()" required>
                                <span class="d-none text-danger" id="pwdVerifyErr">Please enter password.</span>
                            </div>
                        </div>
                        <span class="d-none text-danger" id="pwdMatchErr">Entered password not matching.</span>
                        <span class="d-none text-danger" id="signUpErrMsg">Password must contain a capital,lower case letter,a digit and 8 letters.</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="row justify-content-start">
            <div class="col">
                <div class="col">
                    <div class="row justify-content-center">
                        <button class="btn btn-primary btn-lg mt-4 float-left" onclick="signUp()" id="signUpBtn" >
                            <i class="fa fa-sign-in p-2" aria-hidden="true"></i>
                            <strong>Sign up</strong></button>
                    </div>
                </div>
                <div class="text-muted mt-4">
                    By signing up you agree to the <a href="#">Terms of Use</a> and <a href="#">Privacy Policy</a>
                </div>
                <br/>
                <div class="text-muted ">
                    If you already have a account <a href="<@ofbizUrl>loginpage</@ofbizUrl>" class="text"> Login here</a><br/>
                </div>
                <br/>
            </div>
        </div>
</div>

<br/><br/><br/><br/>