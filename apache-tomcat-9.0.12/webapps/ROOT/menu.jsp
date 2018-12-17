<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: João Silva
  Date: 10/12/2018
  Time: 15:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Menu</title>
</head>
<body>
    <s:if test="hasActionMessages()">
        <s:actionmessage/>
    </s:if><br>
    <s:if test="hasActionErrors()">
        <s:actionerror/>
    </s:if><br>
    <p>MENU</p>
    <s:form action="searchMenu"><s:submit value="SEARCH"/></s:form>
    <s:form action="reviewMenu"><s:submit value="ALBUM REVIEW"/></s:form>
    <s:form action="createGroupMenu"><s:submit value="CREATE GROUP"/></s:form>
    <s:form action="joinGroupMenu"><s:submit value="JOIN GROUP"/></s:form>
    <s:form action="addInfoMenu"><s:submit value="ADD INFO"/></s:form>
    <s:form action="changeInfo"><s:submit value="CHANGE INFO"/></s:form>
    <s:form action="upload"><s:submit value="UPLOAD MUSIC FILE"/></s:form>
    <s:form action="download"><s:submit value="DOWNLOAD MUSIC FILE"/></s:form>
    <s:if test="%{#session.perks == 1 || #session.perks == 2}">
        <s:form action="manageGroupsMenu"><s:submit value="MANAGE GROUPS"/></s:form>
        <s:form action="permissionsMenu"><s:submit value="GRANT PRIVILEGES TO ANOTHER USER"/></s:form>
    </s:if>
    <s:form action="associateButton"><s:submit value="LINK DROPBOX ACCOUNT"/></s:form>

    <h4>${session.message}</h4>
</body>
</html>
