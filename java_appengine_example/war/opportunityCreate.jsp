<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String accountName = (String)request.getAttribute("accountName");
%>
<html> 
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>통신 판매 데모 (자바용 구글 앱 엔진)</title>
	<link rel="stylesheet" type="text/css" href="/stylesheets/styles.css"/> 
</head> 
<body>
	<span class="nav">
		<a href="telesales?action=accountDisplay&accountId=<%= request.getParameter("accountId") %>">뒤로가기</a></span><p/>
	<span class="title">신규 영업 기회 생성</span> <p/>
	
	<form method="post" action="telesales?action=opportunityCreateDo&accountId=<%= request.getParameter("accountId") %>">
	<input type="hidden" name="accountId" value="{{accountId}}"> 
	<table border="0" cellspacing="1" cellpadding="5" bgcolor="#CCCCCC">
		<tr bgcolor="#407BA8"> 
			<td style="color: #ffffff; font-weight: bold;">계정</td>
			<td bgcolor="#ffffff"><%= accountName %></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">이름</td>
			<td bgcolor="#ffffff"><input type="input" name="name" style="width:250px"></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">총액</td>
			<td bgcolor="#ffffff"><input type="input" name="amount" value="125.25"></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">단계</td>
			<td bgcolor="#ffffff"> 
				<select name="stageName"> 
					<option>관찰</option> 
					<option>조건부</option> 
					<option>가치 제안</option>
				</select> 
			</td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">가능성</td>
			<td bgcolor="#ffffff"> 
				<select name="probability">
					<option value="10">10%</option> 
					<option value="25">25%</option> 
					<option value="50">50%</option> 
					<option value="75">75%</option>
				</select> 
			</td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">마감일</td>
			<td bgcolor="#ffffff"><input type="input" name="closeDate" value="2012. 1. 1"></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">주문량</td>
			<td bgcolor="#ffffff"><input type="input" name="orderNumber" value="7"></td>
		</tr> 
		<tr>
			<td colspan="2" bgcolor="#ffffff" align="center"><input type="submit" value="등록"></td>
		</tr> 
		</table>
	</form>
	
	입력 값 확인을 하지 않으니 모든 항목을 채워주십시오.
</body> 
</html>