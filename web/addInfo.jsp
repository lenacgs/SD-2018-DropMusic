<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/17/2018
  Time: 12:10 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <s:if test="%{#session.operation == 'add'}">
        <title>ADD INFO</title>
    </s:if>
    <s:else>
        <title>CHANGE INFO</title>
    </s:else>
</head>
<body>
    <p>DropMusic</p>
    <s:if test="%{#session.operation == 'add'}">
        <p>What do you want to add:</p><br>
    </s:if>
    <s:else>
        <p>What do you want to change:</p><br>
    </s:else>
    <s:form action="changeMusicMenu"><s:submit value="MUSIC"/></s:form>
    <s:form action="changeArtistMenu"><s:submit value="ARTIST"/></s:form>
    <s:form action="changeAlbumMenu"><s:submit value="ALBUM"/></s:form>

</body>
</html>
