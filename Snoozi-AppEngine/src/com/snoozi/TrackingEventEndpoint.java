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

@Api(name = "trackingeventendpoint",version="v3", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class TrackingEventEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listTrackingEvent")
	public CollectionResponse<TrackingEvent> listTrackingEvent(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<TrackingEvent> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(TrackingEvent.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<TrackingEvent>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (TrackingEvent obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<TrackingEvent> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getTrackingEvent")
	public TrackingEvent getTrackingEvent(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		TrackingEvent trackingevent = null;
		try {
			trackingevent = mgr.getObjectById(TrackingEvent.class, id);
		} finally {
			mgr.close();
		}
		return trackingevent;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param trackingevent the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertTrackingEvent")
	public TrackingEvent insertTrackingEvent(TrackingEvent trackingevent) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsTrackingEvent(trackingevent)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(trackingevent);
		} finally {
			mgr.close();
		}
		return trackingevent;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param trackingevent the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateTrackingEvent")
	public TrackingEvent updateTrackingEvent(TrackingEvent trackingevent) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsTrackingEvent(trackingevent)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(trackingevent);
		} finally {
			mgr.close();
		}
		return trackingevent;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeTrackingEvent")
	public void removeTrackingEvent(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			
			TrackingEvent trackingevent = mgr.getObjectById(
					TrackingEvent.class, id);
			mgr.deletePersistent(trackingevent);
		} finally {
			mgr.close();
		}
	}	
	
	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "removeErrorLoggerEvent")
	public void removeErrorLoggerEvent() {
		PersistenceManager mgr = getPersistenceManager();
		Query query = null;
		
		
		try {
			/* Via query function
			query = mgr.newQuery(TrackingEvent.class);
			query.setFilter("type == tracktype");
			query.setRange(0, 200);
			query.setOrdering("timestamp desc");*/
			
			/* Via query filter 
			 Query.Builder query = Query.newBuilder();
			query.addKindBuilder().setName("com.snoozi.TrackingEvent");
			query.setFilter(makeFilter(
			    makeFilter("type", PropertyFilter.Operator.EQUAL, makeValue("ERROR_LOGGER")).build(),
			    makeFilter("otherfilter", PropertyFilter.Operator.LESS_THAN, makeValue(63)).build()));
			query.addOrder(makeOrder("timestamp", PropertyOrder.Direction.DESCENDING));
			query.execute()... */
			
			/* Via query string */
			 query = mgr.newQuery("select from com.snoozi.TrackingEvent " +
	                " where type == tracktype " +
					 " RANGE 0,200");
				
			query.declareParameters("String tracktype");

			List<TrackingEvent> results = (List<TrackingEvent>) query.execute("ERROR_LOGGER");
			if (!results.isEmpty()) {
			    for (TrackingEvent p : results) {
			    	mgr.deletePersistent(p);
			    }
			  }
		} finally {
			query.closeAll();
			mgr.close();
		}
	}

	private boolean containsTrackingEvent(TrackingEvent trackingevent) {
		if(trackingevent.getKey() == null)
			return false;
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(TrackingEvent.class, trackingevent.getKey());
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
