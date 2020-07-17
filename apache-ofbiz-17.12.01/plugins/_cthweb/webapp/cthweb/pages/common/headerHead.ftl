
<!doctype html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1" />
    <!-- title -->
    <title>
    <#if title?has_content>
    ${title} - Teachers Helper
    <#else>
        Teachers Helper
    </#if>
    </title>
    <meta name="description" content="lgdescription" />
    <meta name="keywords" content="lgkeywords" />
    <meta name="author" content="lgauthor">
    <!-- favicon -->
    <link rel="shortcut icon" href="../static/images/icon/favicon.ico">
    <!-- animation -->
    <link rel="stylesheet" href="../static/css/animate.css" />
    <!-- bootstrap -->
    <link rel="stylesheet" href="../static/css/bootstrap.min.css" />
    <!-- font-awesome icon -->
    <link rel="stylesheet" href="../static/css/font-awesome.min.css" />
    <!-- themify-icons -->
    <link rel="stylesheet" href="../static/css/themify-icons.css" />
    <!-- owl carousel -->
    <link rel="stylesheet" href="../static/css/owl.transitions.css" />
    <link rel="stylesheet" href="../static/css/owl.carousel.css" />
    <!-- magnific popup -->
    <link rel="stylesheet" href="../static/css/magnific-popup.css" />
    <!-- base -->
    <link rel="stylesheet" href="../static/css/base.css" />
    <!-- elements -->
    <link rel="stylesheet" href="../static/css/elements.css" />
    <!-- responsive -->
    <link rel="stylesheet" href="../static/css/responsive.css" />
    <!--[if IE 9]>
    <link rel="stylesheet" type="text/css" href="../static/css/ie.css" />
    <![endif]-->
    <!--[if IE]>
    <script src="../static/js/html5shiv.min.js"></script>
    <![endif]-->
</head>
<body>

<div id="page" class="page"><!-- footer section -->


<header class="header-style5" id="header-section11">
    <!-- nav -->
    <nav class="navbar bg-white tz-header-bg no-margin alt-font shrink-header light-header">
        <div class="container navigation-menu">
            <div class="row">
                <!-- logo -->
                <div class="col-md-3 col-sm-4 col-xs-6">
                    <a href="<@ofbizUrl>home</@ofbizUrl>" class="inner-link"><img alt="" src="../static/images/logo.png" data-img-size="(W)163px X (H)39px"></a>
                </div>
                <!-- logo -->
                <div class="col-md-9 col-sm-8 col-xs-6 position-inherit xs-no-padding-left">
                    <button data-target="#bs-example-navbar-collapse-1" data-toggle="collapse" class="navbar-toggle collapsed" type="button">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <!-- social elements -->
                    <#--<div class="social float-right pull-right">
                        <a href="#"><i class="fa fa-facebook tz-icon-color"></i></a>
                        <a href="#"><i class="fa fa-twitter tz-icon-color"></i></a>
                        <a href="#"><i class="fa fa-linkedin tz-icon-color"></i></a>
                        <a href="#"><i class="fa fa-google-plus tz-icon-color"></i></a>
                    </div>-->
                    <!-- end social elements -->
                    <div id="bs-example-navbar-collapse-1" class="collapse navbar-collapse pull-right">
                        <ul class="nav navbar-nav font-weight-500">
                            <li class="propClone"><a class="inner-link" href="#">SECTION 1</a></li>
                            <li class="propClone"><a class="inner-link" href="#">SECTION 2</a></li>
                            <li class="propClone"><a class="inner-link" href="#">SECTION 3</a></li>
                            <li class="propClone"><a class="inner-link" href="#">SECTION 4</a></li>
                            <li class="propClone"><a class="inner-link" href="#">SECTION 5</a></li>

                            <#if userLogin??>
                            <li class="propClone">
                                John Teacher
                                <li class="nav-button propClone float-left btn-medium sm-no-margin-tb">
                                    <a href="<@ofbizUrl>myaccount</@ofbizUrl>" class="inner-link btn-small propClone bg-dark-gray text-white border-radius-0 sm-display-inline-block font-weight-400 sm-padding-nav-btn">
                                        <i class="fa fa-user tz-icon-color" aria-hidden="true"></i>
                                        <span class="tz-text">MY ACCOUNT</span></a>
                                </li>
                                <li class="propClone"><a class="inner-link" href="<@ofbizUrl>logout</@ofbizUrl>"><i class="fa fa-power-off"></i></a></li>
                            </li>
                            <#else>
                                <li class="nav-button propClone float-left btn-medium sm-no-margin-tb">
                                <a href="<@ofbizUrl>login</@ofbizUrl>" class="inner-link btn-small propClone bg-dark-gray text-white border-radius-0 sm-display-inline-block font-weight-400 sm-padding-nav-btn"><i class="fa fa-user tz-icon-color" aria-hidden="true"></i>
                                    <span class="tz-text">LOGIN</span></a>
                                </li>
                            </#if>

                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </nav>
    <!-- end nav -->
</header>

