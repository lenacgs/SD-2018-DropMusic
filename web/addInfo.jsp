<%--
  Created by IntelliJ IDEA.
  User: MADALENA
  Date: 14/12/2018
  Time: 22:15
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>ADD INFO</title>
</head>
<body>
    <h4>Do you want to add a new...</h4>
    <s:form action="addMusicButton"><s:submit value="MUSIC"/></s:form>
    <s:form action="addAlbumButton"><s:submit value="ALBUM"/></s:form>
    <s:form action="addArtistButton"><s:submit value="ARTIST"/></s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
