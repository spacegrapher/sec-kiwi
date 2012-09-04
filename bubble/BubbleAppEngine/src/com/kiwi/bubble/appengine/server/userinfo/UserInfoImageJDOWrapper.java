package com.kiwi.bubble.appengine.server.userinfo;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Blob;
import com.kiwi.bubble.appengine.server.PMF;

public class UserInfoImageJDOWrapper {
	public static void insertImage(UserInfoImage data) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	public static void updateImage(Long id, Blob image) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			UserInfoImage userInfoImage = pm.getObjectById(UserInfoImage.class,
					getImageByUserId(id).get(0).getId());
			userInfoImage.setContent(image);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<UserInfoImage> getImageByUserId(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query query = pm.newQuery(UserInfoImage.class);
		query.setFilter("userId == inputId");
		query.declareParameters("Long inputId");

		List<UserInfoImage> ret = null;
		try {
			ret = (List<UserInfoImage>) query.execute(id);
		} finally {
			query.closeAll();
		}
		return ret;
	}
}
