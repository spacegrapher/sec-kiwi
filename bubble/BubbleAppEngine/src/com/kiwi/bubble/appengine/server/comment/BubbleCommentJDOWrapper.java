package com.kiwi.bubble.appengine.server.comment;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.kiwi.bubble.appengine.server.PMF;
import com.kiwi.bubble.appengine.server.bubbledata.BubbleData;

public class BubbleCommentJDOWrapper {
	public static void insertComment(BubbleComment data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}
	
	public static List<BubbleComment> getBubbleByBubbleId(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleComment.class);
		query.setFilter("bubbleId == inputId");
		query.declareParameters("Long inputId");
		
		List<BubbleComment> ret = null;
		try {
			ret = (List<BubbleComment>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
}
