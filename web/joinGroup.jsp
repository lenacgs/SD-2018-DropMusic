<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/14/2018
  Time: 11:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Join Group</title>
</head>
<body>
    <p>DropMusic</p>
    <p>Group application possibilities:</p>
    <s:iterator var="group" value="#session.groups">
        <s:form action="joinGroup" method="post">
            <s:hidden name="id" value="%{group}" /><s:submit value="%{group}"/>
        </s:form>
    </s:iterator>
</body>
</html>
