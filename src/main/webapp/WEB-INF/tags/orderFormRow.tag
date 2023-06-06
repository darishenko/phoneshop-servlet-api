<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>
<%@ attribute name="order" type="com.es.phoneshop.model.order.Order" required="true" %>

<tr>
    <td>${label}
        <span style="color: red">*</span></td>
    <td>
    <c:set var="error" value="${errors[name]}"/>
    <input value="${not empty error ? param[name] : order[name]}" name="${name}"/>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>
    </td>
</tr>

