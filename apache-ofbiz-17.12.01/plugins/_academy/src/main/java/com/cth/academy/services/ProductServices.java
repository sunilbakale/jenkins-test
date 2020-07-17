package com.cth.academy.services;

import com.cth.academy.model.ProductPriceVO;
import com.cth.academy.model.ProductVO;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductServices {

    public final static String module = ProductServices.class.getName();

    public static Map<String, Object> getAvailableProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        List<ProductVO> productList = new ArrayList<>();
        List<String> productIdList = new ArrayList<>();
        productIdList.add("FREE");
        productIdList.add("PAID_BASIC");
        productIdList.add("PAID_PREMIUM");

        try {
            EntityCondition productCondition = EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList);
            List<GenericValue> productGVList = delegator.findList("Product", productCondition, null, null, null, false);
            for (GenericValue productGV : productGVList) {
                String productId = productGV.getString("productId");
                //set product object
                ProductVO productVO = new ProductVO();
                productVO.setProductId(productId);
                productVO.setProductName(productGV.getString("productName"));
                productVO.setProductTypeId(productGV.getString("productTypeId"));
                productVO.setLongDescription(productGV.getString("longDescription"));
                //set product price object
                GenericValue productPrice = EntityQuery.use(delegator).from("ProductPrice").where("productId", productId,
                        "productPricePurposeId", "USAGE_CHARGE", "productPriceTypeId", "DEFAULT_PRICE").queryFirst();
                ProductPriceVO productPriceVO = new ProductPriceVO();
                productPriceVO.setProductId(productId);
                productPriceVO.setCurrencyUomId(productPrice.getString("currencyUomId"));
                productPriceVO.setFromDate(productPrice.getString("fromDate"));
                productPriceVO.setPrice(productPrice.getString("price"));
                productPriceVO.setProductPricePurposeId(productPrice.getString("productPricePurposeId"));
                productPriceVO.setProductPriceTypeId(productPrice.getString("productPriceTypeId"));
                productPriceVO.setProductStoreGroupId(productPrice.getString("productStoreGroupId"));
                productVO.setProductPrice(productPriceVO);
                productList.add(productVO);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnFailure("Unable to fetch product details, error: " + e.getMessage());
        }
        Map<String, Object> sendResp = ServiceUtil.returnSuccess();
        sendResp.put("productList", productList);
        return sendResp;
    }


}
