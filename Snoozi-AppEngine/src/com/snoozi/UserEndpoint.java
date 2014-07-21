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

@Api(name = "userendpoint",version="v3", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class UserEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listUser")
	public CollectionResponse<User> listUser(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<User> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(User.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<User>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (User obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<User> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getUser")
	public User getUser(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		User user = null;
		try {
			user = mgr.getObjectById(User.class, id);
		} finally {
			mgr.close();
		}
		return user;
	}
	
	/**
	 * This method gets the entity having the email. It uses HTTP GET method.
	 *
	 * @param email of the java bean.
	 * @return The entity with email.
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "getUserByEmail", path="getUserByEmail",httpMethod="GET")
	public User getUserByEmail(@Named("email") String email) {
		PersistenceManager mgr = null;
		List<User> execute = null;
		User usr = null;
		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(User.class);
			query.setFilter("email == userEmail");
			query.declareParameters("String userEmail");
			query.setRange(0, 1);
					
			
			execute = (List<User>) query.execute(email);
			
			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (User obj : execute)
				usr = obj;
		} finally {
			mgr.close();
		}

		return usr;
	}

	
	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param user the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertUser")
	public User insertUser(User user) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (containsUser(user)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.makePersistent(user);
		} finally {
			mgr.close();
		}
		return user;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param user the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateUser")
	public User updateUser(User user) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsUser(user)) {
				return insertUser(user);
			}
			mgr.makePersistent(user);
		} finally {
			mgr.close();
		}
		return user;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeUser")
	public void removeUser(@Named("id") String id) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			User user = mgr.getObjectById(User.class, id);
			mgr.deletePersistent(user);
		} finally {
			mgr.close();
		}
	}

	
	
	
	@ApiMethod(name = "addUserview", path="addUserview",httpMethod="PUT")
	public User addUserview(@Named("id") Long id, @Named("viewcount") int viewcount) {
		PersistenceManager mgr = getPersistenceManager();
		User user = null;
		try {
			
			user = mgr.getObjectById(User.class, id);
			if(user != null)
			{
				if(viewcount != 0)
					user.setViewcount(user.getViewcount() + viewcount);
				
				mgr.makePersistent(user);
			}
		} finally {
			mgr.close();
		}
		return user;
	}	
	
	
	@ApiMethod(name = "wakeupUser", path="wakeupUser",httpMethod="PUT")
	public User wakeupUser(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		User user = null;
		try {
			
			user = mgr.getObjectById(User.class, id);
			if(user != null)
			{
				user.setWakeupcount(user.getWakeupcount() + 1);
				
				mgr.makePersistent(user);
			}
		} finally {
			mgr.close();
		}
		return user;
	}	
	
	
	
	private boolean containsUser(User user) {
		if(user.getId() ==null)
			return false;
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(User.class, user.getId());
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
