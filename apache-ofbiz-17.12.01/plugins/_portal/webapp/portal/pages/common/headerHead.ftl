<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="">
  <meta name="author" content="">

  <#if title??>
    <title>${title!} - Teachers Helper</title>
  <#else>
    <title>Teachers Helper</title>
  </#if>

  <link href="https://maxcdn.icons8.com/fonts/line-awesome/1.1/css/line-awesome-font-awesome.min.css" rel="stylesheet">
  <link href="../static/vendor/datepicker_2.2.3/datepicker.css" rel="stylesheet">

  <link href="../static/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <link href="../static/vendor/font-awesome.min.css" rel="stylesheet">
  <link href="../static/vendor/bootstrap/bs4 pop/bs4.pop.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

  <!-- Below added for calender-->
  <#--<link href="https://fonts.googleapis.com/css?family=Montserrat:300,400,500,600,700&display=swap" rel="stylesheet">-->

  <link href="../static/fonts/font-awesome.4.3.0.min.css" rel="stylesheet">
<#--  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">-->
  <link href="../static/fonts/open_sans_font.css" rel="stylesheet">
  <link href="../static/fonts/roboto_font.css" rel="stylesheet">

  <#--Calendar-->
  <link rel="stylesheet" href="../static/vendor/jquery/jquery.datetimepicker.min.css">

  <#--calendar student dropdown-->
  <link href="https://cdn.rawgit.com/harvesthq/chosen/gh-pages/chosen.min.css" rel="stylesheet"/>

  <#--timepicker-->
  <link rel="stylesheet" type="text/css" href="../static/vendor/timepicker/jquery.timepicker.css" />
  <link rel="stylesheet" href="../static/js/jquery.fulltable/jquery.fulltable.css"/>

  <!-- Custom styles for this template -->
  <link href="../static/css/cthportal.css?ns=${.now}" rel="stylesheet">
  <link href="../static/css/cthcalender.css" rel="stylesheet">
  <link rel="stylesheet" href="../static/css/login_reg.css">

  <#--dd-->
  <link rel="stylesheet" href="../static/vendor/dropdown_plugin/fastselect.min.css">
</head>
<body>
<div id="loader" class="centerLoader"></div>
  <div class="d-flex" id="wrapper">
