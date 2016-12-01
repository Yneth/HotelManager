<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:site-page>
    <c:if test="${empty sessionScope.user}">
        <t:login-partial/>
    </c:if>
    <c:if test="${not empty sessionScope.user}">
        <div class="container">
            <div class="jumbotron">
                <c:if test="${sessionScope.user.authority.name == 'ADMIN'}">
                    <h1>Hi, admin.</h1>
                    <p>It is your panel to check and approve customer orders.</p>
                    <p><a class="btn btn-primary" href="/admin/requests" role="button">View created requests »</a></p>
                    <p><a class="btn btn-primary" href="/admin/apartments" role="button">View apartments »</a></p>
                </c:if>
                <c:if test="${sessionScope.user.authority.name == 'USER'}">
                    <h1>Hi, ${sessionScope.user.login}.</h1>
                    <p>Here you can make requests for apartment and check already requested ones.</p>
                    <p><a class="btn btn-primary" href="/user/requests" role="button">View requests »</a></p>
                    <p><a class="btn btn-primary" href="/user/requests/new" role="button">Make a new request »</a></p>
                </c:if>
            </div>
        </div>
    </c:if>
</t:site-page>