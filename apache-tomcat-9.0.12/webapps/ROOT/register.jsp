
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>DropMusic</title>
</head>
<body>
<s:form action="register" method="post">
    <s:text name = "Username:" />
    <s:textfield name="username" /><br>
    <s:text name = "Password: " />
    <s:password name="password" /><br><br>
    <s:submit value="REGISTER"/>
</s:form>
</body>
</html>
