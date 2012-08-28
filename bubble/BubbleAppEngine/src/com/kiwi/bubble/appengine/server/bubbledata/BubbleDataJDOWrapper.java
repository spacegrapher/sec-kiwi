package com.kiwi.bubble.appengine.server.bubbledata;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.kiwi.bubble.appengine.server.PMF;

public class BubbleDataJDOWrapper {
	public static void insertBubble(BubbleData data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}
	
	public static List<BubbleData> getAllBubbles() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		query.setOrdering("postTime desc");
		
		List<BubbleData> ret = null;
		try {
			ret = (List<BubbleData>) query.execute();
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<BubbleData> getFriendBubbles(Long id, List<Long> friendId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		String filter = "(authorId == inputId)";
		for(int i=0; i<friendId.size(); i++) {
			filter += "|| (authorId == " + friendId.get(i) + ")";
		}
		query.setFilter(filter);
		query.declareParameters("Long inputId");
		query.setOrdering("postTime desc");
		
		List<BubbleData> ret = null;
		try {
			ret = (List<BubbleData>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<BubbleData> getBubbleByAuthorId(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		query.setFilter("authorId == inputId");
		query.declareParameters("Long inputId");
		query.setOrdering("postTime desc");
		
		List<BubbleData> ret = null;
		try {
			ret = (List<BubbleData>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<BubbleData> getBubbleById(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		query.setFilter("id == inputId");
		query.declareParameters("Long inputId");
		query.setOrdering("postTime desc");
		
		List<BubbleData> ret = null;
		try {
			ret = (List<BubbleData>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<BubbleData> getBubbleByTag(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		query.setFilter("tag.contains(tagId)");
		query.declareParameters("Long tagId");
		query.setOrdering("postTime desc");
		
		List<BubbleData> ret = null;
		
		try {
			ret = (List<BubbleData>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<BubbleData> getBubbleByTags(List<Long> id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		String filter = new String();
		for(int i=0; i<id.size(); i++) {
			if (i>0) filter += " && ";
			filter += "tag.contains(" + id.get(i) + ")";
		}
		query.setFilter(filter);
		query.setOrdering("postTime desc");
		List<BubbleData> ret = null;
		
		try {
			ret = (List<BubbleData>) query.execute();
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
}
