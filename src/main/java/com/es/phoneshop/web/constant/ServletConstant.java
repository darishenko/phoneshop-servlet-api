package com.es.phoneshop.web.constant;

public interface ServletConstant {
    interface JspPage {
        String CART = "/WEB-INF/pages/cart.jsp";
        String CHECKOUT = "/WEB-INF/pages/checkout.jsp";
        String MINI_CART = "/WEB-INF/pages/minicart.jsp";
        String ORDER_OVERVIEW = "/WEB-INF/pages/orderOverview.jsp";
        String PRODUCT_DETAILS = "/WEB-INF/pages/productDetails.jsp";
        String PRODUCT_LIST = "/WEB-INF/pages/productList.jsp";
        String PRODUCT_PRICE_HISTORY = "/WEB-INF/pages/productPriceHistory.jsp";
        String ADVANCED_SEARCH = "/WEB-INF/pages/advancedSearch.jsp";

    }

    interface RequestAttribute {
        String QUANTITY = "quantity";
        String CART = "cart";
        String ADD_TO_CART_ERROR = "addToCartError";
        String UPDATE_CART_ERRORS = "updateCartErrors";
        String PRICE_ERROR = "priceError";
        String PRICE_ERRORS = "priceErrors";
        String PRODUCT_ID = "productId";
        String PRODUCT = "product";
        String PRODUCTS = "products";
        String RECENT_PRODUCTS = "recentProducts";
        String ORDER = "order";
        String PLACE_ORDER_ERRORS = "orderErrors";
        String ACCEPTANCE_CRITERIA = "acceptanceCriteria";
    }

    interface RequestParameter {
        String QUERY = "query";
        String SORT = "sort";
        String ORDER = "order";
        String PRODUCT_ID = "productId";
        String QUANTITY = "quantity";
        String ACCEPTANCE_CRITERIA = "acceptanceCriteria";
        String MIN_PRICE = "minPrice";
        String MAX_PRICE = "maxPrice";


        interface Order {
            String LAST_NAME = "lastName";
            String FIRST_NAME = "firstName";
            String PHONE = "phone";
            String ADDRESS = "deliveryAddress";
            String PAYMENT_METHOD = "paymentMethod";
            String DELIVERY_DATE = "deliveryDate";
        }
    }

    interface Message {
        String REQUIRED_VALUE = "Value is required";

        interface Error {
            String NOT_A_NUMBER = "Not a number!";
            String OUT_OF_STOCK = "Not enough items in stock!";
            String PAYMENT_METHOD = "Invalid payment method";
            String DELIVERY_DATE = "Invalid delivery date";
            String MIN_LESS_MAX_PRICE = "Min price should be less than max price";
        }

        interface Success {
            String UPDATE_CART = "Cart updated successfully";
            String REMOVE_CART_ITEM = "Cart item removed successfully";
            String ADD_PRODUCT_TO_CART = "Product was added to cart";
        }
    }

}
