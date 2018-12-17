<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Alvineca
  Date: 12/15/2018
  Time: 6:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search</title>
</head>
<body>
    <p>DropMusic</p><br>
    <s:if test="%{#session.object == 'music'}">
        <s:iterator var="item" value="#session.search">
            <li><s:property /></li>
        </s:iterator>
    </s:if>
    <s:else>
        <s:iterator var="item" value="#session.search">
            <s:form action="details" method="post">
                <li>
                <s:hidden name="id" value="%{item}"/>
                <s:hidden name="object" value="%{#session.object}"/>
                <s:text name="%{item}"/>   <s:submit value="DETAILS"/>
                </li>
            </s:form>
        </s:iterator>
    </s:else>
    <s:form action="searchMenu"><s:submit value="BACK"/></s:form>

    <jsp:include page="notifications.jsp"/>
</body>
</html>
