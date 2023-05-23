<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
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
    <p></p>
    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
                <tags:sortLink sort="description" order="asc"></tags:sortLink>
                <tags:sortLink sort="description" order="desc"></tags:sortLink>
            </td>
            <td>Quantity</td>
            <td class="price">
                Price
                <tags:sortLink sort="price" order="asc"></tags:sortLink>
                <tags:sortLink sort="price" order="desc"></tags:sortLink>
            </td>
            <td></td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <form method="post">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}">
                    </td>
                    <td>
                        <a href="products/${product.id}"
                           style="text-decoration: none">
                            ${product.description}
                        </a>
                    </td>
                    <td class="quantity">
                        <c:if test="${param.productId == product.id}">
                            <input name="quantity" class="quantity" value="${param.quantity}"/>
                            <c:if test="${not empty addToCartError}">
                                <div class="error">${addToCartError}</div>
                            </c:if>
                        </c:if>
                        <c:if test="${param.productId != product.id}">
                            <input name="quantity" class="quantity" value="1"/>
                        </c:if>
                    </td>

                    <td class="price">
                        <a href="products/priceHistory/${product.id}" style="text-decoration: none">
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                    <td>
                        <input type="submit" value="Add to cart">
                        <input type="hidden" name="productId" value="${product.id}"/>
                        <c:if test="${not empty param.query}">
                            <input type="hidden" name="query" value="${param.query}">
                        </c:if>
                        <c:if test="${not empty param.order}">
                            <input type="hidden" name="order" value="${param.order}">
                        </c:if>
                    </td>
                </tr>
            </form>
        </c:forEach>
    </table>

    <p></p>
    <div>
        <tags:recentProducts recentProducts="${recentProducts}"/>
    </div>

</tags:master>