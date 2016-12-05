<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="p" tagdir="/WEB-INF/tags/partials" %>

<t:user-page>
    <div class="container">
        <div class="jumbotron">
            <div class="row">
                <p:requests-partial/>
                <p:pagination-partial uri="/user/requests"/>
                <c:if test="${empty requests}">
                    <p>No requests yet, but you can make one <a href="/user/request/new">here</a>.</p>
                </c:if>
                <p:error-partial/>
            </div>
        </div>
    </div>
</t:user-page>