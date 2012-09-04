/**
 * 
 */
package com.kiwi.bubble.appengine.server.bubbledata;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Blob;
import com.kiwi.bubble.appengine.server.ModelBase;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class BubbleDataImage extends ModelBase {
	@Persistent
	private Long bubbleId;

	@Persistent
	private Blob content;

	public BubbleDataImage(Long id, Blob content) {
		this.bubbleId = id;
		this.content = content;
	}

	public Long getBubbleId() {
		return bubbleId;
	}

	public void setBubbleId(Long bubbleId) {
		this.bubbleId = bubbleId;
	}

	public Blob getContent() {
		return content;
	}

	public void setContent(Blob content) {
		this.content = content;
	}
}
