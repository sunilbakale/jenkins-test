<!-- Sidebar -->
<div class="border-right " id="sidebar-wrapper">
  <div class="sidebar-heading"><a href="<@ofbizUrl>home</@ofbizUrl>"><i>Teachers Helper</i></a></div>

  <div class="list-group list-group-flush">
    <a href="<@ofbizUrl>home</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='HOME'>active-item</#if>">
    <i class="fa fa-tachometer" aria-hidden="true"></i> Dashboard</a>

    <a href="<@ofbizUrl>students</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='STUDENTS'>active-item</#if>
    <#if currentViewId=='NEW STUDENT'>active</#if><#if currentViewId=='EDIT STUDENTS'>active</#if>">
    <i class="fa fa-users" aria-hidden="true"></i> Students
    </a>
    <a href="<@ofbizUrl>calender</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='CALENDAR'>active-item</#if>">
    <i class="fa fa-calendar" aria-hidden="true"></i> Calendar</a>

    <a href="<@ofbizUrl>invoices</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='INVOICES'>active-item</#if>
    <#if currentViewId=='NEW INVOICE'>active</#if> <#if currentViewId=='EDIT INVOICE'>active</#if>">
    <i class="fa fa-credit-card" aria-hidden="true"></i> Billing</a>

    <a href="<@ofbizUrl>notes</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='NOTES'>active-item</#if>">
    <i class="fa fa-book" aria-hidden="true"></i> Notes</a>

    <a href="<@ofbizUrl>files</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=="FILES">active-item</#if>">
      <i class="fa fa-files-o" aria-hidden="true"></i>
      Files
    </a>
    <a href="<@ofbizUrl>support</@ofbizUrl>" class="list-group-item list-group-item-action <#if currentViewId=='SUPPORT'>active-item</#if>">
    <i class="fa fa-question-circle" aria-hidden="true"></i> Support</a>
  </div>
</div>
<!-- /#sidebar-wrapper -->