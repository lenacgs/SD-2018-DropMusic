<%--
  Created by IntelliJ IDEA.
  User: MADALENA
  Date: 20/12/2018
  Time: 01:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>
<head>
    <title>MY FILES</title>
</head>
<body>
    <h3>These are the files that have been shared with you</h3><br />

    <div>
    <c:forEach items="${results}" var="music">
        <c:out value="${music[0]}"/> by <c:out value="${music[1]}"/> shared by <c:out value="${music[3]}"/>
            <audio controls>
                <source src="${music[2]}">
            </audio>
        <br />
    </c:forEach>
    </div>



</body>
</html>
