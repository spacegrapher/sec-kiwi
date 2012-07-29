package com.kiwi.bubble.appengine.server;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.images.ImagesService.OutputEncoding;

@SuppressWarnings("serial")
public class ImageTransform extends HttpServlet{
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setSizeMax(50000000);

        PrintWriter pw = null;
        try {
            resp.reset();
            pw = resp.getWriter();
            resp.setContentType("text/html");

            FileItemIterator iterator = upload.getItemIterator(req);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                InputStream in = item.openStream();
                BufferedInputStream bis = new BufferedInputStream(in);
                byte[] bisArray = IOUtils.toByteArray(bis);

                Date date = new Date();

                ImagesService imagesService = ImagesServiceFactory.getImagesService();
                Image origImage = ImagesServiceFactory.makeImage(bisArray);
                com.google.appengine.api.datastore.Blob origBlob = new com.google.appengine.api.datastore.Blob(origImage.getImageData());
                ImageObject origImageObject = new ImageObject("origFile.jpg", origBlob, date);
                Transform flip = ImagesServiceFactory.makeHorizontalFlip();
                Image newImage = imagesService.applyTransform(flip, origImage, OutputEncoding.JPEG);
                com.google.appengine.api.datastore.Blob newBlob = new com.google.appengine.api.datastore.Blob(newImage.getImageData());
                ImageObject newImageObject = new ImageObject("newFile.jpg", newBlob, date);

                PersistenceManager pm = PMF.get().getPersistenceManager();
                try {
                	log("preparing orig save. size:"+origBlob.getBytes().length);
                    pm.makePersistent(origImageObject);
                    log("orig save ok~! size");
                	log("preparing new save. size:"+newBlob.getBytes().length);
                    pm.makePersistent(newImageObject);
                    log("target save ok~! size:"+newBlob.getBytes().length);
                    pw.println("<HTML><HEAD></HEAD><BODY>");
                    pw.println("<img src='" + "/ImageSource" + "?id=" + String.valueOf(origImageObject.getId()) + "'/>");
                    pw.println("<img src='" + "/ImageSource" + "?id=" + String.valueOf(newImageObject.getId()) + "'/>");
                    pw.println("</BODY></HTML>");
                } catch (Exception ex) {
                	ex.printStackTrace();
                    // do something
                }
            }
        } catch (Exception ex) {
            //do something
        	ex.printStackTrace();
        }
    }
}