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
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "alarmendpoint",version="v4", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class AlarmEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listAlarm")
	public CollectionResponse<Alarm> listAlarm(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Alarm> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Alarm.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Alarm>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Alarm obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Alarm> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getAlarm")
	public Alarm getAlarm(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Alarm alarm = null;
		try {
			alarm = mgr.getObjectById(Alarm.class, id);
		} finally {
			mgr.close();
		}
		return alarm;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param alarm the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertAlarm")
	public Alarm insertAlarm(Alarm alarm) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsAlarm(alarm)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(alarm);
		} finally {
			mgr.close();
		}
		return alarm;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param alarm the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateAlarm")
	public Alarm updateAlarm(Alarm alarm) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsAlarm(alarm)) {
				return insertAlarm(alarm);
			}
			mgr.makePersistent(alarm);
		} finally {
			mgr.close();
		}
		return alarm;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeAlarm")
	public void removeAlarm(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			Alarm alarm = mgr.getObjectById(Alarm.class, id);
			mgr.deletePersistent(alarm);
		} finally {
			mgr.close();
		}
	}

	private boolean containsAlarm(Alarm alarm) {
		if(alarm.getId() ==null)
			return false;
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Alarm.class, alarm.getId());
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
