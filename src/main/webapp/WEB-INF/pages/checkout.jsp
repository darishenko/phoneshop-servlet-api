<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" class="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order">
    <p>
        Order
    </p>
    
    <tags:cartTable order="${order}"></tags:cartTable>

    <c:if test="${not empty param.message }">
        <p class="success">${param.message}</p>
    </c:if>
    <h3>Your details</h3>
    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
        <table
        <tags:orderFormRow name="firstName" label="First Name" errors="${orderErrors}"
                           order="${order}"></tags:orderFormRow>
        <tags:orderFormRow name="lastName" label="Last Name" errors="${orderErrors}"
                           order="${order}"></tags:orderFormRow>
        <tags:orderFormRow name="phone" label="Phone" errors="${orderErrors}" order="${order}"></tags:orderFormRow>
        <tags:orderFormRow name="deliveryAddress" label="Delivery address" errors="${orderErrors}"
                           order="${order}"></tags:orderFormRow>
        <tags:orderFormRow name="deliveryDate" label="Delivery date" errors="${orderErrors}"
                           order="${order}"></tags:orderFormRow>
        <td>Payment method</td>
        <td>
            <select name="paymentMethod">
                <c:forEach var="method" items="${paymentMethod}">
                    <option value="${method}" ${param.paymentMethod == method ? 'selected' : ''}>${method}</option>
                </c:forEach>
            </select>
        </td>
        </table>
        <p>
            <c:if test="${not empty order.items}">
                <button type="submit">Place order</button>
            </c:if>
        </p>

    </form>
</tags:master>

