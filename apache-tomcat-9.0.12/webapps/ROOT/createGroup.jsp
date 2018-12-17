<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/14/2018
  Time: 6:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Group</title>
</head>
<body>
    <p>DropMusic</p>
    <p>Do you really want to create a new group?</p>
    <s:form action="createGroup"><s:submit value="YES"/></s:form>
    <s:form action="menu"><s:submit value="NO"/></s:form>

</body>
</html>
