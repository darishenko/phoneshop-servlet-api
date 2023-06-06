<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="order" type="com.es.phoneshop.model.order.Order" required="true" %>

<tr>
    <td><p style="color: green">${label}</p></td>
    <td>${order[name]}</td>
</tr>

