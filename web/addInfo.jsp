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
    <title>ADD INFO</title>
</head>
<body>
    <p>DropMusic</p>
    <p>What do you want to add:</p><br>
    <s:form action="addMusicMenu"><s:submit value="MUSIC"/></s:form>
    <s:form action="addArtistMenu"><s:submit value="ARTIST"/></s:form>
    <s:form action="addAlbumMenu"><s:submit value="ALBUM"/></s:form>
</body>
</html>
