<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/17/2018
  Time: 12:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <s:if test="%{#session.operation == 'add'}">
        <title>Add Artist</title>
    </s:if>
    <s:else>
        <title>Change Artist</title>
    </s:else>
</head>
<body>
    <p>DropMusic</p>
    <s:if test="%{#session.operation == 'add'}">
        <s:form action="addInfo" method="post">
            <s:hidden name="object" value="artist" />
            Group to add the artist into: <input type="text" name = "group"><br>
            Artist Name: <input type="text" name = "title"><br>
            Artist Description: <input type="text" name = "description"><br>
            Artist Genre: <input type="text" name = "genre"><br>
            Artist Concerts: <input type="text" name = "concerts"><br>
            <s:submit value="ADD ARTIST"/>
        </s:form>
    </s:if>
    <s:else>
        <s:form action="changeInfo" method="post">
            <s:hidden name="object" value="artist" />
            Group  where to apply the artist changes: <input type="text" name = "group"><br>
            Artist Name: <input type="text" name = "title"><br>
            Artist Description: <input type="text" name = "description"><br>
            Artist Genre: <input type="text" name = "genre"><br>
            Artist Concerts: <input type="text" name = "concerts"><br>
            <s:submit value="CHANGE ARTIST"/>
        </s:form>
    </s:else>
</body>
</html>
