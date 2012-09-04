package com.kiwi.bubble.appengine.server.comment;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.kiwi.bubble.appengine.server.PMF;

public class BubbleCommentJDOWrapper {
	public static void insertComment(BubbleComment data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleComment> getBubbleByBubbleId(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleComment.class);
		query.setFilter("bubbleId == inputId");
		query.declareParameters("Long inputId");
		query.setOrdering("postTime asc");

		List<BubbleComment> ret = null;
		try {
			ret = (List<BubbleComment>) query.execute(id);
		} finally {
			query.closeAll();
		}

		return ret;
	}
}
