package com.kiwi.bubble.appengine.server.userinfo;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Blob;
import com.kiwi.bubble.appengine.server.PMF;

public class UserInfoJDOWrapper {
	public static void insertUser(UserInfo info) {
		boolean exists = checkUserExists(info);
		if (!exists) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(info);
			} finally {
				pm.close();
			}
		} else {
			System.err.println("ERROR: Duplicate input");
		}
	}
	
	public static void addUserFriend(Long id, Long friendId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
	    try {
	    	UserInfo userInfo = pm.getObjectById(UserInfo.class, id);
	    	if (userInfo.isFriend(friendId)) {
	    		userInfo.removeFriend(friendId);
	    	} else {
	    		userInfo.addFriend(friendId);
	    	}
	    } finally {
	        pm.close();
	    }
	}
	
	public static List<UserInfo> getUserByEmail(String email) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(UserInfo.class);
		query.setFilter("email == inputEmail");
		query.declareParameters("String inputEmail");
		
		List<UserInfo> ret = null;
		try {
			ret = (List<UserInfo>) query.execute(email);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<UserInfo> getUserByEmailAndPassword(String email, String password) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(UserInfo.class);
		query.setFilter("email == inputEmail && password == inputPassword");
		query.declareParameters("String inputEmail, String inputPassword");
		
		List<UserInfo> ret = null;
		try {
			ret = (List<UserInfo>) query.execute(email, password);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static List<UserInfo> getUserById(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(UserInfo.class);
		query.setFilter("id == inputId");
		query.declareParameters("Long inputId");
		
		List<UserInfo> ret = null;
		try {
			ret = (List<UserInfo>) query.execute(id);
		} finally {
			query.closeAll();
		}
		
		return ret;
	}
	
	public static Long getUserIdByEmailAndPassword(String email, String password) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(UserInfo.class);
		query.setFilter("email == inputEmail && password == inputPassword");
		query.declareParameters("String inputEmail, String inputPassword");
		
		List<UserInfo> ret = null;
		try {
			ret = (List<UserInfo>) query.execute(email, password);
		} finally {
			query.closeAll();
		}
		
		if (ret == null || ret.isEmpty())
			return Long.valueOf(-1);
		
		assert(ret.size() == 1);
		
		return ret.get(0).getId();
	}
	
	private static boolean checkUserExists(UserInfo info) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(UserInfo.class, "email == inputEmail");
		query.declareParameters("String inputEmail");
		List<UserInfo> ret = null;
		try {
			ret = (List<UserInfo>) query.execute(info.getEmail());
		} finally {
			query.closeAll();
		}
		return !ret.isEmpty();
	}
}
