package com.kiwi.bubble.appengine.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class BubbleDataJDOWrapper {
	public static void insertBubble(BubbleData data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}
	
	public static List<BubbleData> getBubbleByEmail(String email) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(BubbleData.class);
		query.setFilter("authorEmail == inputEmail");
		query.declareParameters("String inputEmail");
		
		List<BubbleData> ret = null;
		try {
			ret = (List<BubbleData>) query.execute(email);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
}
