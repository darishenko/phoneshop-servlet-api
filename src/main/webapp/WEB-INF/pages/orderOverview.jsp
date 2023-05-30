<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" class="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order">
    <p>
        Order Overview
    </p>

    <tags:cartTable order="${order}"></tags:cartTable>

    <h3>Your details</h3>
    <table
    <tags:orderOverviewRow name="firstName" label="First Name" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="lastName" label="Last Name" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="phone" label="Phone" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="deliveryAddress" label="Delivery address" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="formattedDeliveryDate" label="Delivery date" order="${order}"></tags:orderOverviewRow>
    <tags:orderOverviewRow name="paymentMethod" label="Payment method" order="${order}"></tags:orderOverviewRow>
    </table>

</tags:master>

