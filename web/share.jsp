<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: MADALENA
  Date: 17/12/2018
  Time: 02:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SHARE</title>
</head>
<body>
    Tell us more about the music for which you want to share a file...
    <s:form action="shareThis" method="post">
        <s:text name="Music title: "/>
        <s:textfield name="musicTitle"/>
        <s:text name="Artist name: "/>
        <s:textfield name="artistName"/>
        <s:text name="Groups in which you want to share this file (separated by \",\": "/>
        <s:textfield name="groups"/>
        <s:submit/>
    </s:form>
</body>
</html>
