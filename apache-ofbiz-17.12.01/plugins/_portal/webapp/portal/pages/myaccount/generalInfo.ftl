<div class="container-fluid">
<div class="row">
    <div class="page-title mb-2">General Information</div>
</div>
<div class="row">
    <div class="col-md-8 p-0">
        <div class="form-group">
            <label for="firstName">First Name
                <i class="fa fa-asterisk cthpage_asterisk" aria-hidden="true"></i>
            </label>
            <input type="text" class="form-control bg-white" id="firstName" value="${partyInfo.firstName!}">
            <span class="hide text-danger small" id="firstNameErrMsg">First name is required field</span>
            <input type="hidden" value="${partyInfo.partyId!}" id="partyId"/>
        </div>
        <div class="form-group ">
            <label for="lastName">Last Name
                <i class="fa fa-asterisk cthpage_asterisk" aria-hidden="true"></i>
            </label>
            <input type="text" class="form-control" id="lastName" value="${partyInfo.lastName!}">
            <span class="hide text-danger small" id="lastNameErrMsg">Last name is required field</span>
        </div>
        <div class="form-group">
            <label for="mailId">Email</label>
            <input type="email" class="form-control" id="mailId" value="${partyInfo.gmail!}" readonly disabled>
        </div>
        <div class="form-group">
            <label for="mobile">Phone</label>
            <input type="text" class="form-control" id="mobile" value="${partyInfo.mobile!}">
        </div>
        <button class="btn btn-primary" id="saveMyAccount">Save</button>
        <a class="btn btn-secondary" href="<@ofbizUrl>home</@ofbizUrl>">Cancel</a>
    </div>
    <div class="col-md-4">
        <div class="card shadow-none border-0">
            <img width="100" height="100" class="round" avatar="${partyInfo.firstName!} ${partyInfo.lastName!}" style="align-content: center"/>
            <span class="page-title">${partyInfo.firstName!} ${partyInfo.lastName!}</span>
        </div>
    </div>
</div>
</div>