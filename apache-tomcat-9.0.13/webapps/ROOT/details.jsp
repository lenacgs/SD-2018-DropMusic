<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/16/2018
  Time: 3:26 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Details</title>
</head>
<body>
    <p>DropMusic</p><br>
    <s:text name="%{#session.object} details:"/><br>
    <s:text name="%{#session.details}"/>
    <s:form action="search"><s:submit value="BACK"/></s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
