/**
 * 
 */
package com.kiwi.bubble.appengine.server.userinfo;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Blob;
import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable(detachable="true")
public class UserInfoImage extends ModelBase {
	@Persistent
	private Long userId;

    @Persistent
    private Blob content;
    
    public UserInfoImage(Long id, Blob content) {
        this.userId = id;
        this.content = content;
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }
}
