package com.appirio;

import java.io.IOException;
import javax.servlet.http.*;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import javax.servlet.*;
import javax.jdo.PersistenceManager;
import com.appirio.entity.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class TelesalesServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 영구 저장 관리자 인스턴스 생성
        PersistenceManager pm = PMF.get().getPersistenceManager();

        // 검색 양식 표시
        if (request.getParameter("action").equals("accountLookup")) {

            // 이름으로 엔티티 질의
            String query = "select from " + Account.class.getName()
                    + " where name == '" + request.getParameter("accountName")
                    + "'";
            List<Account> accounts = (List<Account>) pm.newQuery(query).execute();
            // 리스트를 JSP에 넘긴다.
            request.setAttribute("accounts", accounts);
            // 요청을 JSP로 전달(forward)한다.
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/accountLookup.jsp");
            dispatcher.forward(request, response);
            // 신규 계정 생성 양식을 표시한다.
        } else if (request.getParameter("action").equals("accountCreate")) {
            response.sendRedirect("/accountCreate.jsp");
            // 신규 계정 생성 작업을 처리하고 사용자를 계정 정보 표시 페이지로
            // 보낸다.
        } else if (request.getParameter("action").equals("accountCreateDo")) {
            // 신규 계정을 생성한다.
            Account a = new Account(request.getParameter("name"), 
                    request.getParameter("billingCity"), 
                    request.getParameter("billingState"), 
                    request.getParameter("phone"), 
                    request.getParameter("website"));

            // 엔티티를 영구 저장한다.
            try {
                pm.makePersistent(a);
            } finally {
                pm.close();
            }
            response.sendRedirect("telesales?action=accountDisplay&accountId="
                    + a.getId());
            // 계정 상세 정보와 영업 기회 목록을 표시한다.
        } else if (request.getParameter("action").equals("accountDisplay")) {
            // 계정을 얻어 온다.
            Key k = KeyFactory.createKey(Account.class.getSimpleName(),
                    new Integer(request.getParameter("accountId")).intValue());
            Account a = pm.getObjectById(Account.class, k);
            // 영업 기회를 질의한다.
            String query = "select from " + Opportunity.class.getName()
                    + " where accountId ==" + request.getParameter("accountId");
            List<Opportunity> opportunities = (List<Opportunity>) pm.newQuery(query).execute();
            // 계정 정보를 JSP로 넘긴다.
            request.setAttribute("account", a);
            // 영업기회 목록을 JSP로 넘긴다.
            request.setAttribute("opportunities", opportunities);
            // 요청을 JSP로 전달한다.
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/accountDisplay.jsp");
            dispatcher.forward(request, response);
            // 신규 영업 기회 등록 양식을 표시한다.
        } else if (request.getParameter("action").equals("opportunityCreate")) {
            Key k = KeyFactory.createKey(Account.class.getSimpleName(),
                    new Integer(request.getParameter("accountId")).intValue());
            Account a = pm.getObjectById(Account.class, k);
            // 계정 이름을 jsp에 넘긴다.
            request.setAttribute("accountName", a.getName());
            // 요청을 jsp로 전달한다.
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/opportunityCreate.jsp");
            dispatcher.forward(request, response);
            // 신규 영업 기회 생성 작업을 처리하고 사용자를 계정 정보 표시 페이지로
            // 보낸다.
        } else if (request.getParameter("action").equals("opportunityCreateDo")) {
            Date closeDate = new Date();
            // 날짜값을 파싱한다.
            DateFormat df = DateFormat.getDateInstance(3);
            try {
                closeDate = df.parse(request.getParameter("closeDate"));
            } catch (java.text.ParseException pe) {
                System.out.println("Exception " + pe);
            }
            // 신규 영업 기회를 생성한다.
            Opportunity opp = new Opportunity(request.getParameter("name"),
                    new Double(request.getParameter("amount")).doubleValue(),
                    request.getParameter("stageName"), new Integer(request
                            .getParameter("probability")).intValue(),
                    closeDate, new Integer(request.getParameter("orderNumber"))
                            .intValue(), new Long(request
                            .getParameter("accountId")));
            // 엔티티를 영구 저장한다.
            try {
                pm.makePersistent(opp);
            } finally {
                pm.close();
            }
            response.sendRedirect("telesales?action=accountDisplay&accountId="
                    + request.getParameter("accountId"));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}