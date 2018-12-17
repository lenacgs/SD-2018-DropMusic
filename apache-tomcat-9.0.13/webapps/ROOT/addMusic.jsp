<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/17/2018
  Time: 12:15 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <s:if test="%{#session.operation == 'add'}">
        <title>Add Music</title>
    </s:if>
    <s:else>
        <title>Change Music</title>
    </s:else>
</head>
<body>
    <p>DropMusic</p>
    <s:if test="%{#session.operation == 'add'}">
        <s:form action="addInfo" method="post">
            <s:hidden name="object" value="music" />
            Group to add the music into: <input type="text" name = "group"><br>
            Music Title: <input type="text" name = "title"><br>
            Music Artist: <input type="text" name = "artist"><br>
            Music Genre: <input type="text" name = "genre"><br>
            Music Duration: <input type="text" name = "duration"><br>
            <s:submit value="ADD MUSIC"/>
        </s:form>
    </s:if>
    <s:else>
        <s:form action="changeInfo" method="post">
            <s:hidden name="object" value="music" />
            Group where to apply the music changes: <input type="text" name = "group"><br>
            Music Title: <input type="text" name = "title"><br>
            Music Artist: <input type="text" name = "artist"><br>
            Music Genre: <input type="text" name = "genre"><br>
            Music Duration: <input type="text" name = "duration"><br>
            <s:submit value="CHANGE MUSIC"/>
        </s:form>
    </s:else>
</body>
</html>
