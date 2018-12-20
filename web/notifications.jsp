
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<html>
<head>
    <title>DropMusic</title>
</head>
<body>
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
        if(sessionStorage.getItem("notification") == 'true'){
            websocket.send("get_notifications | " + "${session.username}");
            sessionStorage.setItem("notification", "false");
        }else{
            websocket.send("username | " + "${session.username}");
        }
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
