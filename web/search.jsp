<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>DropMusic</title>
</head>
<body>
<div title = "header">
    <s:form action="search" method="get">
    <s:select list="option" name="option">
        <option value = "music" selected>Music</option>
        <option value = "artist">Artist</option>
        <option value = "album">Album</option>
    </s:select>
    <s:textfield name="keyword" />
    <s:submit value="SEARCH"/>
    </s:form>
</div>

</body>>
<div></div>
</body>
</html>