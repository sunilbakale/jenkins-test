<div class="container-fluid">
    <nav class="nav navbar cth_page_heading bg-light">
        <div class="page-title">New Student</div>
        <a class="nav-link  btn btn-secondary" href="<@ofbizUrl>students</@ofbizUrl>" >
          <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>
    </nav>

    <div class="container-fluid ">
        <div class="row">
            <div class="col-md-8 cthform">
                <form action="<@ofbizUrl>CreateStudent</@ofbizUrl>" method="POST" id="addstudent" class="form ">
                    <div class="form-group ">
                        <label for="StudentFirstName">First Name <i class="fa fa-asterisk cthpage_asterisk" aria-hidden="true"></i>
                        </label>
                        <input type="text" class="form-control" id="StudentFirstName" placeholder="John" name="studentfirstname" required>
                    </div>

                    <div class="form-group ">
                        <label for="StudentSecondName">Last Name <i class="fa fa-asterisk cthpage_asterisk" aria-hidden="true"></i></label>
                        <input type="text" class="form-control" id="StudentSecondName" placeholder="Doe" name="studentlastname" required>
                    </div>
                    <div class="form-group ">
                        <label for="StudentGender">Gender</label><br/>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="exampleRadios" id="exampleRadios1" value="option1" checked>
                            <label class="form-check-label" for="exampleRadios1">Male</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="exampleRadios" id="exampleRadios2" value="option2">
                            <label class="form-check-label" for="exampleRadios2">
                                Female
                            </label>
                        </div>
                        <div class="form-check form-check-inline">
                            <input class="form-check-input" type="radio" name="exampleRadios" id="exampleRadios3" value="option3">
                            <label class="form-check-label" for="exampleRadios3">
                                Not-Specified
                            </label>
                        </div>
                    </div>

                    <div class="form-group ">
                        <label for="StudentEmail">Email <i class="fa fa-asterisk cthpage_asterisk" aria-hidden="true"></i></label>
                        <input type="email" class="form-control" id="StudentEmail" placeholder="john.doe@yahoo.com" name="studentemail" required>
                    </div>
                    <div class="form-group ">
                        <label for="StudentPhone">Phone</label>
                        <input type="text" class="form-control" id="StudentPhone" placeholder="+111222333" name="studentmobile" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Add Student</button>
                    <a class="btn btn-secondary" href="<@ofbizUrl>students</@ofbizUrl>" >Cancel</a>
                </form>
            </div>
            <div class="col-md-4">
                <div class="cthform-info">
                    <div class="text-info">Few pointers:</div>
                    <ul class="text-info">
                        <li>Provide student basic details</li>
                        <li>Email will be used to send out any event notification if you choose to</li>
                        <li>Phone number is stored only for your reference, and no contact will be made by system via phone</li>
                    </ul>
                </div>
            </div>
        </div>

    </div>
</div>