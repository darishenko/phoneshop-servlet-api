<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Advanced Search">
    <p>
        Advanced Search
    </p>

    <form style="margin-inside: 5vh">
        <p>Description
            <input name="query" value="${param.query}">
            <select name="acceptanceCriteria">
                <c:forEach var="criteria" items="${acceptanceCriteria}">
                    <option value="${criteria}" ${param.acceptanceCriteria == criteria ? 'selected' : ''}>${criteria}</option>
                </c:forEach>
            </select>
        </p>
        <c:set var="error" value="${priceErrors['priceError']}"/>
        <c:if test="${not empty error}">
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>
        </c:if>
        <p>Min Price
                <c:set var="error" value="${priceErrors['minPrice']}"/>
            <input value="${param['minPrice']}" name="minPrice"/>
            <c:if test="${not empty error}">
        <div class="error">${error}</div>
        </c:if>
        </p>
        <p>Max Price
                <c:set var="error" value="${priceErrors['maxPrice']}"/>
            <input value="${param['maxPrice']}" name="maxPrice"/>
            <c:if test="${not empty error}">
        <div class="error">${error}</div>
        </c:if>
        </p>
        <button type="submit">Search</button>
    </form>

    <c:if test="${not empty products}">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="price">Price</td>
            </tr>
            </thead>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>
                        <img class="product-tile" src="${product.imageUrl}">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}"
                           style="text-decoration: none">
                                ${product.description}
                        </a>
                    </td>
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${product.id}"
                           style="text-decoration: none">
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

</tags:master>
