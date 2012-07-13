package com.kiwi.bubble.appengine.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
