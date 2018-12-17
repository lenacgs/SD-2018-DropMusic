<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/16/2018
  Time: 4:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Album Review</title>
</head>
<body>
    <p>DropMusic</p><br>
    <s:form action="review" method="post">
        Album Title: <input type="text" name = "title"><br>
        Album Artist: <input type="text" name = "artist"><br>
        Album Review (MAX: 300 words): <input type="text" name = "review"><br>
        Album Rate: <select name="rate">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
    </select><br>
        <s:submit value="SUBMIT REVIEW"/>
    </s:form>
    <s:form action="menu"><s:submit value="BACK"/></s:form>

    <jsp:include page="notifications.jsp"/>

</body>
</html>
