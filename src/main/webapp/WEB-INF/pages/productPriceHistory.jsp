<%@ page contentType="text/html;charset=UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product price history">
    <p style="font-weight: bold">
        ${product.description}
    </p>
    <table>
        <thead>
        <tr>
            <td>Start date</td>
            <td>Price</td>
        </tr>
        </thead>
        <c:forEach var="priceHistory" items="${product.priceHistory}">
            <tr>
                <td>
                    ${priceHistory.formattedStartDate}
                </td>
                <td>
                    <fmt:formatNumber value="${priceHistory.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
    </table>

    <p></p>
    <div>
        <tags:recentProducts recentProducts="${recentProducts}"/>
    </div>

</tags:master>