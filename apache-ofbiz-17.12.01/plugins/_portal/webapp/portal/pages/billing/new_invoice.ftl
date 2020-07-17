<div class="container-fluid p-2 new_invoice_container">
    <nav class="nav navbar cth_page_heading p-1">
        <span class="page-title">New Invoice </span>
        <a class="nav-link  btn btn-outline-secondary btn-sm" href="<@ofbizUrl>invoices</@ofbizUrl>" >
            <i class="fa fa-caret-left" aria-hidden="true"></i> Back</a>
    </nav>
</div>
<div class="container-fluid">
    <div class="row">
        <div class="col-8">
                <div class="row">
                    <div class="col-xs-12 col-md-12">
                        <div class="row">
                            <div class="col-xs-6 col-md-6">
                                <label for="stdForInvoice">Choose Student:</label><br>
                                <select class="custom-select" id="stdForInvoice" required>
                                    <#list students as student>
                                        <option value="${student.studentId!}">${student.firstName!} ${student.lastName!}</option>
                                    </#list>
                                </select>
                            </div>
                        <div class="col-xs-6 col-md-6 ">
                                <label for="invoice_date"> Due-Date:</label><br>
                                <input type='text' class="datepickerBs form-control" id="invoice_date" autocomplete="off" required>
                        </div>
                        </div><br/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12">
                        <div class="table-responsive tabclass">
                            <table class="table table-sm table-active">
                                <thead>
                                <tr class="">
                                    <th>Item</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Total</th>
                                    <th>Action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr class="item-row">
                                    <td class="item-name">
                                        <div class="delete-btn">
                                            <input type="text" class="form-control form-control-sm item" placeholder="Item" type="text">

                                        </div>
                                    </td>
                                    <td>
                                        <input class="form-control form-control-sm price" placeholder="Price" type="number">
                                    </td>
                                    <td>
                                        <input class="form-control form-control-sm qty" placeholder="Quantity" type="number" min="1">
                                    </td>
                                    <td>
                                        <span class="total">0.00</span>
                                    </td>
                                    <td>
                                       <#-- <a class="delete" href="javascript:;" title="Remove row">X</a>-->
                                        <i class="fa fa-trash-o text-danger" id="delete"></i>
                                    </td>
                                </tr>
                                <!-- Here should be the item row -->

                                </tbody>
                            </table>


                        </div>
                        <div class="row">
                            <div class="col"><a id="addRow" href="javascript:;" title="Add a row" class="btn btn-success btn-sm"><i class="fa fa-plus-square" aria-hidden="true"></i>
                                    Add row</a>
                            <strong class="float-right">Sub-total</strong>
                            </div>
                            <div class="col-2">
                                <span>
                                    <#if preferredCurrency.currencyType??>
                                        <#if preferredCurrency.currencyType == "INR">
                                            <i class="fa fa-inr" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "USD">
                                            <i class="fa fa-usd" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "EUR">
                                            <i class="fa fa-eur" aria-hidden="true"></i>
                                        </#if>
                                    <#else>
                                        <i class="fa fa-usd" aria-hidden="true"></i>
                                    </#if>

                                </span>
                                <strong class="" id="subtotal">0.00</strong>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col"><strong class="float-right">Discount</strong></div>
                            <div class="col-2"><input class="form-control form-control-sm float-right" id="discount" value="0" type="text"></div>
                        </div>
                        <div class="row">
                            <div class="col">
                                <strong class="float-right"><strong>Grand Total</strong></strong></div>
                            <div class="col-2">
                                <span>
                                    <#if preferredCurrency.currencyType??>
                                        <#if preferredCurrency.currencyType == "INR">
                                            <i class="fa fa-inr" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "USD">
                                            <i class="fa fa-usd" aria-hidden="true"></i>
                                        <#elseif preferredCurrency.currencyType == "EUR">
                                            <i class="fa fa-eur" aria-hidden="true"></i>
                                        </#if>
                                    <#else>
                                        <i class="fa fa-usd" aria-hidden="true"></i>
                                    </#if>
                                </span>
                                <span id="grandTotal">0</span></div>
                        </div><hr/>
                        <div class="row m-1">
                            <input class="btn btn-primary" type="button" value="Submit" id="getval">
                            <a class="btn btn-secondary ml-1" href="<@ofbizUrl>invoices</@ofbizUrl>">Cancel</a>
                        </div>
                    </div>
                </div>
        </div>
    </div>
</div>

