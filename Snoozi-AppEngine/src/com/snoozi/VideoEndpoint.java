package com.snoozi;

import com.snoozi.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.DefaultValue;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "videoendpoint",version="v2", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class VideoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listVideo")
	public CollectionResponse<Video> listVideo(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Video> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Video.class);
			query.setOrdering("timestamp desc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}
			if (limit != null) {
				query.setRange(0, limit);
			}
			execute = (List<Video>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Video obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Video> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	
	

	
	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getVideo")
	public Video getVideo(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Video video = null;
		try {
			video = mgr.getObjectById(Video.class, id);
		} finally {
			mgr.close();
		}
		return video;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param video the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertVideo")
	public Video insertVideo(Video video) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsVideo(video)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(video);
		} finally {
			mgr.close();
		}
		return video;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param video the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateVideo")
	public Video updateVideo(Video video) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsVideo(video)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(video);
		} finally {
			mgr.close();
		}
		return video;
	}
	

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeVideo")
	public void removeVideo(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Video video = mgr.getObjectById(Video.class, id);
			mgr.deletePersistent(video);
		} finally {
			mgr.close();
		}
	}

	private boolean containsVideo(Video video) {
		if(video.getId() ==null)
			return false;
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Video.class, video.getId());
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

	
	
	
	/*
	 ****************** CUSTOM METHOD & PATH ******************
	 */
	

	/**
	 * Get all recent video posted
	 * @param cursorString  point to start	
	 * @param limit record count
	 * @param status status of type VIDEO_STATUS (OK, REPORT,DELETE...)
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listRecentVideo", path="listRecentVideo")
	public CollectionResponse<Video> listRecentVideo(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("status") String status) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Video> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Video.class);
			query.setFilter("status ==  statusParam");
			query.declareParameters("String statusParam");
			
			
			query.setOrdering("timestamp desc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}
			
			if(status == null)
				status = "OK";

			execute = (List<Video>) query.execute(status);
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Video obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Video> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	

	/**
	 * Get all recent video posted
	 * @param cursorString  point to start	
	 * @param limit record count
	 * @param status status of type VIDEO_STATUS (OK, REPORT,DELETE...)
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "getVideosFromUser", path="getVideosFromUser")
	public CollectionResponse<Video> getVideosFromUser(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("userid") Long userid,
			@Nullable @Named("fromstamp") Long fromstamp,
			@Nullable @Named("status") String status) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Video> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Video.class);
			query.setFilter("userid == useridParam && status ==  statusParam && timestamp > timeParam");
			query.declareParameters("Long useridParam, String statusParam, Long timeParam");
			
			
			query.setOrdering("timestamp asc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				if(limit > 30)
					limit = 30;
				query.setRange(0, limit);
			}else
				query.setRange(0, 5);
					
			
			if(status == null)
				status = "OK";

			execute = (List<Video>) query.execute(userid, status,fromstamp);
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Video obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Video> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	
	
	
	
	/**
	 * This method is used for adding a like to an existing entity. 
	 * It uses HTTP PUT method.
	 *
	 * @param id the id of the entity
	 * @param likevalue positive or negative value of the like
	 * @return The updated entity.
	 */
	@ApiMethod(name = "rateVideo", path="rateVideo",httpMethod="PUT")
	public Video rateVideo(@Named("id") Long id, @Named("viewcount") int viewcount, @Named("likevalue") int likevalue) {
		PersistenceManager mgr = getPersistenceManager();
		Video video = null;
		try {
			
			video = mgr.getObjectById(Video.class, id);
			if(video != null)
			{
				if(likevalue != 0)
					video.setLike(video.getLike() + likevalue);
				if(viewcount != 0)
					video.setViewcount(video.getViewcount() + viewcount);
				
				mgr.makePersistent(video);
			}
		} finally {
			mgr.close();
		}
		return video;
	}	
	
}
