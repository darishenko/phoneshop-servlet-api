<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" type="com.es.phoneshop.model.order.Order" required="true" %>

<table>
    <thead>
    <tr>
        <td>Image</td>
        <td>
            Description
        </td>
        <td class="quantity">
            Quantity
        </td>
        <td class="price">
            Price
        </td>
    </tr>
    </thead>
    <c:forEach var="item" items="${order.items}" varStatus="status">
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
            <td>
                <p class="quantity">${item.quantity}</p>
            </td>
            <td class="price">
                <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${item.product.id}"
                   style="text-decoration: none">
                    <fmt:formatNumber value="${item.product.price}" type="currency"
                                      currencySymbol="${item.product.currency.symbol}"/>
                </a>
            </td>
        </tr>
    </c:forEach>
</table>
<p></p>
<table>
    <tr>
        <td class="price" style="text-align: left;">Subtotal</td>
        <td>${order.subTotal}</td>
    </tr>
    <tr>
        <td class="price" style="text-align: left;">Delivery cost</td>
        <td>${order.deliveryCost}</td>
    </tr>
    <tr>
        <td class="price" style="text-align: left;">Total cost</td>
        <td>${order.totalCost}</td>
    </tr>
</table>