
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>DropMusic</title>
</head>
<body>
    <s:form action="login" method="post">
        <s:text name = "Username:" />
        <s:textfield name="username" /><br>
        <s:text name = "Password: " />
        <s:password name="password" /><br><br>
        <s:submit/>
    </s:form>

    <h4>${session.message}</h4>

    <script>
        window.onload = function () {
            sessionStorage.setItem("notification", "true");
        }
    </script>
</body>
</html>
