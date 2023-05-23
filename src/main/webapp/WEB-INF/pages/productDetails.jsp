<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product Details">
    <p style="font-weight: bold">
            ${product.description}
    </p>
    <c:if test="${not empty param.message and empty addToCartError}">
        <div class="success">
                ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty addToCartError}">
        <div class="error">
            There was an error adding to cart!
        </div>
    </c:if>
    <p>
            ${cart}
    </p>
    <form method="post">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    <img src="${product.imageUrl}">
                </td>
            </tr>
            <tr>
                <td>Code</td>
                <td>
                        ${product.code}
                </td>
            </tr>
            <tr>
                <td>Stock</td>
                <td>
                        ${product.stock}
                </td>
            </tr>
            <tr>
                <td>Prise</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
            <tr>
                <td>Quantity</td>
                <td>
                    <input class="quantity" name="quantity" value="${not empty addToCartError ? param.quantity : 1}">
                    <c:if test="${not empty addToCartError}">
                        <div class="error">
                                ${addToCartError}
                        </div>
                    </c:if>
                </td>
            </tr>
        </table>
        <p>
            <button>Add to cart</button>
        </p>
    </form>

    <p></p>
    <div>
        <tags:recentProducts recentProducts="${recentProducts}"/>
    </div>

</tags:master>