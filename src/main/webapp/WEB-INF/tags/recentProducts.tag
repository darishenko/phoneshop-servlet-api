<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="recentProducts" type="java.util.Deque<com.es.phoneshop.model.product.Product>" required="true" %>

<c:if test="${not empty recentProducts}">
    3 recently viewed products
</c:if>
<table>
    <tr>
        <c:forEach var="product" items="${recentProducts}">
            <td>
                <img class="product-tile" src="${product.imageUrl}">
                <p>
                    <a href="${pageContext.request.contextPath}/products/${product.id}"
                       style="text-decoration: none">
                        ${product.description}
                    </a>
                </p>
                <p>
                    <a href="${pageContext.request.contextPath}/products/priceHistory/${product.id}"
                       style="text-decoration: none">
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                    </a>
                </p>
            </td>
        </c:forEach>
    </tr>
</table>