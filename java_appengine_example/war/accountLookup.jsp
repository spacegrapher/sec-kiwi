<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%> 
<%@ page import="com.appirio.entity.*"%>
<%
	List<Account> accounts = (List<Account>)request.getAttribute("accounts"); 
%>
<html> 
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>통신 판매 데모 (자바용 구글 앱 엔진)</title>
	<link rel="stylesheet" type="text/css" href="/stylesheets/styles.css"/> 
</head> 
<body>
	<span class="title">통신 판매 데모 (자바용 구글 앱 엔진)</span>
	<p/>
	<p>신규 영업 기회를 생성하기 전에 계정이 먼저 등록되어 있어야 합니다. <a href="telesales?action=accountCreate">새 계정 등록</a>도 가능합니다.</p>
	<p/> 
	<form method="post" action="telesales">
		<input type="hidden" name="action" value="accountLookup"/> 
		<span class="heading">계정 이름 검색</span> 
		<p/> 
		<input type="text" name="accountName" value="<% if (request.getParameter("accountName") != null) { out.println(request.getParameter("accountName")); } %>" style="width: 300px"/>
		&nbsp 
		<input type="submit" value="Search"/> 
		&nbsp
	</form> 
	<p/> 
	<% if (accounts.size() > 0) { %>
		<span class="heading">계정이 <%= accounts.size() %> 건 검색되었습니다.</span>
		<p/>
		<table border="0" cellspacing="1" cellpadding="5" bgcolor="#CCCCCC" width="50%">
		<tr bgcolor="#407BA8"> 
			<td style="color: #ffffff; font-weight: bold;">이름</td> 
			<td style="color: #ffffff; font-weight: bold;">시</td>
			<td style="color: #ffffff; font-weight: bold;">주</td>
			<td style="color: #ffffff; font-weight: bold;">전화</td> 
		</tr>		
		<% for (int i = 0;i<accounts.size();i++) { %>
			<% Account a = (Account)accounts.get(i); %>
			<tr style="background:#ffffff" onMouseOver="this.style.background='#eeeeee';" onMouseOut="this.style.background='#ffffff';">
				<td><a href="telesales?action=accountDisplay&accountId=<%= a.getId() %>"><%=
a.getName() %></a></td>
				<td><%= a.getCity() %></td> 
				<td><%= a.getState() %></td> 
				<td><%= a.getPhone() %></td>
			</tr> 
		<% } %>
		</table>
	<% } else { %> 
		<span class="heading">해당하는 계정을 찾지 못했습니다.</span>
	<% } %> 
	<p/>
</body> 
</html>