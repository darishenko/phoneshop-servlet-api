<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" class="com.es.phoneshop.model.cart.Cart" scope="request"/>
<a href="${pageContext.servletContext.contextPath}/cart" style="float: right;">
    Cart: ${cart.totalQuantity} item<c:if test="${cart.totalQuantity != 1}">s</c:if>
</a>


