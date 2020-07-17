
<!-- login section -->
<section class="padding-110px-tb xs-padding-60px-tb bg-white builder-bg" id="login-section">
    <div class="container">
        <div class="row">
            <div class="col-md-8 center-col col-sm-12 text-center">
                <h2 class="title-extra-large-2 alt-font xs-title-large text-sky-blue-dark margin-four-bottom tz-text">Login</h2>
                <div class="text-extra-large sm-text-extra-large text-medium-gray width-80 xs-width-100 center-col margin-twelve-bottom xs-margin-nineteen-bottom tz-text">
                    Login with your email and password.</div>
            </div>
            <div class="col-md-5 center-col col-sm-12 text-center">
                <form id="login" action="<@ofbizUrl>login</@ofbizUrl>" method="post">
                    <input type="text" name="USERNAME" id="name" tabindex="1"  data-email="required" placeholder="Your Email" class="big-input bg-light-gray alt-font border-radius-4">
                    <input type="password" name="PASSWORD" tabindex="2" id="email" data-email="required" placeholder="Password" class="big-input bg-light-gray alt-font border-radius-4">
                    <button type="submit" class="contact-submit btn btn-extra-large2 propClone bg-deep-green btn-3d text-white width-100 builder-bg tz-text">LOGIN</button>
                </form>
                <div class="margin-seven-top text-small2 sm-width-100 center-col tz-text xs-line-height-20">
                    <a href="<@ofbizUrl>forgotPassword</@ofbizUrl>" class="text-decoration-underline tz-text">I forgot my password!</a>
                </div>
                <div class="margin-seven-top text-small2 sm-width-100 center-col tz-text xs-line-height-20">
                    * We don't share your personal info with anyone. Check out our <a href="#" class="text-decoration-underline tz-text">Privacy Policy</a> for more information.
                </div>
            </div>
        </div>
    </div>
</section>
<!-- end login section -->


