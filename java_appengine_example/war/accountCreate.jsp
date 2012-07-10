<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>통신 판매 데모 (자바용 구글 앱 엔진)</title>
	<link rel="stylesheet" type="text/css" href="/stylesheets/styles.css"/>
</head>
<body>
	<span class="nav"><a href="index.html">뒤로가기</a></span><p/>
	<span class="title">신규 계정 생성</span>
	<p/>
	
	<form method="get" action="telesales">
	<input type="hidden" name="action" value="accountCreateDo"/>
	<table border="0" cellspacing="1" cellpadding="5" bgcolor="#CCCCCC">
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">이름</td>
			<td bgcolor="#ffffff"><input type="input" name="name"></td>
		</tr>
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">시</td>
			<td bgcolor="#ffffff"><input type="input" name="billingCity"></td> 
		</tr>
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">주</td>
			<td bgcolor="#ffffff"><input type="input" name="billingState"></td>
		</tr>
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">전화</td>
			<td bgcolor="#ffffff"><input type="input" name="phone"></td>
		</tr>
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">웹사이트</td>
			<td bgcolor="#ffffff"><input type="input" name="website"></td>
		</tr>
		<tr>
			<td colspan="2" bgcolor="#ffffff" align="center">
				<input type="submit" value="등록"></td>
		</tr> 
	</table>
	</form>
</body> 
</html>