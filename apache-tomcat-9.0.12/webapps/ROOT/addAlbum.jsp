<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/17/2018
  Time: 12:44 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Album</title>
</head>
<body>
    <p>DropMusic</p>
    <s:form action="addInfo" method="post">
        <s:hidden name="object" value="album" />
        Group to add the album into: <input type="text" name = "group"><br>
        Album Title: <input type="text" name = "title"><br>
        Album Artist: <input type="text" name = "artist"><br>
        Album Musics (separated by ','): <input type="text" name = "musics"><br>
        Album Year of Creation: <input type="text" name = "year"><br>
        Album Publisher: <input type="text" name = "publisher"><br>
        Album Genre: <input type="text" name = "genre"><br>
        Album Description: <input type="text" name = "description"><br>
        <s:submit value="ADD ALBUM"/>
    </s:form>
</body>
</html>
