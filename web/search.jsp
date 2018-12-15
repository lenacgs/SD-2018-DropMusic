<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>DropMusic</title>
</head>
<body>
<div title = "header">
    <s:form action="search" method="get">
    <select name="inputObject.option">
        <option value = "music" selected>Music</option>
        <option value = "artist">Artist</option>
        <option value = "album">Album</option>
    </select>
    <s:textfield name="inputObject.keyword" />
    <s:submit value="SEARCH"/>
    </s:form>
</div>

<div id="main">
    <c:choose>
        <c:when test="${results == null}">
        </c:when>
        <c:when test="${results.isEmpty()}">
            No results found!
        </c:when>
        <c:otherwise>
            Found ${results.size()} products!
            <br />
            <c:forEach items="${results}" var="item">
                <div>
                    Name:<c:out value="${item.name}" /> <br />
                    <%--<c:when test="${inputObject.option == 'music' || inputObject.option == 'album'}">--%>
                        <%--Artist:<c:out value="${item.artist}" />--%>
                    <%--</c:when>--%>
                </div>
                <br />
            </c:forEach>
        </c:otherwise>
    </c:choose>

</div>

</body>>
<div></div>
</body>
</html>