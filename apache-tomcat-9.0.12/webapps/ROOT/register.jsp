
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
    <s:submit type = "button">
        <s:text name="Register"></s:text>
    </s:submit>
</s:form>
</body>
</html>
