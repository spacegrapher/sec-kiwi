/**
 * 
 */
package com.kiwi.bubble.appengine.server;

/**
 * @author seongwon
 *
 */
import java.io.IOException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ImageSource extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/jpeg");
        PersistenceManager pm = PMF.get().getPersistenceManager();
        resp.getOutputStream().write(pm.getObjectById(ImageObject.class, Long.valueOf(req.getParameter("id").toString())).getContent().getBytes());
        resp.getOutputStream().flush();
        resp.getOutputStream().close();
    }
}
