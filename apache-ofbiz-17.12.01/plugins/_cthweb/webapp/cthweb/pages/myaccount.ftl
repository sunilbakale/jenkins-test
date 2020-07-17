
<section id="tab-section1" class="padding-110px-tb bg-white builder-bg xs-padding-60px-tb">
    <div class="container">
        <div class="row">
            <!-- section title -->
            <div class="col-md-12 col-sm-12 col-xs-12 text-center">
                <h2 class="section-title-large sm-section-title-medium xs-section-title-large text-dark-gray font-weight-700 alt-font margin-three-bottom xs-margin-fifteen-bottom tz-text">MY ACCOUNT</h2>
                <div class="text-medium width-60 margin-lr-auto md-width-70 sm-width-100 tz-text margin-five-bottom xs-margin-nineteen-bottom">Manage Your Account, Change password, Manage You Academy, and more</div>
            </div>
            <!-- section title -->
            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 tab-style4">
                <div class="col-lg-3 col-md-4 col-sm-4 col-xs-12 no-padding-left xs-no-padding xs-margin-fifteen-bottom">
                    <!-- tab navigation -->
                    <ul class="nav nav-tabs bg-gray border-none tz-background-color">
                        <li class="active"><a data-toggle="tab" href="#tab_sec1" class="sm-display-inline-block"><i class="fa fa-file-text-o icon-small float-left width-20 text-center tz-icon-color"></i><span class="tz-text">ACADEMIES</span></a></li>
                        <li><a data-toggle="tab" href="#tab_sec2" class="sm-display-inline-block"><i class="fa ti-user icon-small float-left width-20 text-center tz-icon-color"></i><span class="tz-text">MY ACCOUNT</span></a></li>
                        <li><a data-toggle="tab" href="#tab_sec3" class="sm-display-inline-block"><i class="fa ti-key icon-small float-left width-20 text-center tz-icon-color"></i><span class="tz-text">PASSWORD</span></a></li>
                        <li><a data-toggle="tab" href="#tab_sec4" class="sm-display-inline-block"><i class="fa ti-fax icon-small float-left width-20 text-center tz-icon-color"></i><span class="tz-text">REFER FRIENDS</span></a></li>
                        <li><a data-toggle="tab" href="#tab_sec5" class="sm-display-inline-block"><i class="fa ti-sign-out icon-small float-left width-20 text-center tz-icon-color"></i><span class="tz-text">LOGOUT</span></a></li>
                    </ul>
                    <!-- tab end navigation -->
                </div>
                <!-- tab content section -->
                <div class="col-lg-9 col-md-8 col-sm-8 col-xs-12 padding-nine-left xs-no-padding">
                    <div class="tab-content">
                        <!-- tab content -->
                        <div id="tab_sec1" class="tab-pane fade in active">
                            <div class="row">
                                <div class="col-md-11 col-sm-12 col-xs-12">

                                <#include "academies.ftl" />

                                </div>
                            </div>
                        </div>
                        <!-- end tab content -->
                        <!-- tab content -->
                        <div id="tab_sec2" class="tab-pane fade in">
                            <div class="row">
                                <div class="col-md-11 col-sm-12 col-xs-12">
                                    Manage My account
                                </div>
                            </div>
                        </div>
                        <!-- end tab content -->
                        <!-- tab content -->
                        <div id="tab_sec3" class="tab-pane fade in">
                            <div class="row">
                                <div class="col-md-11 col-sm-12 col-xs-12">
                                    change password form
                                </div>
                            </div>
                        </div>
                        <!-- end tab content -->
                        <!-- tab content -->
                        <div id="tab_sec4" class="tab-pane fade in">
                            <div class="row">
                                <div class="col-md-11 col-sm-12 col-xs-12">
                                    Refer friends form
                                </div>
                            </div>
                        </div>
                        <!-- end tab content -->
                        <!-- tab content -->
                        <div id="tab_sec5" class="tab-pane fade in">
                            <div class="row">
                                <div class="col-md-11 col-sm-12 col-xs-12">
                                    Logout?
                                </div>
                            </div>
                        </div>
                        <!-- end tab content -->
                    </div>
                </div>
                <!-- end tab content section -->
            </div>
        </div>
    </div>
</section>
        