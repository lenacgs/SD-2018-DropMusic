<%--
  Created by IntelliJ IDEA.
  User: MADALENA
  Date: 16/12/2018
  Time: 18:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
    <title>UPLOAD</title>
</head>
<body>
    Your files on dropbox are:</br>
    <s:iterator value="musics" var="music"><s:property value="music"/><br></s:iterator>
    <s:form action="uploadThis" method="post">
        <s:text name="Which of these?: "/>
        <s:textfield name="index"/>
        Tell us more about the music you want to associate this music file with...
        <s:text name="Music title: "/>
        <s:textfield name="musicTitle"/>
        <s:text name="Artist name: "/>
        <s:textfield name="artistName"/>
        <s:submit/>
    </s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
