package com.snoozi;

import com.snoozi.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "commentsendpoint",version="v4", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class CommentsEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listComments")
	public CollectionResponse<Comments> listComments(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Comments> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Comments.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Comments>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Comments obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Comments> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getComments")
	public Comments getComments(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Comments comments = null;
		try {
			comments = mgr.getObjectById(Comments.class, id);
		} finally {
			mgr.close();
		}
		return comments;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param comments the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertComments")
	public Comments insertComments(Comments comments) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsComments(comments)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(comments);
		} finally {
			mgr.close();
		}
		return comments;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param comments the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateComments")
	public Comments updateComments(Comments comments) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsComments(comments)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(comments);
		} finally {
			mgr.close();
		}
		return comments;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeComments")
	public void removeComments(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Comments comments = mgr.getObjectById(Comments.class, id);
			mgr.deletePersistent(comments);
		} finally {
			mgr.close();
		}
	}
	
	

	private boolean containsComments(Comments comments) {
		if(comments.getId() ==null)
			return false;
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Comments.class, comments.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
