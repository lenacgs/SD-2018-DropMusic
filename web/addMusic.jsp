<%--
  Created by IntelliJ IDEA.
  User: MADALENA
  Date: 14/12/2018
  Time: 22:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>ADD MUSIC</title>
</head>
<body>
    <s:form action="addMusic" method="post">
        <s:text name = "Groups you want to share this with (separated by \",\"):"/>
        <s:textfield name="groups" /><br><br>
        <s:text name = "Music title: " />
        <s:textfield name="title" /><br><br>
        <s:text name = "Artist name: " />
        <s:textfield name="artist" /><br><br>
        <s:text name = "Genre: " />
        <s:textfield name="genre" /><br><br>
        <s:text name = "Duration: " />
        <s:textfield name="duration" /><br><br>
        <s:submit/>
    </s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
