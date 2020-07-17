<div class="container-fluid p-0">
  <div class="container-fluid">
      <br>
    <!-- Nav tabs -->
    <ul class="nav nav-tabs" role="tablist">
      <li class="nav-item">
        <a class="nav-link active" data-toggle="tab" href="#overview_tab">Overview</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#invoices_tab" id="navInvoiceBtn">Invoices</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#expenses" id="navExpenseBtn">Expenses</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#transactions" id="navTxBtn">Transactions Log</a>
      </li>
    </ul>
    <!-- Tab panes -->
    <div class="tab-content">
      <div id="overview_tab" class="tab-pane active cth-tab-container">
        ${screens.render("component://portal/widget/PortalScreens.xml#overView_tab")}
      </div>
      <div id="invoices_tab" class="tab-pane cth-tab-container">
        ${screens.render("component://portal/widget/PortalScreens.xml#invoices_tab")}
      </div>
      <div id="expenses" class="tab-pane cth-tab-container">
        <#include 'expenses_tab.ftl'>
      </div>
      <div id="transactions" class="tab-pane cth-tab-container">
        ${screens.render("component://portal/widget/PortalScreens.xml#transactions")}
      </div>
      <br/>
    </div>

  </div>


</div>
