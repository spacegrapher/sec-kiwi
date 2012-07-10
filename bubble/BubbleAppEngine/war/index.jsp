<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>
<jsp:useBean id="settings" class="com.kiwi.bubble.appengine.server.SettingsBean"/>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html charset=utf-8">
	<link href="${settings.cssPath}/base.css" type="text/css" rel="stylesheet">
	<title>Welcome to Bubble!</title>
</head>
<body>
	<div id="logo"><b>Bubble</b></div>
	<div id="menu">
	Home
	<a href="/bubble/new/">Create</a>
	</div>
	<div id="content">
		Hello,
		<c:choose>
			<c:when test="${user_info == null}">
				<a href="/user/login/">Login</a><br \>
			</c:when>
			<c:otherwise>
				Hello, <a href="/user/">${user_info.nickname}</a> /
				<a href="${settings.logoutPath}">Logout</a><br \>
			</c:otherwise>
		</c:choose>
		<br \>
		Bubble bubble!<br \>
		<br \>
	</div>
</body>
</html>