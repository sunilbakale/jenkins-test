<div class="container-fluid">
    <div class="row">
        <div class="page-title mb-2">
            Preferences
        </div>
    </div>
    <div class="row">
        <div class="col-4 p-0">
            <label for="preferredCurrency">Choose currency type</label>
            <div class="row">
                <div class="col-8 pr-0">
                    <select class="form-control" id="preferredCurrency">
                        <option value="INR"
                                <#if preferredCurrency.currencyType??>
                            <#if preferredCurrency.currencyType == "INR" >
                                selected
                            </#if>
                                </#if>>
                            INR
                        </option>
                        <option value="USD"
                                <#if preferredCurrency.currencyType??>
                                    <#if preferredCurrency.currencyType == "USD" >
                                        selected
                                    </#if></#if>
                                <#if preferredCurrency.currencyType!?length == 0>
                        selected
                                </#if>>
                            USD
                        </option>
                        <option value="EUR"
                                <#if preferredCurrency.currencyType??>
                            <#if preferredCurrency.currencyType == "EUR" >
                                selected
                            </#if></#if>>
                            EUR
                        </option>
                    </select>
                </div>
                <div class="col-4 p-0 pl-1">
                    <button class="btn btn-primary" id="preferenceSaveBtn">Save</button>
                </div>
            </div>
        </div>
    </div>
</div>


