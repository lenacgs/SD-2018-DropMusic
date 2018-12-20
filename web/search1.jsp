<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/15/2018
  Time: 6:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search</title>
</head>
<body>
    <p>DropMusic</p><br>
    <s:form action="search" method="post">
        Search for: <select name="object">
        <option value="music">Music</option>
        <option value="artist">Artist</option>
        <option value="album">Album</option>
    </select><br>
        Keyword: <input type="text" name = "keyword"><br>
        <s:submit value="SEARCH"/>
    </s:form>
    <s:form action="menu"><s:submit value="BACK"/></s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
