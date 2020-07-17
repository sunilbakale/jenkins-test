<div class="container-fluid">
    <div class="nav navbar cth_page_heading bg-light">
        <div class="page-title">My Account</div>
        <a class="nav-link  btn btn-outline-secondary" href="<@ofbizUrl>home</@ofbizUrl>">
            <i class="fa fa-home" aria-hidden="true"></i></a>
    </div>
    <div class="container-fluid mt-2">
        <div class="row">
            <div class="col-3 p-0 border-right">
                <ul class="nav nav-pills flex-column" role="tablist" style="background-color: #e9ecef;min-height: 100vh;">
                    <li class="nav-item ">
                        <a class="nav-link active border-top rounded-0 text-dark" data-toggle="pill" href="#generalInfo">
                            <div class="row">
                                <div class="col-1">
                                    <i class="fa fa-user-o" aria-hidden="true"></i>
                                </div>
                                <div class="col p-0 pl-1">
                                    General Information<br/>
                                    <span class="text-muted small">Name,email,phone</span>
                                </div>
                            </div>
                        </a>
                    </li>
                    <li class="nav-item ">
                        <a class="nav-link rounded-0 border-top text-dark" data-toggle="pill" href="#preference">
                            <div class="row">
                                <div class="col-1">
                                    <i class="fa fa-certificate" aria-hidden="true"></i>
                                </div>
                                <div class="col p-0 pl-1">
                                    Preferences<br/>
                                    <span class="text-muted small">Change currency type</span>
                                </div>
                            </div>
                        </a>
                    </li>
                    <li class="nav-item ">
                        <a class="nav-link rounded-0 text-dark border-top" data-toggle="pill" href="#userBilling">
                            <div class="row">
                                <div class="col-1">
                                    <i class="fa fa-calculator" aria-hidden="true"></i>
                                </div>
                                <div class="col p-0 pl-1">
                                    Billing<br/>
                                    <span class="text-muted small">Manage your subscription</span>
                                </div>
                            </div>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link rounded-0 text-dark border-top border-bottom" data-toggle="pill" href="#security">
                            <div class="row">
                                <div class="col-1">
                                    <i class="fa fa-shield" aria-hidden="true"></i>
                                </div>
                                <div class="col p-0 pl-1">
                                    Security<br/>
                                    <span class="text-muted small">Password,secure account</span>
                                </div>
                            </div>

                        </a>
                    </li>
                </ul>
            </div>
            <div class="col p-0">
                <div class="tab-content">
                    <div id="generalInfo" class="container tab-pane active">
                        ${screens.render("component://portal/widget/PortalScreens.xml#generalInfo")}
                    </div>
                    <div id="preference" class="container tab-pane fade">
                        ${screens.render("component://portal/widget/PortalScreens.xml#preference")}
                    </div>
                    <div id="userBilling" class="container tab-pane fade">
                        ${screens.render("component://portal/widget/PortalScreens.xml#userBilling")}
                    </div>
                    <div id="security" class="container tab-pane fade">
                        ${screens.render("component://portal/widget/PortalScreens.xml#security")}
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
