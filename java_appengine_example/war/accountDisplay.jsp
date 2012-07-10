<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%> 
<%@ page import="java.text.SimpleDateFormat"%> 
<%@ page import="com.appirio.entity.*"%>
<%
	Account account = (Account)request.getAttribute("account");
	List<Opportunity> opportunities = (List<Opportunity>)request.getAttribute("opportunities");
	SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>통신 판매 데모 (자바용 구글 앱 엔진)</title>
	<link rel="stylesheet" type="text/css" href="/stylesheets/styles.css"/> 
</head>
<body>
	<span class="nav"><a href="/index.html">검색</a></span><p/> 
	<span class="title">계정 상세 정보</span> 
	<p/>
	<table border="0" cellspacing="1" cellpadding="5" bgcolor="#CCCCCC"> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">이름</td> 
			<td bgcolor="#ffffff"><%= account.getName() %></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">시</td> 
			<td bgcolor="#ffffff"><%= account.getCity() %></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">주</td>
			<td bgcolor="#ffffff"><%= account.getState() %></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">전화번호</td>
			<td bgcolor="#ffffff"><%= account.getPhone() %></td>
		</tr> 
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">웹 사이트</td>
			<td bgcolor="#ffffff"><%= account.getWebsite() %></td>
		</tr> 
	</table>
	
	<br><a href="telesales?action=opportunityCreate&accountId=<%= account.getId() %>">신규 영업 기회 생성</a><p/>

	<% if (opportunities != null && opportunities.size() > 0) { %> 
	
		<p/><span class="heading"><%= account.getName() %>의 영업 기회</span><br><p/>
		
		<table border="0" cellspacing="1" cellpadding="5" bgcolor="#CCCCCC">
		<tr bgcolor="#407BA8">
			<td style="color: #ffffff; font-weight: bold;">이름</td> 
			<td style="color: #ffffff; font-weight: bold;">총액</td> 
			<td style="color: #ffffff; font-weight: bold;">단계</td> 
			<td style="color: #ffffff; font-weight: bold;">가능성</td>
			<td style="color: #ffffff; font-weight: bold;">마감일</td>
			<td style="color: #ffffff; font-weight: bold;">주문량</td>
		</tr>
		<% for (int i = 0;i<opportunities.size();i++) { %>
			<% Opportunity o = (Opportunity)opportunities.get(i); %> 
			<tr style="background:#ffffff" onMouseOver="this.style.background='#eeeeee';" onMouseOut="this.style.background='#ffffff';">
				<td nowrap><%= o.getName() %></td> 
				<td><%= o.getAmount() %></td> 
				<td><%= o.getStageName() %></td> 
				<td><%= o.getProbability() %>%</td> 
				<td><%= sdf.format(o.getCloseDate()) %></td> 
				<td><%= o.getOrderNumber() %></td>
			</tr> 
		<% } %>
		</table>
		
	<% } else { %> 
		<p/><span class="heading"><%= account.getName() %> 고객님의 영업 기회가 등록되지 않았습니다.</span> 
	<% } %>	
</body> 
</html>