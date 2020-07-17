<div class="container-fluid">
    <div class="row">
        <div class="page-title mb-2">Security</div>
    </div>
    <div class="row">
        <div class="col-6 p-0">
            <div class="form-group">
                <label for="currentPassword">Current password</label>
                <input type="password" class="form-control" id="currentPassword" autocomplete="off" required>
            </div>
            <div class="form-group">
                <label for="newPassword">New password</label>
                <input type="password" class="form-control" id="newPassword" autocomplete="off" required>
                <span class="text-danger small d-none" id="lowerCaseLetterErrInfo">
                    <span class="text-dark">* </span>Enter lower case letter.<br/>
                </span>
                <span class="text-danger small d-none" id="upperCaseLetterErrInfo">
                    <span class="text-dark">* </span>Enter upper case letter.<br/>
                </span>
                <span class="text-danger small d-none" id="numberErrInfo">
                    <span class="text-dark">* </span>Enter a number.<br/>
                </span>
                <span class="text-danger small d-none" id="charErrInfo">
                    <span class="text-dark">* </span>Enter minimum 8 characters.
                </span>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Re-enter password</label>
                <input type="password" class="form-control" id="confirmPassword" autocomplete="off" required>
            </div>
            <div class="form-group">
                <button class="btn btn-primary" id="changePwdBtn" onclick="changePwd()">Update</button>
                <button class="btn btn-secondary" id="pwdResetBtn">Reset</button>
            </div>
        </div>
        <div class="col-6">
            <div class="cthform-info">
                <div class="text-info">Password must contain the following:</div>
                <ul class="text-info">
                    <li id="lowerCaseLetterErr">A <b>lowercase</b></li>
                    <li id="upperCaseLetterErr">A <b>capital (uppercase)</b> letter</li>
                    <li id="numberErr">A <b>number</b></li>
                    <li id="characterErr">Minimum <b>8 characters</b></li>
                </ul>
            </div>
        </div>
    </div>
</div>