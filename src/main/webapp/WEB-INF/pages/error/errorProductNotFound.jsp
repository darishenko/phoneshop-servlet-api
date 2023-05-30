<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product not found">
    <tags:error errorMessage="Product with code ${pageContext.errorData.throwable.itemId} not found."></tags:error>
</tags:master>