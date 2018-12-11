
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
<p>DropMusic</p>
<a href = "login.jsp"> Search</a><br>
<a href = "register.jsp"> Album and Artist details</a><br>
<a href = "register.jsp"> Album Review</a><br>
<a href = "register.jsp"> Upload Music</a><br>
<a href = "register.jsp"> Download Music</a><br>
<a href = "register.jsp" type = "button"> Share Music</a><br>
<s:form action = "create_group" method="post">
    <s:submit type ="button">
        <s:text name="Create Group"></s:text>
    </s:submit>
</s:form>
<a href = "register.jsp"> Join Group</a><br>



<a href = "register.jsp"> Logout</a>






</body>
</html>