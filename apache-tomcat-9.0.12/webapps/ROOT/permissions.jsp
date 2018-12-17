<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/15/2018
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Permissions</title>
</head>
<body>
    <p>DropMusic</p><br>
    <s:form action="givePermissions" method="post">
        Privilege: <select name="perk">
            <option value="owner">Owner</option>
            <option value="editor">Editor</option>
        </select><br>
        Group ID: <input type="text" name = "groupId"><br>
        User: <input type="text" name = "newUser"><br>
        <s:submit value="GIVE PERMISSIONS"/>
    </s:form>
</body>
</html>
