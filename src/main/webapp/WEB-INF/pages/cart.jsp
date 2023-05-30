<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" class="com.es.phoneshop.model.cart.Cart" scope="session"/>
<tags:master pageTitle="Cart">
    <p>
        Cart
    </p>
    <c:choose>
        <c:when test="${not empty updateCartErrors}">
            <p class="error">There were some problems updating the cart!</p>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty param.message}">
                <p class="success">${param.message}</p>
            </c:if>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty cart.items}">
    <form method="post" action="${pageContext.servletContext.contextPath}/cart">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>
                    Description
                </td>
                <td class="price">
                    Price
                </td>
                <td class="quantity">
                    Quantity
                </td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}" varStatus="status">
                <tr>
                    <td>
                        <img class="product-tile"
                             src="${item.product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                                ${item.product.description}
                        </a>
                    </td>
                    <td class="price">
                        <a href="products/priceHistory/${item.product.id}" style="text-decoration: none">
                            <fmt:formatNumber value="${item.product.price}" type="currency"
                                              currencySymbol="${item.product.currency.symbol}"/>
                        </a>
                    </td>
                    <td class="quantity">
                        <c:set var="updateCartError" value="${updateCartErrors[item.product.id]}"/>
                        <input name="quantity" class="quantity"
                               value="${not empty updateCartError ? paramValues['quantity'][status.index] : item.quantity}"/>
                        <input type="hidden" name="productId" value="${item.product.id}"/>
                        <c:if test="${not empty updateCartError}">
                            <div class="error">${updateCartError}</div>
                        </c:if>
                    </td>

                    <td>
                        <input form="deleteCartItem" type="submit" value="Delete"
                               formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                    </td>
                </tr>
            </c:forEach>
        </table>
        <p></p>
        <table>
            <tr>
                <td class="price" style="text-align: left;">Total price</td>
                <td>${cart.totalCost}</td>
                </td>
            </tr>
            <tr>
                <td class="quantity" style="text-align: left;">Total quantity</td>
                <td>${cart.totalQuantity}</td>
                </td>
            </tr>
        </table>
        <p></p>
        <div class="button-row">
            <c:if test="${not empty cart.items}">
                <input type="submit" value="Update">
            </c:if>
        </div>
    </form>
    </c:if>
    <form id="deleteCartItem" method="post"
          action="${pageContext.servletContext.contextPath}/cart/deleteItem/${item.product.id}">
    </form>
    <c:if test="${not empty cart.items}">
    <form action="${pageContext.servletContext.contextPath}/checkout" method="get">
        <button type="submit">Checkout</button>
    </form>
    </c:if>
    <p>
        <tags:recentProducts recentProducts="${recentProducts}"/>
    </p>
</tags:master>
