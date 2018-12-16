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
    <p>MENU</p>

    <h4>${session.message}</h4>

    <s:form action="search"><s:submit value="SEARCH"/></s:form>
    <s:form action="details"><s:submit value="ALBUM & ARTIST DETAILS"/></s:form>
    <s:form action="review"><s:submit value="ALBUM REVIEW"/></s:form>
    <s:form action="upload"><s:submit value="UPLOAD MUSIC FILE"/></s:form>
    <s:form action="download"><s:submit value="DOWNLOAD MUSIC FILE"/></s:form>
    <s:form action="createGroup"><s:submit value="CREATE GROUP"/></s:form>
    <s:form action="joinGroup"><s:submit value="JOIN GROUP"/></s:form>
    <s:form action="editorPrivileges"><s:submit value="GRANT EDITOR PRIVILEGES"/></s:form>
    <s:form action="ownerPrivileges"><s:submit value="GRANT OWNER PRIVILEGES"/></s:form>
    <s:form action="addInfo"><s:submit value="ADD INFO"/></s:form>
    <s:form action="changeInfo"><s:submit value="CHANGE INFO"/></s:form>
    <s:form action="manageGroups"><s:submit value="MANAGE GROUPS"/></s:form>
    <s:form action="associateButton"><s:submit value="LINK DROPBOX ACCOUNT"/></s:form>

    <div id="container"><div id="history"></div></div>

    <script type="text/javascript">
        var websocket = null;

        window.onload = function () {
             connect('ws://' + window.location.host + '/ws');
        }

        function connect(host) {
            if('WebSocket' in window)
                websocket = new WebSocket(host);
            else if ('MozWebSocket' in window)
                websocket = new MozWebSocket(host);
            else{
                writeToHistory('Get a real browser which supports WebSocket.');
                return;
            }

            websocket.onopen = onOpen;
            websocket.onclose = onClose;
            websocket.onmessage = onMessage;
            websocket.onerror = onError;
        }

        function onOpen(event) {
            websocket.send("username | " + "${session.username}");
            writeToHistory("NOTIFICATIONS");
        }

        function onClose(event) {
            writeToHistory('Notifications are turned off!');
        }

        function onMessage(message) {
           writeToHistory(message.data);
        }

        function onError(event) {
            writeToHistory('WebSocket error.')
        }


        function writeToHistory(text) {
            var history = document.getElementById('history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;
        }

    </script>

    <noscript>JavaScript must be enabled for WebSockets to work.</noscript>
</body>
</html>
