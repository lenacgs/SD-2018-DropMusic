
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

  <s:form action="loginMenu"><s:submit value="LOGIN"/></s:form>
  <s:form action="registerMenu"><s:submit value="REGISTER"/></s:form>
  <s:form action="loginDropbox"><s:submit value="LOGIN WITH DROPBOX"/></s:form>
</body>
</html>