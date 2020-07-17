<div class="container-fluid">
    <div class="nav navbar cth_page_heading bg-light">
        <div class="page-title">Students</div>
        <div class="input-group-append float-right ">
            <button type="button" class="btn btn-primary mr-2" data-toggle="modal"
                    data-target="#newStudentModal">
                <i class="fa fa-user-plus" aria-hidden="true"></i> New Student
            </button>

            <input type="text" class="form-control col-md-6" placeholder="Search Students" id="searchStudent"/>
            <div class="input-group-append">
                <span class="input-group-text"><i class="fa fa-search"></i></span>
            </div>
        </div>
    </div>
    <div class="modal fade" id="newStudentModal" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-md" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="newStudentModelTitle">New Student</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="container">
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
                                <div class="modal-footer">
                                    <button class="btn btn-primary" onclick="createStudent();">Create</button>
                                </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div>
        <#if students?? && students?size &gt; 0>
            <table class="table table-striped table-sm  cthtable">
                <thead class="thead-dark">
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <#list students as student>
                     <tr>
                        <td>${student_index + 1}</td>
                        <td>
                            <img width="30" height="30" class="round mr-2" avatar="${student.firstName!} ${student.lastName!}"/>
                            <a href="<@ofbizUrl>edit_student</@ofbizUrl>?student_id=${student.studentId}">${student.firstName!} ${student.lastName!}</a></td>
                        <td>${student.email!}</td>
                        <td>${student.mobile!}</td>
                        <td>
                            <a href="<@ofbizUrl>edit_student</@ofbizUrl>?student_id=${student.studentId}" class="btn btn-outline-primary btn-sm">
                                <i class="fa fa-pencil" aria-hidden="true"></i>
                            </a>
                            <a href="" data-student-id="${student.studentId}" class="btn btn-outline-danger btn-sm" data-toggle="modal" data-target="#del_std_modal">
                                <i class="fa fa-trash-o" aria-hidden="true"></i>
                            </a>
                            <div class="modal fade" id="del_std_modal">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <div class="page-title">Delete Student</div>
                                            <button type="button" class="close" data-dismiss="modal">&times;</button>
                                        </div>
                                        <div class="modal-body">
                                            <center>
                                                <label>
                                                    Are you sure you want to delete this student ?
                                                </label>
                                            </center>
                                            <center>
                                                <button class="btn btn-danger" data-dismiss="modal" onclick="deleteStudent()">Yes</button>&nbsp;
                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">No</button>
                                            </center>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </td>
                      </tr>
                     </#list>
                </tbody>
                <caption>Total Students: ${students?size}</caption>
            </table>
            <form id="delete_student_form" action="<@ofbizUrl></@ofbizUrl>">
                <input type="hidden" id="studentExpenseId">
            </form>
        <#else>
            <div style="text-align: center" class="p-4">
                <img src="../../static/lottiefiles/629-empty-box.gif" width="300" class="center" />
                <div class="text-muted">No students found</div>
                <div>
                    <button data-toggle="modal"
                       data-target="#newStudentModal" class="btn btn-link">
                        <i class="fa fa-plus-circle" aria-hidden="true"></i> Add Now</button>
                </div>
            </div>
        </#if>
    </div>
</div>
