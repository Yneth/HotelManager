<%@ tag description="Base page for admin" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="isAdmin" value="${not empty sessionScope.user && sessionScope.user.authority.name == 'ADMIN'}" />
<t:site-page>
    <c:if test="${not empty sessionScope.user && sessionScope.user.authority.name == 'ADMIN'}">
        <jsp:body>
            <jsp:doBody/>
        </jsp:body>
    </c:if>
</t:site-page>