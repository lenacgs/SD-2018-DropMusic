<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/15/2018
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Manage Requests</title>
</head>
<body>
    <p>DropMusic</p>
    <p>Group application possibilities:</p>
    <s:iterator var="request" value="#session.requests">
        <s:form action="acceptRequest" method="post">
            <s:hidden name="request" value="%{request}" />
            <s:submit value="Accept %{request}"/>
        </s:form>
        <s:form action="declineRequest" method="post">
            <s:hidden name="request" value="%{request}"/>
            <s:submit value="Decline %{request}"/>
        </s:form>
        <p>---------------- / / ----------------</p>
    </s:iterator>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
