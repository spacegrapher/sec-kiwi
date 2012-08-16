package com.kiwi.bubble.appengine.server.bubbledata;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.kiwi.bubble.appengine.server.PMF;

public class BubbleDataImageJDOWrapper {
	public static void insertImage(BubbleDataImage data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}
	
	public static List<BubbleDataImage> getImageByBubbleId(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        Query query = pm.newQuery(BubbleDataImage.class);
        query.setFilter("bubbleId == inputId");
		query.declareParameters("Long inputId");
		
        List<BubbleDataImage> ret = null;
		try {
			ret = (List<BubbleDataImage>) query.execute(id);
		} finally {
			query.closeAll();
		}
		return ret;
	}
}
