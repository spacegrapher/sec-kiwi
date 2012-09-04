package com.kiwi.bubble.appengine.server.tag;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.GeoPt;
import com.kiwi.bubble.appengine.server.PMF;

public class BubbleTagJDOWrapper {
	public static void insertTag(BubbleTag data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleTag> getAllTags() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleTag.class);
		query.setOrdering("text asc");

		List<BubbleTag> ret = null;
		try {
			ret = (List<BubbleTag>) query.execute();
		} finally {
			query.closeAll();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleTag> getTagById(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleTag.class);
		query.setFilter("id == inputId");
		query.declareParameters("long inputId");

		List<BubbleTag> ret = null;
		try {
			ret = (List<BubbleTag>) query.execute(id);
		} finally {
			query.closeAll();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleTag> getTagByText(String text) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleTag.class);
		query.setFilter("type == 0");
		query.setFilter("text == inputText");
		query.declareParameters("String inputText");

		List<BubbleTag> ret = null;
		try {
			ret = (List<BubbleTag>) query.execute(text);
		} finally {
			query.closeAll();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleTag> getTagByUser(long text) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleTag.class);
		query.setFilter("type == 1");
		query.setFilter("user == inputUser");
		query.declareParameters("long inputUser");

		List<BubbleTag> ret = null;
		try {
			ret = (List<BubbleTag>) query.execute(text);
		} finally {
			query.closeAll();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public static List<BubbleTag> getTagByLocation(GeoPt location) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(BubbleTag.class);
		query.setFilter("type == 2");
		query.setFilter("location == inputLocation");
		query.declareParameters("GeoPt inputLocation");

		List<BubbleTag> ret = null;
		try {
			ret = (List<BubbleTag>) query.execute(location);
		} finally {
			query.closeAll();
		}

		return ret;
	}
}
