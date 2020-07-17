<#if requestAttributes.errorMessageList?has_content>
    <#assign errorMessageList=requestAttributes.errorMessageList>
</#if>
<#if requestAttributes.eventMessageList?has_content>
    <#assign eventMessageList=requestAttributes.eventMessageList>
</#if>
<#if requestAttributes.serviceValidationException??>
    <#assign serviceValidationException=requestAttributes.serviceValidationException>
</#if>
<#if requestAttributes.uiLabelMap?has_content>
    <#assign uiLabelMap=requestAttributes.uiLabelMap>
</#if>

<#if !errorMessage?has_content>
    <#assign errorMessage=requestAttributes._ERROR_MESSAGE_!>
</#if>
<#if !errorMessageList?has_content>
    <#assign errorMessageList=requestAttributes._ERROR_MESSAGE_LIST_!>
</#if>
<#if !eventMessage?has_content>
    <#assign eventMessage=requestAttributes._EVENT_MESSAGE_!>
</#if>
<#if !eventMessageList?has_content>
    <#assign eventMessageList=requestAttributes._EVENT_MESSAGE_LIST_!>
</#if>


<div class="container">
    <div>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="/">Teachers Helper</a>
        </nav>
    </div>

    <form action="<@ofbizUrl>login</@ofbizUrl>" class="form" method="post">
    <div class="row justify-content-center p-4">
        <div class="col-12 col-md-8 col-lg-8 col-xl-6 reg_from">
            <div class="row">

                <div class="container container-fluid  reg_header">
                    <div style="text-align: center;"><h4>Login</h4></div>
                </div>
            </div>

            <div>
                <#list errorMessageList as error>
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </#list>
            </div>

            <div class="row align-items-center mt-4">
                <div class="col">
                    <label for="Email">Your Email</label>
                    <input type="email" class="form-control" placeholder="" id="Email" name="USERNAME" required>
                </div>
            </div>
            <div class="row align-items-center mt-4">
                <div class="col">
                    <label for="password">Password</label>
                    <input type="password" class="form-control" placeholder="" id="password" name="PASSWORD" required>
                </div>
            </div>
            <div class="row justify-content-start mt-4">
                <div class="col">
                    <div class="row">
                        <div class="form-group form-check">
                            <label class="form-check-label col-sm-2" for="rm">
                                <input class="form-check-input" type="checkbox" name="remember" id="rm"> Remember&nbsp;me
                            </label>
                        </div>
                    </div>
                    <div style="text-align: center;">
                        <button type="submit" class="btn btn-lg btn-link">
                            <img src="../static/images/login-button-png-icon-5.png" height="50" />
                        </button>
                    </div>
                    <div class="text-muted mt-4">
                        <div>Don't have a account yet? <a href="<@ofbizUrl>signup</@ofbizUrl>" class="text">Sign Up</a> here.</div>

                        <br/><a href="<@ofbizUrl>forgot_password</@ofbizUrl>" class="text">Forgot Password?</a>
                    </div>
                </div>
            </div>
            <br/>
        </div>

    </form>

</div>
